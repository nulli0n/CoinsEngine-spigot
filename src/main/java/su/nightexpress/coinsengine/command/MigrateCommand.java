package su.nightexpress.coinsengine.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.migration.MigrationPlugin;
import su.nightexpress.coinsengine.migration.impl.AbstractDataConverter;

import java.util.List;
import java.util.stream.Stream;

public class MigrateCommand extends AbstractCommand<CoinsEngine> {

    public MigrateCommand(@NotNull CoinsEngine plugin) {
        super(plugin, new String[]{"migrate"}, Perms.COMMAND_MIGRATE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_MIGRATE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_MIGRATE_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Stream.of(MigrationPlugin.values()).map(MigrationPlugin::getPluginName).toList();
        }
        if (arg == 2) {
            return this.plugin.getCurrencyManager().getCurrencies().stream().map(Currency::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        AbstractDataConverter converter = MigrationPlugin.getConverter(result.getArg(1));
        if (converter == null) {
            this.plugin.getMessage(Lang.COMMAND_MIGRATE_ERROR_PLUGIN).send(sender);
            return;
        }

        Currency currency = this.plugin.getCurrencyManager().getCurrency(result.getArg(2));
        if (currency == null) {
            this.plugin.getMessage(Lang.CURRENCY_ERROR_INVALID).send(sender);
            return;
        }

        this.plugin.runTaskAsync(task -> {
            this.plugin.getMessage(Lang.COMMAND_MIGRATE_START).replace(Placeholders.GENERIC_NAME, converter.getPluginName()).send(sender);
            converter.migrate(currency);
            this.plugin.getMessage(Lang.COMMAND_MIGRATE_DONE).replace(Placeholders.GENERIC_NAME, converter.getPluginName()).send(sender);
        });
    }
}
