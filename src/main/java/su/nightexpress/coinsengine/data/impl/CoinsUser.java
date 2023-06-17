package su.nightexpress.coinsengine.data.impl;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUser;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoinsUser extends AbstractUser<CoinsEngine> {

    private final Map<String, Double> balanceMap;

    public CoinsUser(@NotNull CoinsEngine plugin, @NotNull UUID uuid, @NotNull String name) {
        this(plugin, uuid, name, System.currentTimeMillis(), System.currentTimeMillis(),
            new HashMap<>());
    }

    public CoinsUser(
            @NotNull CoinsEngine plugin,
            @NotNull UUID uuid,
            @NotNull String name,
            long dateCreated,
            long lastLogin,
            @NotNull Map<String, Double> balanceMap
    ) {
        super(plugin, uuid, name, dateCreated, lastLogin);
        this.balanceMap = new HashMap<>();

        this.getBalanceMap().putAll(balanceMap);
        //System.out.println("loaded: " + balanceMap);
        this.getBalanceMap().keySet().removeIf(id -> this.plugin.getCurrencyManager().getCurrency(id) == null);
        //System.out.println("cleaned up: " + balanceMap);
        this.plugin.getCurrencyManager().getCurrencies().forEach(currency -> {
            if (!this.getBalanceMap().containsKey(currency.getId())) {
                this.setBalance(currency, currency.getStartValue());
            }
        });
    }

    @NotNull
    public Map<String, Double> getBalanceMap() {
        return balanceMap;
    }

    public double getBalance(@NotNull Currency currency) {
        return this.getBalance(currency.getId());
    }

    public double getBalance(@NotNull String id) {
        return this.getBalanceMap().getOrDefault(id.toLowerCase(), 0D);
    }

    public void setBalance(@NotNull Currency currency, double amount) {
        this.getBalanceMap().put(currency.getId(), currency.fineAndLimit(amount));
    }

    public void addBalance(@NotNull Currency currency, double amount) {
        this.setBalance(currency, this.getBalance(currency) + Math.abs(amount));
    }

    public void removeBalance(@NotNull Currency currency, double amount) {
        this.setBalance(currency, this.getBalance(currency) - Math.abs(amount));
    }
}
