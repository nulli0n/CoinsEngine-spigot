package su.nightexpress.coinsengine.command.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.api.event.SentBalanceEvent;
import su.nightexpress.coinsengine.command.CommandArguments;
import su.nightexpress.coinsengine.command.CommandFlags;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencySettings;
import su.nightexpress.coinsengine.util.TopEntry;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.RootCommand;
import su.nightexpress.nightcore.command.experimental.ServerCommand;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.CommandNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CurrencyCommands {

    public static void loadForCurrency(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
        var command = RootCommand.chained(plugin, currency.getCommandAliases(), builder -> builder
            .permission(currency.isPermissionRequired() ? currency.getPermission() : null)
            .description(currency.replacePlaceholders().apply(Lang.COMMAND_CURRENCY_ROOT_DESC.getString()))
        );

        ChainedNode rootNode = command.getNode();

        CommandNode balanceNode = balanceBuilder(plugin, currency, "balance").build();

        if (Config.CURRENCY_COMMAND_DEFAULT_TO_BALANCE.get()) {
            rootNode.setFallback(balanceNode);
        }
        rootNode.addChildren(balanceNode);
        rootNode.addChildren(topBuilder(plugin, currency, "top"));

        rootNode.addChildren(DirectNode.builder(plugin, "giveall")
            .permission(Perms.COMMAND_CURRENCY_GIVE_ALL)
            .description(Lang.COMMAND_CURRENCY_GIVE_ALL_DESC)
            .withArgument(CommandArguments.amount().required())
            .withFlag(CommandFlags.silent())
            .withFlag(CommandFlags.silentOutput())
            .executes((context, arguments) -> giveAll(plugin, currency, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "give")
            .permission(Perms.COMMAND_CURRENCY_GIVE)
            .description(Lang.COMMAND_CURRENCY_GIVE_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.amount().required())
            .withFlag(CommandFlags.silent())
            .withFlag(CommandFlags.silentOutput())
            .executes((context, arguments) -> give(plugin, currency, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "set")
            .permission(Perms.COMMAND_CURRENCY_SET)
            .description(Lang.COMMAND_CURRENCY_SET_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.amount().required())
            .withFlag(CommandFlags.silent())
            .withFlag(CommandFlags.silentOutput())
            .executes((context, arguments) -> set(plugin, currency, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "take")
            .permission(Perms.COMMAND_CURRENCY_TAKE)
            .description(Lang.COMMAND_CURRENCY_TAKE_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.amount().required())
            .withFlag(CommandFlags.silent())
            .withFlag(CommandFlags.silentOutput())
            .executes((context, arguments) -> remove(plugin, currency, context, arguments))
        );

        if (currency.isTransferAllowed()) {
            rootNode.addChildren(payBuilder(plugin, currency, "pay", "send"));
            rootNode.addChildren(DirectNode.builder(plugin, "payments")
                .permission(Perms.COMMAND_CURRENCY_PAYMENTS)
                .description(Lang.COMMAND_CURRENCY_PAYMENTS_DESC)
                .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_CURRENCY_PAYMENTS_OTHERS))
                .withFlag(CommandFlags.silent().permission(Perms.COMMAND_CURRENCY_PAYMENTS_OTHERS))
                .executes((context, arguments) -> togglePayments(plugin, currency, context, arguments))
            );
        }

        if (currency.isExchangeAllowed()) {
            rootNode.addChildren(DirectNode.builder(plugin, "exchange")
                .playerOnly()
                .permission(Perms.COMMAND_CURRENCY_EXCHANGE)
                .description(Lang.COMMAND_CURRENCY_EXCHANGE_DESC)
                .withArgument(CommandArguments.currency(plugin).required()
                    .withSamples(context -> plugin.getCurrencyManager().getCurrencies().stream()
                        .filter(other -> currency.getExchangeRate(other) > 0).map(Currency::getId).toList()
                    )
                )
                .withArgument(CommandArguments.amount().required())
                .executes((context, arguments) -> exchange(plugin, currency, context, arguments))
            );
        }

        plugin.getCommandManager().registerCommand(command);
    }

    public static void loadForEconomy(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
        ServerCommand balanceCommand = RootCommand.build(plugin, balanceBuilder(plugin, currency, "balance", "bal"));
        ServerCommand payCommand = RootCommand.build(plugin, payBuilder(plugin, currency, "pay"));
        ServerCommand topCommand = RootCommand.build(plugin, topBuilder(plugin, currency, "balancetop", "baltop"));

        plugin.getCommandManager().registerCommand(balanceCommand);
        plugin.getCommandManager().registerCommand(payCommand);
        plugin.getCommandManager().registerCommand(topCommand);
    }

    public static void unloadForCurrency(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
        plugin.getCommandManager().unregisterServerCommand(currency.getCommandAliases()[0]);
    }

    public static void unloadForEconomy(@NotNull CoinsEnginePlugin plugin) {
        plugin.getCommandManager().unregisterServerCommand("balance");
        plugin.getCommandManager().unregisterServerCommand("pay");
        plugin.getCommandManager().unregisterServerCommand("balancetop");
    }


    private static DirectNodeBuilder balanceBuilder(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull String... aliases) {
        return DirectNode.builder(plugin, aliases)
            .permission(Perms.COMMAND_CURRENCY_BALANCE)
            .description(Lang.COMMAND_CURRENCY_BALANCE_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_CURRENCY_BALANCE_OTHERS))
            .executes((context, arguments) -> showBalance(plugin, currency, context, arguments));
    }

    private static DirectNodeBuilder payBuilder(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull String... aliases) {
        return DirectNode.builder(plugin, aliases)
            .playerOnly()
            .permission(Perms.COMMAND_CURRENCY_SEND)
            .description(Lang.COMMAND_CURRENCY_SEND_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.amount().required())
            .executes((context, arguments) -> send(plugin, currency, context, arguments));
    }

    private static DirectNodeBuilder topBuilder(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull String... aliases) {
        return DirectNode.builder(plugin, aliases)
            .permission(Perms.COMMAND_CURRENCY_TOP)
            .description(Lang.COMMAND_CURRENCY_TOP_DESC)
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .localized(Lang.COMMAND_ARGUMENT_NAME_PAGE)
                .withSamples(context -> IntStream.range(1, 11).boxed().map(String::valueOf).toList())
            )
            .executes((context, arguments) -> showTop(plugin, currency, context, arguments));
    }

    public static boolean showBalance(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());
        boolean isOwn = !arguments.hasArgument(CommandArguments.PLAYER);

        plugin.getUserManager().manageUser(name, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            currency.withPrefix((isOwn ? Lang.CURRENCY_BALANCE_DISPLAY_OWN : Lang.CURRENCY_BALANCE_DISPLAY_OTHERS).getMessage()).send(context.getSender(), replacer -> replacer
                .replace(currency.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
            );
        });
        return true;
    }

    public static boolean exchange(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Currency targetCurrency = arguments.getArgument(CommandArguments.CURRENCY, Currency.class);

        double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (amount <= 0) return false;

        Player player = context.getPlayerOrThrow();
        return plugin.getCurrencyManager().exchange(player, currency, targetCurrency, amount);
    }

    public static boolean giveAll(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (amount <= 0D) return false;

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            CoinsUser user = plugin.getUserManager().getOrFetch(player);

            user.addBalance(currency, amount);

            plugin.getUserManager().save(user);
            plugin.getCoinsLogger().logGive(user, currency, amount, context.getSender());

            if (!arguments.hasFlag(CommandFlags.SILENT)) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_GIVE_NOTIFY.getMessage()).send(player, replacer -> replacer
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            }
        });

        if (!arguments.hasFlag(CommandFlags.SILENT_FEEDBACK)) {
            currency.withPrefix(Lang.COMMAND_CURRENCY_GIVE_ALL_DONE.getMessage()).send(context.getSender(), replacer -> replacer
                .replace(currency.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
            );
        }

        return true;
    }

    public static boolean give(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (amount <= 0D) return false;

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            user.addBalance(currency, amount);

            plugin.getUserManager().save(user);
            plugin.getCoinsLogger().logGive(user, currency, amount, context.getSender());

            if (!arguments.hasFlag(CommandFlags.SILENT_FEEDBACK)) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_GIVE_DONE.getMessage()).send(context.getSender(), replacer -> replacer
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            }

            Player target = user.getPlayer();
            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_GIVE_NOTIFY.getMessage()).send(target, replacer -> replacer
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            }
        });

        return true;
    }

    public static boolean togglePayments(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        boolean isOwn = !arguments.hasArgument(CommandArguments.PLAYER);
        String name = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());

        plugin.getUserManager().manageUser(name, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            CurrencySettings settings = user.getSettings(currency);
            settings.setPaymentsEnabled(!settings.isPaymentsEnabled());
            plugin.getUserManager().save(user);

            if (!isOwn) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_PAYMENTS_TARGET.getMessage()).send(context.getSender(), replacer -> replacer
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(settings.isPaymentsEnabled()))
                );
            }

            Player target = user.getPlayer();
            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_PAYMENTS_TOGGLE.getMessage()).send(target, replacer -> replacer
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(settings.isPaymentsEnabled()))
                );
            }
        });
        return true;
    }

    public static boolean send(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player from = context.getPlayerOrThrow();
        String targetName = arguments.getStringArgument(CommandArguments.PLAYER);
        if (from.getName().equalsIgnoreCase(targetName)) {
            Lang.ERROR_COMMAND_NOT_YOURSELF.getMessage().send(context.getSender());
            return false;
        }

        double amount = currency.fine(arguments.getDoubleArgument(CommandArguments.AMOUNT));
        if (amount <= 0D) return false;

        if (currency.getMinTransferAmount() > 0 && amount < currency.getMinTransferAmount()) {
            currency.withPrefix(Lang.COMMAND_CURRENCY_SEND_ERROR_TOO_LOW.getMessage()).send(context.getSender(), replacer -> replacer
                .replace(Placeholders.GENERIC_AMOUNT, currency.format(currency.getMinTransferAmount()))
            );
            return false;
        }

        plugin.getUserManager().manageUser(targetName, targetUser -> {
            if (targetUser == null) {
                context.errorBadPlayer();
                return;
            }

            CoinsUser fromUser = plugin.getUserManager().getOrFetch(from);
            if (amount > fromUser.getBalance(currency)) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_SEND_ERROR_NOT_ENOUGH.getMessage()).send(from, replacer -> replacer
                    .replace(currency.replacePlaceholders())
                );
                return;
            }

            CurrencySettings settings = targetUser.getSettings(currency);
            if (!settings.isPaymentsEnabled()) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_SEND_ERROR_NO_PAYMENTS.getMessage()).send(from, replacer -> replacer
                    .replace(Placeholders.PLAYER_NAME, targetUser.getName())
                    .replace(currency.replacePlaceholders())
                );
                return;
            }

            targetUser.addBalance(currency, amount);
            fromUser.removeBalance(currency, amount);

            plugin.getUserManager().save(targetUser);
            plugin.getUserManager().save(fromUser);
            plugin.getCoinsLogger().logSend(targetUser, currency, amount, from);

            SentBalanceEvent event = new SentBalanceEvent(fromUser, targetUser, currency, amount);
            plugin.getServer().getPluginManager().callEvent(event);

            currency.withPrefix(Lang.COMMAND_CURRENCY_SEND_DONE_SENDER.getMessage()).send(context.getSender(), replacer -> replacer
                .replace(currency.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                .replace(Placeholders.GENERIC_BALANCE, fromUser.getBalance(currency))
                .replace(Placeholders.PLAYER_NAME, targetUser.getName())
            );

            Player target = plugin.getServer().getPlayer(targetUser.getName());
            if (target != null) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_SEND_DONE_NOTIFY.getMessage()).send(target, replacer -> replacer
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, targetUser.getBalance(currency))
                    .replace(Placeholders.PLAYER_NAME, fromUser.getName())
                );
            }
        });
        return true;
    }

    public static boolean set(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (amount < 0D) return false;

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            user.setBalance(currency, amount);

            plugin.getUserManager().save(user);
            plugin.getCoinsLogger().logSet(user, currency, amount, context.getSender());

            if (!arguments.hasFlag(CommandFlags.SILENT_FEEDBACK)) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_SET_DONE.getMessage()).send(context.getSender(), replacer -> replacer
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            }

            Player target = user.getPlayer();

            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_SET_NOTIFY.getMessage()).send(target, replacer -> replacer
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            }
        });
        return true;
    }

    public static boolean remove(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (amount <= 0D) return false;

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            user.removeBalance(currency, amount);

            plugin.getUserManager().save(user);
            plugin.getCoinsLogger().logTake(user, currency, amount, context.getSender());

            if (!arguments.hasFlag(CommandFlags.SILENT_FEEDBACK)) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_TAKE_DONE.getMessage()).send(context.getSender(), replacer -> replacer
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            }

            Player target = user.getPlayer();
            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                currency.withPrefix(Lang.COMMAND_CURRENCY_TAKE_NOTIFY.getMessage()).send(target, replacer -> replacer
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            }
        });
        return true;
    }

    public static boolean showTop(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        int perPage = Config.TOP_ENTRIES_PER_PAGE.get();

        List<TopEntry> full = plugin.getCurrencyManager().getTopBalances(currency);
        List<List<TopEntry>> split = Lists.split(full, perPage);
        int pages = split.size();
        int page = Math.max(0, Math.min(pages, Math.abs(arguments.getIntArgument(CommandArguments.AMOUNT, 1))) - 1);

        List<TopEntry> entries = pages > 0 ? split.get(page) : new ArrayList<>();

        currency.withPrefix(Lang.COMMAND_CURRENCY_TOP_LIST.getMessage()).send(context.getSender(), replacer -> replacer
            .replace(currency.replacePlaceholders())
            .replace(Placeholders.GENERIC_CURRENT, page + 1)
            .replace(Placeholders.GENERIC_MAX, pages)
            .replace(Placeholders.GENERIC_ENTRY, list -> {
                for (TopEntry entry : entries) {
                    list.add(Lang.COMMAND_CURRENCY_TOP_ENTRY.getString()
                        .replace(Placeholders.GENERIC_POS, NumberUtil.format(entry.position()))
                        .replace(Placeholders.GENERIC_BALANCE, currency.format(entry.balance()))
                        .replace(Placeholders.PLAYER_NAME, entry.name()));
                }
            })
        );

        return true;
    }
}
