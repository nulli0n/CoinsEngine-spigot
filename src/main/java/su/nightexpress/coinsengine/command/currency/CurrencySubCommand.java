package su.nightexpress.coinsengine.command.currency;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.nightcore.command.impl.PluginCommand;

public abstract class CurrencySubCommand extends PluginCommand<CoinsEnginePlugin> {

    protected final Currency currency;

    public CurrencySubCommand(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull String[] aliases, @NotNull Permission permission) {
        this(plugin, currency, aliases, permission.getName());
    }

    public CurrencySubCommand(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency, @NotNull String[] aliases, @NotNull String permission) {
        super(plugin, aliases, permission);
        this.currency = currency;
    }
}
