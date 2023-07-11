package su.nightexpress.coinsengine.command.currency;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;

public abstract class CurrencySubCommand extends GeneralCommand<CoinsEngine> {

    protected final Currency currency;

    public CurrencySubCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency, @NotNull String[] aliases, @NotNull Permission permission) {
        this(plugin, currency, aliases, permission.getName());
    }

    public CurrencySubCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency, @NotNull String[] aliases, @NotNull String permission) {
        super(plugin, aliases, permission);
        this.currency = currency;
    }
}
