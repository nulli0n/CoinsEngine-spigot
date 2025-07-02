package su.nightexpress.coinsengine.migration.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandArguments;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.migration.MigrationManager;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;

public class MigrationCommands {

    public static void load(@NotNull CoinsEnginePlugin plugin, @NotNull MigrationManager manager) {
        ChainedNode rootNode = plugin.getRootNode();

        rootNode.addChildren(DirectNode.builder(plugin, "migrate")
            .permission(Perms.COMMAND_MIGRATE)
            .description(Lang.COMMAND_MIGRATE_DESC)
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).required()
                .localized(Lang.COMMAND_ARGUMENT_NAME_PLUGIN)
                .withSamples(context -> manager.getMigratorNames()))
            .withArgument(CommandArguments.currency(plugin).required())
            .executes((context, arguments) -> migrate(manager, context, arguments))
        );
    }

    private static boolean migrate(@NotNull MigrationManager manager, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getStringArgument(CommandArguments.NAME);
        Currency currency = arguments.getArgument(CommandArguments.CURRENCY, Currency.class);

        return manager.startMigration(context.getSender(), name, currency);
    }
}
