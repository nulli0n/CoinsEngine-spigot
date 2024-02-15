package su.nightexpress.coinsengine.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.migration.MigrationPlugin;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;

import java.util.List;

public class MigrateCommand extends AbstractCommand<CoinsEnginePlugin> {

    public MigrateCommand(@NotNull CoinsEnginePlugin plugin) {
        super(plugin, new String[]{"migrate"}, Perms.COMMAND_MIGRATE);
        this.setDescription(Lang.COMMAND_MIGRATE_DESC);
        this.setUsage(Lang.COMMAND_MIGRATE_USAGE);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.plugin.getMigrationManager().getMigrationPluginNames();
        }
        if (arg == 2) {
            return this.plugin.getCurrencyManager().getCurrencies().stream().map(Currency::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.errorUsage(sender);
            return;
        }

        MigrationPlugin migrationPlugin = this.plugin.getMigrationManager().getPlugin(result.getArg(1));
        if (migrationPlugin == null) {
            Lang.COMMAND_MIGRATE_ERROR_PLUGIN.getMessage().send(sender);
            return;
        }

        Currency currency = this.plugin.getCurrencyManager().getCurrency(result.getArg(2));
        if (currency == null) {
            Lang.CURRENCY_ERROR_INVALID.getMessage().send(sender);
            return;
        }

        this.plugin.runTaskAsync(task -> {
            Lang.COMMAND_MIGRATE_START.getMessage().replace(Placeholders.GENERIC_NAME, migrationPlugin.getPluginName()).send(sender);
            this.plugin.getMigrationManager().migrate(migrationPlugin, currency);
            Lang.COMMAND_MIGRATE_DONE.getMessage().replace(Placeholders.GENERIC_NAME, migrationPlugin.getPluginName()).send(sender);
        });
    }
}
