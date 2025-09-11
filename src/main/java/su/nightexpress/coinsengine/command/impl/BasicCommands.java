package su.nightexpress.coinsengine.command.impl;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandArguments;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.currency.operation.impl.ResetOperation;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.RootCommand;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.impl.ReloadCommand;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.util.Lists;

public class BasicCommands {

    public static void load(@NotNull CoinsEnginePlugin plugin) {
        ChainedNode rootNode = plugin.getRootNode();

        rootNode.addChildren(ReloadCommand.builder(plugin, Perms.COMMAND_RELOAD));

        rootNode.addChildren(DirectNode.builder(plugin, "create")
            .permission(Perms.COMMAND_CREATE)
            .description(Lang.COMMAND_CREATE_DESC)
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().localized(Lang.COMMAND_ARGUMENT_NAME_NAME))
            .withArgument(ArgumentTypes.string(CommandArguments.SYMBOL).required().localized(Lang.COMMAND_ARGUMENT_NAME_SYMBOL))
            .withArgument(ArgumentTypes.bool(CommandArguments.DECIMALS).localized(Lang.COMMAND_ARGUMENT_NAME_DECIMAL).withSamples(context -> Lists.newList("true", "false")))
            .executes((context, arguments) -> createCurrency(plugin, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "reset")
            .permission(Perms.COMMAND_RESET)
            .description(Lang.COMMAND_RESET_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .executes((context, arguments) -> reset(plugin, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "resetall")
            .permission(Perms.COMMAND_RESET_ALL)
            .description(Lang.COMMAND_RESET_ALL_DESC)
            .withArgument(CommandArguments.currency(plugin))
            .executes((context, arguments) -> resetAll(plugin, context, arguments))
        );

        if (Config.isWalletEnabled()) {
            var command = RootCommand.direct(plugin, Config.WALLET_ALIASES.get(), builder -> builder
                .description(Lang.COMMAND_WALLET_DESC)
                .permission(Perms.COMMAND_WALLET)
                .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(Perms.COMMAND_WALLET_OTHERS))
                .executes((context, arguments) -> showWallet(plugin, context, arguments))
            );
            plugin.getCommandManager().registerCommand(command);
        }
    }

    private static boolean createCurrency(@NotNull CoinsEnginePlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getStringArgument(CommandArguments.NAME);
        String symbol = arguments.getStringArgument(CommandArguments.SYMBOL);
        boolean decimals = arguments.getBooleanArgument(CommandArguments.DECIMALS, true);

        return plugin.getCurrencyManager().createCurrency(context.getSender(), name, symbol, decimals);
    }

    private static boolean showWallet(@NotNull CoinsEnginePlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getStringArgument(CommandArguments.PLAYER, context.getSender().getName());
        plugin.getCurrencyManager().showWallet(context.getSender(), name);
        return true;
    }

    private static boolean reset(@NotNull CoinsEnginePlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            CommandSender sender = context.getSender();

            plugin.getCurrencyManager().getCurrencies().forEach(currency -> {
                ResetOperation operation = new ResetOperation(currency, user, sender);
                operation.setFeedback(false);

                plugin.getCurrencyManager().performOperation(operation);
            });

            Lang.COMMAND_RESET_DONE.getMessage().send(sender, replacer -> replacer.replace(Placeholders.PLAYER_NAME, user.getName()));
        });
        return true;
    }

    private static boolean resetAll(@NotNull CoinsEnginePlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (arguments.hasArgument(CommandArguments.CURRENCY)) {
            Currency currency = arguments.getArgument(CommandArguments.CURRENCY, Currency.class);
            plugin.getCurrencyManager().resetBalances(context.getSender(), currency);
        }
        else {
            plugin.getCurrencyManager().resetBalances(context.getSender());
        }
        return true;
    }
}
