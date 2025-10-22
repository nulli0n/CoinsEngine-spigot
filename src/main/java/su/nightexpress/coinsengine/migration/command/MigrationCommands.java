package su.nightexpress.coinsengine.migration.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandArguments;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.coinsengine.migration.MigrationManager;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;

public class MigrationCommands {

    private final CoinsEnginePlugin plugin;
    private final MigrationManager migrationManager;
    private final CurrencyRegistry currencyRegistry;

    public MigrationCommands(@NotNull CoinsEnginePlugin plugin, @NotNull MigrationManager migrationManager, @NotNull CurrencyRegistry currencyRegistry) {
        this.plugin = plugin;
        this.migrationManager = migrationManager;
        this.currencyRegistry = currencyRegistry;
    }

    public void load() {
        this.plugin.getCommander().getPluginCommands().registerProvider(builder -> {
            builder.branch(Commands.literal("migrate")
                .permission(Perms.COMMAND_MIGRATE)
                .description(Lang.COMMAND_MIGRATE_DESC)
                .withArguments(
                    Arguments.string(CommandArguments.NAME).localized(Lang.COMMAND_ARGUMENT_NAME_PLUGIN).suggestions((reader, context) -> this.migrationManager.getMigratorNames()),
                    CommandArguments.currency(this.currencyRegistry)
                )
                .executes(this::migrate)
            );
        });
    }

    private boolean migrate(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getString(CommandArguments.NAME);
        Currency currency = arguments.get(CommandArguments.CURRENCY, Currency.class);

        return this.migrationManager.startMigration(context.getSender(), name, currency);
    }
}
