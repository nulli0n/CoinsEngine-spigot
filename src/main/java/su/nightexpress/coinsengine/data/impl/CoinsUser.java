package su.nightexpress.coinsengine.data.impl;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.api.events.ChangeBalanceEvent;
import su.nightexpress.nightcore.database.AbstractUser;

import java.util.*;

public class CoinsUser extends AbstractUser<CoinsEnginePlugin> {

    private final Map<String, Double> balanceMap;
    private final Map<String, CurrencySettings> settingsMap;

    public static CoinsUser create(@NotNull CoinsEnginePlugin plugin, @NotNull UUID uuid, @NotNull String name) {
        Map<String, Double> balanceMap = new HashMap<>();
        for (Currency currency : plugin.getCurrencyManager().getCurrencies()) {
            balanceMap.put(currency.getId(), currency.getStartValue());
        }

        return new CoinsUser(plugin, uuid, name, System.currentTimeMillis(), System.currentTimeMillis(),
            balanceMap,
            new HashMap<>()
        );
    }

    public CoinsUser(
        @NotNull CoinsEnginePlugin plugin,
        @NotNull UUID uuid,
        @NotNull String name,
        long dateCreated,
        long lastLogin,
        @NotNull Map<String, Double> balanceMap,
        @NotNull Map<String, CurrencySettings> settingsMap
    ) {
        super(plugin, uuid, name, dateCreated, lastLogin);
        this.balanceMap = new HashMap<>(balanceMap);
        this.settingsMap = new HashMap<>(settingsMap);
    }

    @NotNull
    public Map<String, Double> getBalanceMap() {
        return balanceMap;
    }

    @NotNull
    public Map<String, CurrencySettings> getSettingsMap() {
        return settingsMap;
    }

    public void resetBalance() {
        this.plugin.getCurrencyManager().getCurrencies().forEach(this::resetBalance);
    }

    public void resetBalance(@NotNull Currency currency) {
        this.setBalance(currency, currency.getStartValue());
    }

    @NotNull
    @Deprecated
    public CurrencyData getCurrencyData(@NotNull Currency currency) {
        CurrencySettings settings = this.getSettings(currency);
        double balance = this.getBalance(currency);

        return new CurrencyData(currency, balance, settings.isPaymentsEnabled());
    }

    @Nullable
    @Deprecated
    public CurrencyData getCurrencyData(@NotNull String id) {
        Currency currency = this.plugin.getCurrencyManager().getCurrency(id);
        return currency == null ? null : this.getCurrencyData(currency);
    }

    public double getBalance(@NotNull Currency currency) {
        return this.balanceMap.computeIfAbsent(currency.getId(), k -> 0D);
    }

    public void addBalance(@NotNull Currency currency, double amount) {
        this.changeBalance(currency, this.getBalance(currency) + Math.abs(amount));
    }

    public void removeBalance(@NotNull Currency currency, double amount) {
        this.changeBalance(currency, this.getBalance(currency) - Math.abs(amount));
    }

    public void setBalance(@NotNull Currency currency, double amount) {
        this.changeBalance(currency, Math.abs(amount));
    }

    private void changeBalance(@NotNull Currency currency, double amount) {
        ChangeBalanceEvent changeBalanceEvent = new ChangeBalanceEvent(Objects.requireNonNull(this.getPlayer()), currency, balanceMap.get(currency.getId()), currency.fineAndLimit(amount));
        Bukkit.getPluginManager().callEvent(changeBalanceEvent);
        this.balanceMap.put(currency.getId(), currency.fineAndLimit(amount));
    }

    @NotNull
    public CurrencySettings getSettings(@NotNull Currency currency) {
        return this.settingsMap.computeIfAbsent(currency.getId(), k -> CurrencySettings.create(currency));
    }
}
