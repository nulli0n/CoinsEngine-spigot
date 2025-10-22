package su.nightexpress.coinsengine.command.plugin;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandArguments;
import su.nightexpress.coinsengine.command.CommandNames;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.coinsengine.currency.operation.NotificationTarget;
import su.nightexpress.coinsengine.currency.operation.OperationContext;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.Lists;

import java.util.HashSet;
import java.util.Set;

public class PluginCommands extends SimpleManager<CoinsEnginePlugin> {

    private final CurrencyRegistry currencyRegistry;
    private final CurrencyManager  currencyManager;

    private final Set<CommandProvider> providers;
    private final Set<NightCommand>    commands;

    public PluginCommands(@NotNull CoinsEnginePlugin plugin, @NotNull CurrencyRegistry currencyRegistry, @NotNull CurrencyManager currencyManager) {
        super(plugin);
        this.currencyRegistry = currencyRegistry;
        this.currencyManager = currencyManager;
        this.providers = new HashSet<>();
        this.commands = new HashSet<>();
    }

    @Override
    protected void onLoad() {
        this.loadAdminCommands();
        this.loadGlobalCommands();
    }

    @Override
    protected void onShutdown() {
        this.commands.forEach(NightCommand::unregister);
        this.commands.clear();
    }

    public void registerProvider(@NotNull CommandProvider provider) {
        this.providers.add(provider);
    }

    private void loadAdminCommands() {
        this.registerCommand(NightCommand.forPlugin(this.plugin, builder -> {
            builder.branch(Commands.literal(CommandNames.ADMIN_RELOAD)
                .description(CoreLang.COMMAND_RELOAD_DESC)
                .permission(Perms.COMMAND_RELOAD)
                .executes((context, arguments) -> {
                    this.plugin.doReload(context.getSender());
                    return true;
                })
            );

            builder.branch(Commands.literal(CommandNames.ADMIN_CREATE)
                .permission(Perms.COMMAND_CREATE)
                .description(Lang.COMMAND_CREATE_DESC)
                .withArguments(
                    Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME),
                    Arguments.string(CommandArguments.SYMBOL).localized(Lang.COMMAND_ARGUMENT_NAME_SYMBOL),
                    Arguments.bool(CommandArguments.DECIMALS).localized(Lang.COMMAND_ARGUMENT_NAME_DECIMAL).optional().suggestions((reader, context) -> Lists.newList("true", "false"))
                )
                .executes(this::createCurrency)
            );

            builder.branch(Commands.literal(CommandNames.ADMIN_RESET)
                .permission(Perms.COMMAND_RESET)
                .description(Lang.COMMAND_RESET_DESC)
                .withArguments(
                    Arguments.playerName(CommandArguments.PLAYER),
                    CommandArguments.currency(this.currencyRegistry)
                )
                .withFlags(CommandArguments.FLAG_SILENT, CommandArguments.FLAG_SILENT_FEEDBACK)
                .executes(this::reset)
            );

            builder.branch(Commands.literal(CommandNames.ADMIN_RESET_ALL)
                .permission(Perms.COMMAND_RESET_ALL)
                .description(Lang.COMMAND_RESET_ALL_DESC)
                .withArguments(CommandArguments.currency(this.currencyRegistry).optional())
                .executes(this::resetAll)
            );

            this.providers.forEach(provider -> provider.build(builder));
        }));
    }

    private void loadGlobalCommands() {
        if (Config.isWalletEnabled()) {
            this.registerCommand(NightCommand.literal(this.plugin, Config.WALLET_ALIASES.get(), builder -> builder
                .description(Lang.COMMAND_WALLET_DESC)
                .permission(Perms.COMMAND_WALLET)
                .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_WALLET_OTHERS).optional())
                .executes(this::showWallet)
            ));
        }
    }

    private void registerCommand(@NotNull NightCommand command) {
        if (command.register()) {
            this.commands.add(command);
        }
    }

    private boolean createCurrency(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getString(CommandArguments.NAME);
        String symbol = arguments.getString(CommandArguments.SYMBOL);
        boolean decimals = arguments.getBoolean(CommandArguments.DECIMALS, true);

        return this.currencyManager.createCurrency(context.getSender(), name, symbol, decimals);
    }

    private boolean showWallet(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getString(CommandArguments.PLAYER, context.getSender().getName());
        this.currencyManager.showWallet(context.getSender(), name);
        return true;
    }

    // TODO Move in currency commands
    private boolean reset(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Currency currency = arguments.get(CommandArguments.CURRENCY, Currency.class);

        this.plugin.getUserManager().manageUser(arguments.getString(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            OperationContext operationContext = OperationContext.of(context.getSender())
                .silentFor(NotificationTarget.USER, context.hasFlag(CommandArguments.FLAG_SILENT))
                .silentFor(NotificationTarget.EXECUTOR, context.hasFlag(CommandArguments.FLAG_SILENT_FEEDBACK));

            this.currencyManager.reset(operationContext, user, currency);
        });
        return true;
    }

    private boolean resetAll(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (arguments.contains(CommandArguments.CURRENCY)) {
            Currency currency = arguments.get(CommandArguments.CURRENCY, Currency.class);
            this.currencyManager.resetBalances(context.getSender(), currency);
        }
        else {
            this.currencyManager.resetBalances(context.getSender());
        }
        return true;
    }
}
