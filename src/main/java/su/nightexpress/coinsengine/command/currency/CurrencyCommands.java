package su.nightexpress.coinsengine.command.currency;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandArguments;
import su.nightexpress.coinsengine.command.CommandFlags;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.currency.CurrencyOperations;
import su.nightexpress.coinsengine.currency.operation.ConsoleOperation;
import su.nightexpress.coinsengine.currency.operation.OperationProvider;
import su.nightexpress.coinsengine.currency.operation.impl.AddOperation;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.RootCommand;
import su.nightexpress.nightcore.command.experimental.ServerCommand;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class CurrencyCommands {

    private static final String FILE = FileConfig.withExtension("commands");

    public static final String DEFAULT_TOP_ALIAS = "top";

    private static final Map<String, CommandData>     DEFAULTS_MAP = new LinkedHashMap<>();
    private static final Map<String, CommandData>     DATA_MAP     = new HashMap<>();
    private static final Map<String, CommandProvider> PROVIDER_MAP = new HashMap<>();

    private static CoinsEnginePlugin plugin;
    private static Set<ServerCommand> economyCommands;

    public static void load(@NotNull CoinsEnginePlugin instance) {
        plugin = instance;
        economyCommands = new HashSet<>();

        FileConfig config = FileConfig.loadOrExtract(plugin, FILE);

        loadDefaults();
        loadCommandData(config);

        config.saveChanges();
    }

    public static void clear() {
        DEFAULTS_MAP.clear();
        DATA_MAP.clear();
        PROVIDER_MAP.clear();
        economyCommands.clear();
        plugin = null;
    }

    private static void loadDefaults() {
        addDefault("balance", CommandVariant.enabled("balance"), CommandVariant.enabled("balance", "bal"), true, CurrencyCommands::buildBalance);
        addDefault("pay", CommandVariant.enabled("pay", "send"), CommandVariant.enabled("pay"), CurrencyCommands::buildPay, Currency::isTransferAllowed);
        addDefault("payments", CommandVariant.enabled("payments"), CommandVariant.enabled("paytoggle"), CurrencyCommands::buildPayments, Currency::isTransferAllowed);

        if (Config.isTopsEnabled()) {
            addDefault("top", CommandVariant.enabled(DEFAULT_TOP_ALIAS), CommandVariant.enabled("balancetop", "baltop"), CurrencyCommands::buildTop, Currency::isLeaderboardEnabled);
        }

        addDefault("add", CommandVariant.enabled("give"), CommandVariant.disabled("addmoney"), CurrencyCommands::buildGive);
        addDefault("addall", CommandVariant.enabled("giveall"), CommandVariant.disabled("addmoneyall"), CurrencyCommands::buildGiveAll);
        addDefault("set", CommandVariant.enabled("set"), CommandVariant.disabled("setmoney"), CurrencyCommands::buildSet);
        addDefault("remove", CommandVariant.enabled("take", "remove"), CommandVariant.disabled("takemoney"), CurrencyCommands::buildRemove);

        addDefault("exchange", CommandVariant.enabled("exchange"), CommandVariant.disabled("exchange"), CurrencyCommands::buildExchange, Currency::isExchangeAllowed);
    }

    private static void loadCommandData(@NotNull FileConfig config) {
        DEFAULTS_MAP.forEach((id, data) -> {
            String path = "Commands." + id;

            if (!config.contains(path)) {
                config.set(path, data);
            }

            CommandData fresh = CommandData.read(config, path);
            DATA_MAP.put(id, fresh);
        });
    }

    private static void addDefault(@NotNull String id, @NotNull CommandVariant children, @NotNull CommandVariant dedicated, @NotNull CommandBuilder provider) {
        addDefault(id, children, dedicated, false, provider);
    }

    private static void addDefault(@NotNull String id, @NotNull CommandVariant children, @NotNull CommandVariant dedicated, boolean fallback, @NotNull CommandBuilder provider) {
        addDefault(id, children, dedicated, fallback, provider, null);
    }

    private static void addDefault(@NotNull String id,
                                   @NotNull CommandVariant children,
                                   @NotNull CommandVariant dedicated,
                                   @NotNull CommandBuilder provider,
                                   @Nullable Predicate<Currency> predicate) {
        addDefault(id, children, dedicated, false, provider, predicate);
    }

    private static void addDefault(@NotNull String id,
                                   @NotNull CommandVariant children,
                                   @NotNull CommandVariant dedicated,
                                   boolean fallback,
                                   @NotNull CommandBuilder provider,
                                   @Nullable Predicate<Currency> predicate) {
        DEFAULTS_MAP.put(id, new CommandData(children, dedicated, fallback));
        PROVIDER_MAP.put(id, new CommandProvider(provider, predicate));
    }

    public static void loadCommands(@NotNull Currency currency, boolean isEconomy) {
        var command = RootCommand.chained(plugin, currency.getCommandAliases(), builder -> builder
            .permission(currency.isPermissionRequired() ? currency.getPermission() : null)
            .description(currency.replacePlaceholders().apply(Lang.COMMAND_CURRENCY_ROOT_DESC.getString()))
        );

        ChainedNode root = command.getNode();

        DATA_MAP.forEach((id, data) -> {
            CommandProvider provider = PROVIDER_MAP.get(id);
            if (provider == null) return;
            if (!provider.canProvide(currency)) return;

            if (isEconomy) {
                loadCommand(currency, data.getDedicatedVariant(), provider, builder -> {
                    economyCommands.add(RootCommand.build(plugin, builder));
                });
            }

            loadCommand(currency, data.getChildrenVariant(), provider, builder -> {
                DirectNode node = builder.build();
                root.addChildren(node);

                if (data.isFallback()) {
                    root.setFallback(node);
                }
            });
        });

        plugin.getCommandManager().registerCommand(command);
        economyCommands.forEach(serverCommand -> plugin.getCommandManager().registerCommand(serverCommand));
    }

    private static void loadCommand(@NotNull Currency currency, @NotNull CommandVariant variant, @NotNull CommandProvider provider, @NotNull Consumer<DirectNodeBuilder> consumer) {
        if (!variant.isEnabled()) return;

        DirectNodeBuilder builder = DirectNode.builder(plugin, variant.getAliases());
        provider.build(currency, builder);

        consumer.accept(builder);
    }

    public static void unloadForCurrency(@NotNull Currency currency) {
        plugin.getCommandManager().unregisterServerCommand(currency.getCommandAliases()[0]);
    }

    public static void unloadForEconomy() {
        economyCommands.forEach(serverCommand -> plugin.getCommandManager().unregisterCommand(serverCommand));
        economyCommands.clear();
    }

    private static void buildBalance(@NotNull Currency currency, @NotNull DirectNodeBuilder builder) {
        builder
            .permission(Perms.COMMAND_CURRENCY_BALANCE)
            .description(Lang.COMMAND_CURRENCY_BALANCE_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_CURRENCY_BALANCE_OTHERS))
            .executes((context, arguments) -> showBalance(currency, context, arguments));
    }

    private static void buildPay(@NotNull Currency currency, @NotNull DirectNodeBuilder builder) {
        builder
            .playerOnly()
            .permission(Perms.COMMAND_CURRENCY_SEND)
            .description(Lang.COMMAND_CURRENCY_SEND_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.amount().required())
            .executes((context, arguments) -> send(currency, context, arguments));
    }

    private static void buildTop(@NotNull Currency currency, @NotNull DirectNodeBuilder builder) {
        builder
            .permission(Perms.COMMAND_CURRENCY_TOP)
            .description(Lang.COMMAND_CURRENCY_TOP_DESC)
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .localized(Lang.COMMAND_ARGUMENT_NAME_PAGE)
                .withSamples(context -> IntStream.range(1, 11).boxed().map(String::valueOf).toList())
            )
            .executes((context, arguments) -> showTop(currency, context, arguments));
    }

    private static void buildGive(@NotNull Currency currency, @NotNull DirectNodeBuilder builder) {
        builder
            .permission(Perms.COMMAND_CURRENCY_GIVE)
            .description(Lang.COMMAND_CURRENCY_GIVE_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.amount().required())
            .withFlag(CommandFlags.silent())
            .withFlag(CommandFlags.silentOutput())
            .executes((context, arguments) -> editBalance(currency, CurrencyOperations::forAdd, context, arguments));
    }

    private static void buildGiveAll(@NotNull Currency currency, @NotNull DirectNodeBuilder builder) {
        builder
            .permission(Perms.COMMAND_CURRENCY_GIVE_ALL)
            .description(Lang.COMMAND_CURRENCY_GIVE_ALL_DESC)
            .withArgument(CommandArguments.amount().required())
            .withFlag(CommandFlags.silent())
            .withFlag(CommandFlags.silentOutput())
            .executes((context, arguments) -> giveAll(currency, context, arguments));
    }

    private static void buildSet(@NotNull Currency currency, @NotNull DirectNodeBuilder builder) {
        builder
            .permission(Perms.COMMAND_CURRENCY_SET)
            .description(Lang.COMMAND_CURRENCY_SET_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.amount().required())
            .withFlag(CommandFlags.silent())
            .withFlag(CommandFlags.silentOutput())
            .executes((context, arguments) -> editBalance(currency, CurrencyOperations::forSet, context, arguments));
    }

    private static void buildRemove(@NotNull Currency currency, @NotNull DirectNodeBuilder builder) {
        builder
            .permission(Perms.COMMAND_CURRENCY_TAKE)
            .description(Lang.COMMAND_CURRENCY_TAKE_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.amount().required())
            .withFlag(CommandFlags.silent())
            .withFlag(CommandFlags.silentOutput())
            .executes((context, arguments) -> editBalance(currency, CurrencyOperations::forRemove, context, arguments));
    }

    private static void buildPayments(@NotNull Currency currency, @NotNull DirectNodeBuilder builder) {
        builder
            .permission(Perms.COMMAND_CURRENCY_PAYMENTS)
            .description(Lang.COMMAND_CURRENCY_PAYMENTS_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_CURRENCY_PAYMENTS_OTHERS))
            .withFlag(CommandFlags.silent().permission(Perms.COMMAND_CURRENCY_PAYMENTS_OTHERS))
            .executes((context, arguments) -> togglePayments(currency, context, arguments));
    }

    private static void buildExchange(@NotNull Currency currency, @NotNull DirectNodeBuilder builder) {
        builder
            .playerOnly()
            .permission(Perms.COMMAND_CURRENCY_EXCHANGE)
            .description(Lang.COMMAND_CURRENCY_EXCHANGE_DESC)
            .withArgument(CommandArguments.currency(plugin).required()
                .withSamples(context -> plugin.getCurrencyManager().getCurrencies().stream().filter(currency::canExchangeTo).map(Currency::getId).toList())
            )
            .withArgument(CommandArguments.amount().required())
            .executes((context, arguments) -> exchange(currency, context, arguments));
    }

    private static boolean showBalance(@NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());
        plugin.getCurrencyManager().showBalance(context.getSender(), name, currency);
        return true;
    }

    private static boolean exchange(@NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        Currency targetCurrency = arguments.getArgument(CommandArguments.CURRENCY, Currency.class);
        double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);

        return plugin.getCurrencyManager().exchange(player, currency, targetCurrency, amount);
    }

    private static boolean giveAll(@NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (amount <= 0D) return false;

        CommandSender sender = context.getSender();

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            CoinsUser user = plugin.getUserManager().getOrFetch(player);

            AddOperation operation = CurrencyOperations.forAdd(currency, amount, user, sender);
            operation.setFeedback(false);
            operation.setNotify(!arguments.hasFlag(CommandFlags.SILENT));

            plugin.getCurrencyManager().performOperation(operation);
        });

        if (!arguments.hasFlag(CommandFlags.SILENT_FEEDBACK)) {
            currency.sendPrefixed(Lang.COMMAND_CURRENCY_GIVE_ALL_DONE, sender, replacer -> replacer
                .replace(currency.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
            );
        }

        return true;
    }

    private static boolean togglePayments(@NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());
        boolean silent = arguments.hasFlag(CommandFlags.SILENT);

        plugin.getCurrencyManager().togglePayments(context.getSender(), name, currency, silent);
        return true;
    }

    private static boolean send(@NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player from = context.getPlayerOrThrow();
        String targetName = arguments.getStringArgument(CommandArguments.PLAYER);
        double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);

        return plugin.getCurrencyManager().sendCurrency(from, targetName, currency, amount);
    }

    private static boolean showTop(@NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        int page = arguments.getIntArgument(CommandArguments.AMOUNT, 1);
        plugin.getTopManager().ifPresent(tops -> tops.showLeaderboard(context.getSender(), currency, page));
        return true;
    }

    private static <T extends ConsoleOperation<CommandSender>> boolean editBalance(@NotNull Currency currency,
                                                                                   @NotNull OperationProvider<T> provider,
                                                                                   @NotNull CommandContext context,
                                                                                   @NotNull ParsedArguments arguments) {
        double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (amount < 0D) return false;

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            T operation = provider.provide(currency, amount, user, context.getSender());
            operation.setFeedback(!arguments.hasFlag(CommandFlags.SILENT_FEEDBACK));
            operation.setNotify(!arguments.hasFlag(CommandFlags.SILENT));

            plugin.getCurrencyManager().performOperation(operation);
        });
        return true;
    }
}
