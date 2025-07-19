package su.nightexpress.coinsengine.user;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class UserBalance {

    private final Map<String, Double> balanceMap;

    public UserBalance() {
        this(new ConcurrentHashMap<>());
    }

    public UserBalance(@NotNull Map<String, Double> balanceMap) {
        this.balanceMap = balanceMap;
    }

    @NotNull
    public static UserBalance createDefault() {
        UserBalance balance = new UserBalance();

        CoinsEngineAPI.getCurrencies().forEach(currency -> balance.set(currency, currency.getStartValue()));

        return balance;
    }

    @NotNull
    public Map<String, Double> getBalanceMap() {
        return this.balanceMap;
    }

    public void edit(@NotNull Currency currency, @NotNull Consumer<BalanceLookup> consumer) {
        consumer.accept(this.lookup(currency));
    }

    @NotNull
    public BalanceLookup lookup(@NotNull Currency currency) {
        return new BalanceLookup(this, currency);
    }

    public void clear() {
        this.balanceMap.clear();
    }

    public void clear(@NotNull Currency currency) {
        this.clear(currency.getId());
    }

    public void clear(@NotNull String currencyId) {
        this.balanceMap.remove(currencyId.toLowerCase());
    }

    public boolean has(@NotNull Currency currency, double amount) {
        return this.get(currency) >= amount;
    }

    public double get(@NotNull Currency currency) {
        return this.get(currency.getId());
    }

    public double get(@NotNull String currencyId) {
        return this.balanceMap.getOrDefault(currencyId, 0D);
    }

    public void add(@NotNull Currency currency, double amount) {
        this.add(currency.getId(), amount);
    }

    public void add(@NotNull String currencyId, double amount) {
        this.set(currencyId, this.get(currencyId) + Math.abs(amount));
    }

    public void remove(@NotNull Currency currency, double amount) {
        this.remove(currency.getId(), amount);
    }

    public void remove(@NotNull String currencyId, double amount) {
        this.set(currencyId, this.get(currencyId) - Math.abs(amount));
    }

    public void set(@NotNull Currency currency, double amount) {
        this.set(currency.getId(), currency.floorAndLimit(amount));
    }

    public void set(@NotNull String currencyId, double amount) {
        this.balanceMap.put(currencyId.toLowerCase(), amount);
    }
}
