package su.nightexpress.coinsengine.data.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.data.AbstractUser;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.*;

public class CoinsUser extends AbstractUser<CoinsEngine> {

    private final Map<String, CurrencyData> currencyDataMap;

    public CoinsUser(@NotNull CoinsEngine plugin, @NotNull UUID uuid, @NotNull String name) {
        this(plugin, uuid, name, System.currentTimeMillis(), System.currentTimeMillis(),
            new HashSet<>());
    }

    public CoinsUser(
            @NotNull CoinsEngine plugin,
            @NotNull UUID uuid,
            @NotNull String name,
            long dateCreated,
            long lastLogin,
            @NotNull Set<CurrencyData> currencyDatas
    ) {
        super(plugin, uuid, name, dateCreated, lastLogin);
        this.currencyDataMap = new HashMap<>();
        currencyDatas.forEach(data -> this.getCurrencyDataMap().put(data.getCurrency().getId(), data));
        this.plugin.getCurrencyManager().getCurrencies().forEach(this::getCurrencyData);
    }

    @NotNull
    public Map<String, CurrencyData> getCurrencyDataMap() {
        return currencyDataMap;
    }

    @NotNull
    public CurrencyData getCurrencyData(@NotNull Currency currency) {
        return this.getCurrencyDataMap().computeIfAbsent(currency.getId(), k -> CurrencyData.create(currency));
    }

    @Nullable
    public CurrencyData getCurrencyData(@NotNull String id) {
        return this.getCurrencyDataMap().get(id.toLowerCase());
    }
}
