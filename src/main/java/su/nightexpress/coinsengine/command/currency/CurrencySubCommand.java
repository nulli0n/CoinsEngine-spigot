package su.nightexpress.coinsengine.command.currency;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.nightcore.command.impl.PluginCommand;

public abstract class CurrencySubCommand extends PluginCommand<CoinsEngine> {

    protected final Currency currency;

    public CurrencySubCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency, @NotNull String[] aliases, @NotNull Permission permission) {
        this(plugin, currency, aliases, permission.getName());
    }

    public CurrencySubCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency, @NotNull String[] aliases, @NotNull String permission) {
        super(plugin, aliases, permission);
        this.currency = currency;
    }
}
