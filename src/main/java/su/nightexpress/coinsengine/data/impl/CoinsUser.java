package su.nightexpress.coinsengine.data.impl;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.api.event.ChangeBalanceEvent;
import su.nightexpress.nightcore.db.AbstractUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoinsUser extends AbstractUser {

    private final Map<String, Double>           balanceMap;
    private final Map<String, CurrencySettings> settingsMap;

    @NotNull
    public static CoinsUser create(@NotNull CoinsEnginePlugin plugin, @NotNull UUID uuid, @NotNull String name) {
        long dateCreated = System.currentTimeMillis();

        Map<String, Double> balanceMap = new HashMap<>();
        for (Currency currency : plugin.getCurrencyManager().getCurrencies()) {
            balanceMap.put(currency.getId(), currency.getStartValue());
        }

        Map<String, CurrencySettings> settingsMap = new HashMap<>();

        return new CoinsUser(plugin, uuid, name, dateCreated, dateCreated, balanceMap, settingsMap);
    }

    public CoinsUser(@NotNull CoinsEnginePlugin plugin,
                     @NotNull UUID uuid,
                     @NotNull String name,
                     long dateCreated,
                     long lastLogin,
                     @NotNull Map<String, Double> balanceMap,
                     @NotNull Map<String, CurrencySettings> settingsMap
    ) {
        super(uuid, name, dateCreated, lastLogin);
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
        CoinsEngineAPI.getCurrencyManager().getCurrencies().forEach(this::resetBalance);
    }

    public void resetBalance(@NotNull Currency currency) {
        this.setBalance(currency, currency.getStartValue());
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
        amount = Math.max(0, amount);
        double oldBalance = this.getBalance(currency);

        this.balanceMap.put(currency.getId(), currency.fineAndLimit(amount));

        ChangeBalanceEvent changeBalanceEvent = new ChangeBalanceEvent(this, currency, oldBalance, this.getBalance(currency));
        Bukkit.getPluginManager().callEvent(changeBalanceEvent);
    }

    @NotNull
    public CurrencySettings getSettings(@NotNull Currency currency) {
        return this.settingsMap.computeIfAbsent(currency.getId(), k -> CurrencySettings.create(currency));
    }
}
