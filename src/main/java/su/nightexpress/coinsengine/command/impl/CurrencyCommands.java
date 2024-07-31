package su.nightexpress.coinsengine.command.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.api.event.*;
import su.nightexpress.coinsengine.command.CommandArguments;
import su.nightexpress.coinsengine.command.CommandFlags;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencySettings;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.RootCommand;
import su.nightexpress.nightcore.command.experimental.ServerCommand;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class CurrencyCommands {

    public static void loadForCurrency(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
        ServerCommand command = RootCommand.chained(plugin, currency.getCommandAliases(), builder -> {
            builder
                    .permission(currency.isPermissionRequired() ? currency.getPermission() : null)
                    .description(currency.replacePlaceholders().apply(Lang.COMMAND_CURRENCY_ROOT_DESC.getString()))
                    .child(balanceBuilder(plugin, currency, "balance"))
                    .addDirect("giveall", children -> children
                            .permission(Perms.COMMAND_CURRENCY_GIVE_ALL)
                            .description(Lang.COMMAND_CURRENCY_GIVE_ALL_DESC)
                            .withArgument(CommandArguments.amount().required())
                            .withFlag(CommandFlags.silent())
                            .executes((context, arguments) -> giveAll(plugin, currency, context, arguments))
                    )
                    .addDirect("give", children -> children
                            .permission(Perms.COMMAND_CURRENCY_GIVE)
                            .description(Lang.COMMAND_CURRENCY_GIVE_DESC)
                            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
                            .withArgument(CommandArguments.amount().required())
                            .withFlag(CommandFlags.silent())
                            .executes((context, arguments) -> give(plugin, currency, context, arguments))
                    )
                    .addDirect("set", children -> children
                            .permission(Perms.COMMAND_CURRENCY_SET)
                            .description(Lang.COMMAND_CURRENCY_SET_DESC)
                            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
                            .withArgument(CommandArguments.amount().required())
                            .withFlag(CommandFlags.silent())
                            .executes((context, arguments) -> set(plugin, currency, context, arguments))
                    )
                    .addDirect("take", children -> children
                            .permission(Perms.COMMAND_CURRENCY_TAKE)
                            .description(Lang.COMMAND_CURRENCY_TAKE_DESC)
                            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
                            .withArgument(CommandArguments.amount().required())
                            .withFlag(CommandFlags.silent())
                            .executes((context, arguments) -> remove(plugin, currency, context, arguments))
                    )
                    .child(topBuilder(plugin, currency, "top"));

            if (currency.isTransferAllowed()) {
                builder
                        .child(payBuilder(plugin, currency, "pay", "send"))
                        .addDirect("payments", children -> children
                                .permission(Perms.COMMAND_CURRENCY_PAYMENTS)
                                .description(Lang.COMMAND_CURRENCY_PAYMENTS_DESC)
                                .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_CURRENCY_PAYMENTS_OTHERS))
                                .withFlag(CommandFlags.silent().permission(Perms.COMMAND_CURRENCY_PAYMENTS_OTHERS))
                                .executes((context, arguments) -> togglePayments(plugin, currency, context, arguments))
                        );
            }

            if (currency.isExchangeAllowed()) {
                builder.addDirect("exchange", children -> children
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
        });

        plugin.getCommandManager().registerCommand(command);
    }

    // ... Altri metodi non modificati ...

    public static boolean exchange(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Currency targetCurrency = arguments.getArgument(CommandArguments.CURRENCY, Currency.class);
        final double initialAmount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (initialAmount <= 0) return false;

        Player player = context.getPlayerOrThrow();
        CoinsUser user = plugin.getUserManager().getUserData(player);

        double exchangeRate = currency.getExchangeRate(targetCurrency);
        final double initialResultAmount = initialAmount * exchangeRate;

        CoinsExchangeEvent event = new CoinsExchangeEvent(user.getPlayer(), currency, targetCurrency, initialAmount, initialResultAmount);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        final double finalAmount = event.getFromAmount();
        final double finalResultAmount = event.getToAmount();

        return plugin.getCurrencyManager().exchange(player, currency, targetCurrency, finalAmount);
    }

    public static boolean giveAll(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        final double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (amount <= 0D) return false;

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            CoinsUser user = plugin.getUserManager().getUserData(player);

            user.addBalance(currency, amount);

            plugin.getUserManager().scheduleSave(user);
            plugin.getCoinsLogger().logGive(user, currency, amount, context.getSender());

            if (!arguments.hasFlag(CommandFlags.SILENT)) {
                Lang.COMMAND_CURRENCY_GIVE_NOTIFY.getMessage()
                        .replace(currency.replacePlaceholders())
                        .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                        .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                        .send(player);
            }
        });

        Lang.COMMAND_CURRENCY_GIVE_ALL_DONE.getMessage()
                .replace(currency.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                .send(context.getSender());

        return true;
    }

    public static boolean give(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        final double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (amount <= 0D) return false;

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }


            CoinsGiveEvent event = new CoinsGiveEvent(Bukkit.getOfflinePlayer(user.getName()), currency, amount, context.getSender());
            plugin.getServer().getPluginManager().callEvent(event);


            user.addBalance(currency, amount);

            plugin.getUserManager().scheduleSave(user);
            plugin.getCoinsLogger().logGive(user, currency, amount, context.getSender());

            Lang.COMMAND_CURRENCY_GIVE_DONE.getMessage()
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                    .send(context.getSender());

            Player target = user.getPlayer();
            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                Lang.COMMAND_CURRENCY_GIVE_NOTIFY.getMessage()
                        .replace(currency.replacePlaceholders())
                        .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                        .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                        .send(target);
            }
        });

        return true;
    }

    public static boolean togglePayments(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        final boolean isOwn = !arguments.hasArgument(CommandArguments.PLAYER);
        final String name = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());

        plugin.getUserManager().manageUser(name, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            CurrencySettings settings = user.getSettings(currency);
            final boolean newState = !settings.isPaymentsEnabled();
            settings.setPaymentsEnabled(newState);
            plugin.getUserManager().scheduleSave(user);

            if (!isOwn) {
                Lang.COMMAND_CURRENCY_PAYMENTS_TARGET.getMessage()
                        .replace(currency.replacePlaceholders())
                        .replace(Placeholders.PLAYER_NAME, user.getName())
                        .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(newState))
                        .send(context.getSender());
            }

            Player target = user.getPlayer();
            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                Lang.COMMAND_CURRENCY_PAYMENTS_TOGGLE.getMessage()
                        .replace(currency.replacePlaceholders())
                        .replace(Placeholders.GENERIC_STATE, Lang.getEnabledOrDisabled(newState))
                        .send(target);
            }
        });
        return true;
    }

    public static boolean send(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        final String userName = arguments.getStringArgument(CommandArguments.PLAYER);

        final double initialAmount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        final double refinedAmount = currency.fine(initialAmount);
        if (refinedAmount <= 0D) return false;

        if (currency.getMinTransferAmount() > 0 && refinedAmount < currency.getMinTransferAmount()) {
            Lang.COMMAND_CURRENCY_SEND_ERROR_TOO_LOW.getMessage()
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(currency.getMinTransferAmount()))
                    .send(context.getSender());
            return false;
        }

        final Player from = context.getPlayerOrThrow();

    plugin
        .getUserManager()
        .manageUser(
            userName,
            targetUser -> {
              if (targetUser == null) {
                context.errorBadPlayer();
                return;
              }

              CoinsUser fromUser = plugin.getUserManager().getUserData(from);
              if (fromUser == targetUser) {
                CoreLang.ERROR_COMMAND_NOT_YOURSELF.getMessage().send(context.getSender());
                return;
              }

              if (refinedAmount > fromUser.getBalance(currency)) {
                Lang.COMMAND_CURRENCY_SEND_ERROR_NOT_ENOUGH
                    .getMessage()
                    .replace(currency.replacePlaceholders())
                    .send(from);
                return;
              }

              CurrencySettings settings = targetUser.getSettings(currency);
              if (!settings.isPaymentsEnabled()) {
                Lang.COMMAND_CURRENCY_SEND_ERROR_NO_PAYMENTS
                    .getMessage()
                    .replace(Placeholders.PLAYER_NAME, targetUser.getName())
                    .replace(currency.replacePlaceholders())
                    .send(from);
                return;
              }

              CoinsSendEvent event =
                  new CoinsSendEvent(Bukkit.getOfflinePlayer(targetUser.getName()), currency, refinedAmount, from);
              plugin.getServer().getPluginManager().callEvent(event);
              if (event.isCancelled()) {
                return;
              }
              final double finalAmount = event.getAmount();

              targetUser.addBalance(currency, finalAmount);
              fromUser.removeBalance(currency, finalAmount);

              plugin.getUserManager().scheduleSave(targetUser);
              plugin.getUserManager().scheduleSave(fromUser);
              plugin.getCoinsLogger().logSend(targetUser, currency, finalAmount, from);

              Lang.COMMAND_CURRENCY_SEND_DONE_SENDER
                  .getMessage()
                  .replace(currency.replacePlaceholders())
                  .replace(Placeholders.GENERIC_AMOUNT, currency.format(finalAmount))
                  .replace(Placeholders.GENERIC_BALANCE, fromUser.getBalance(currency))
                  .replace(Placeholders.PLAYER_NAME, targetUser.getName())
                  .send(context.getSender());

              Player target = plugin.getServer().getPlayer(targetUser.getName());
              if (target != null) {
                Lang.COMMAND_CURRENCY_SEND_DONE_NOTIFY
                    .getMessage()
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(finalAmount))
                    .replace(Placeholders.GENERIC_BALANCE, targetUser.getBalance(currency))
                    .replace(Placeholders.PLAYER_NAME, fromUser.getName())
                    .send(target);
              }
            });
        return true;
    }

    public static boolean set(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        final double initialAmount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (initialAmount < 0D) return false;

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            CoinsSetEvent event = new CoinsSetEvent(user, currency, initialAmount, context.getSender());
            plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            final double finalAmount = event.getAmount();

            user.setBalance(currency, finalAmount);

            plugin.getUserManager().scheduleSave(user);
            plugin.getCoinsLogger().logSet(user, currency, finalAmount, context.getSender());

            Lang.COMMAND_CURRENCY_SET_DONE.getMessage()
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(finalAmount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                    .send(context.getSender());

            Player target = user.getPlayer();

            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                Lang.COMMAND_CURRENCY_SET_NOTIFY.getMessage()
                        .replace(currency.replacePlaceholders())
                        .replace(Placeholders.GENERIC_AMOUNT, currency.format(finalAmount))
                        .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                        .send(target);
            }
        });
        return true;
    }

    public static boolean remove(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        final double initialAmount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        if (initialAmount <= 0D) return false;

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            CoinsTakeEvent event = new CoinsTakeEvent(user, currency, initialAmount, context.getSender());
            plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            final double finalAmount = event.getAmount();

            user.removeBalance(currency, finalAmount);

            plugin.getUserManager().scheduleSave(user);
            plugin.getCoinsLogger().logTake(user, currency, finalAmount, context.getSender());

            Lang.COMMAND_CURRENCY_TAKE_DONE.getMessage()
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(finalAmount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                    .send(context.getSender());

            Player target = user.getPlayer();
            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                Lang.COMMAND_CURRENCY_TAKE_NOTIFY.getMessage()
                        .replace(currency.replacePlaceholders())
                        .replace(Placeholders.GENERIC_AMOUNT, currency.format(finalAmount))
                        .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                        .send(target);
            }
        });
        return true;
    }

    public static boolean showTop(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        final int perPage = Config.TOP_ENTRIES_PER_PAGE.get();

        List<Pair<String, Double>> full = plugin.getCurrencyManager().getBalanceMap().getOrDefault(currency, Collections.emptyList());
        List<List<Pair<String, Double>>> split = Lists.split(full, perPage);
        final int pages = split.size();
        final int page = Math.max(0, Math.min(pages, Math.abs(arguments.getIntArgument(CommandArguments.AMOUNT, 1))) - 1);

        List<Pair<String, Double>> list = pages > 0 ? split.get(page) : new ArrayList<>();
        final int startPos = 1 + perPage * page;

        Lang.COMMAND_CURRENCY_TOP_LIST.getMessage()
                .replace(currency.replacePlaceholders())
                .replace(Placeholders.GENERIC_CURRENT, page + 1)
                .replace(Placeholders.GENERIC_MAX, pages)
                .replace(Placeholders.GENERIC_ENTRY, list1 -> {
                    int pos = startPos;
                    for (Pair<String, Double> pair : list) {
                        final int currentPos = pos++;
                        list1.add(Lang.COMMAND_CURRENCY_TOP_ENTRY.getString()
                                .replace(Placeholders.GENERIC_POS, NumberUtil.format(currentPos))
                                .replace(Placeholders.GENERIC_BALANCE, currency.format(pair.getSecond()))
                                .replace(Placeholders.PLAYER_NAME, pair.getFirst()));
                    }
                })
                .send(context.getSender());

        return true;
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
        final String name = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());
        final boolean isOwn = !arguments.hasArgument(CommandArguments.PLAYER);

        plugin.getUserManager().manageUser(name, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            (isOwn ? Lang.CURRENCY_BALANCE_DISPLAY_OWN : Lang.CURRENCY_BALANCE_DISPLAY_OTHERS).getMessage()
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                    .send(context.getSender());
        });
        return true;
    }
}