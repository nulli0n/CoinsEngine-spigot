package su.nightexpress.coinsengine.command.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandArguments;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.migration.MigrationPlugin;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.impl.ReloadCommand;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;

public class BasicCommands {

    public static void load(@NotNull CoinsEnginePlugin plugin) {
        ChainedNode rootNode = plugin.getRootNode();

        rootNode.addChildren(ReloadCommand.builder(plugin, Perms.COMMAND_RELOAD));

        rootNode.addChildren(DirectNode.builder(plugin, "migrate")
            .permission(Perms.COMMAND_MIGRATE)
            .description(Lang.COMMAND_MIGRATE_DESC)
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).required()
                .localized(Lang.COMMAND_ARGUMENT_NAME_PLUGIN)
                .withSamples(context -> plugin.getMigrationManager().getMigrationPluginNames()))
            .withArgument(CommandArguments.currency(plugin).required())
            .executes((context, arguments) -> migrate(plugin, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "reset")
            .permission(Perms.COMMAND_RESET)
            .description(Lang.COMMAND_RESET_DESC)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .executes((context, arguments) -> reset(plugin, context, arguments))
        );

        rootNode.addChildren(DirectNode.builder(plugin, "wipe")
            .permission(Perms.COMMAND_WIPE)
            .description(Lang.COMMAND_WIPE_DESC)
            .withArgument(CommandArguments.currency(plugin).required())
            .executes((context, arguments) -> wipe(plugin, context, arguments))
        );
    }

    public static boolean migrate(@NotNull CoinsEnginePlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        MigrationPlugin migrationPlugin = plugin.getMigrationManager().getPlugin(arguments.getStringArgument(CommandArguments.NAME));
        if (migrationPlugin == null) {
            Lang.COMMAND_MIGRATE_ERROR_PLUGIN.getMessage().send(context.getSender());
            return false;
        }

        Currency currency = arguments.getArgument(CommandArguments.CURRENCY, Currency.class);

        plugin.runTaskAsync(task -> {
            Lang.COMMAND_MIGRATE_START.getMessage().send(context.getSender(), replacer -> replacer.replace(Placeholders.GENERIC_NAME, migrationPlugin.getPluginName()));
            plugin.getMigrationManager().migrate(migrationPlugin, currency);
            Lang.COMMAND_MIGRATE_DONE.getMessage().send(context.getSender(), replacer -> replacer.replace(Placeholders.GENERIC_NAME, migrationPlugin.getPluginName()));
        });

        return true;
    }

    public static boolean reset(@NotNull CoinsEnginePlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            user.resetBalance();
            plugin.getUserManager().save(user);

            Lang.COMMAND_RESET_DONE.getMessage().send(context.getSender(), replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
            );
        });
        return true;
    }

    public static boolean wipe(@NotNull CoinsEnginePlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Currency currency = arguments.getArgument(CommandArguments.CURRENCY, Currency.class);

        Lang.COMMAND_WIPE_START.getMessage().send(context.getSender(), replacer -> replacer.replace(currency.replacePlaceholders()));

        plugin.runTaskAsync(task -> {
            plugin.getData().resetBalances(currency);
            plugin.getUserManager().getLoaded().forEach(user -> user.resetBalance(currency));
            Lang.COMMAND_WIPE_FINISH.getMessage().send(context.getSender(), replacer -> replacer.replace(currency.replacePlaceholders()));
        });

        return true;
    }
}
