package su.nightexpress.coinsengine.data.impl;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.api.event.ChangeBalanceEvent;
import su.nightexpress.coinsengine.user.BalanceLookup;
import su.nightexpress.coinsengine.user.UserBalance;
import su.nightexpress.nightcore.db.AbstractUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class CoinsUser extends AbstractUser {

    private final UserBalance                   balance;
    private final Map<String, CurrencySettings> settingsMap;

    private boolean hiddenFromTops;

    @NotNull
    public static CoinsUser create(@NotNull UUID uuid, @NotNull String name) {
        long dateCreated = System.currentTimeMillis();
        UserBalance balance = UserBalance.createDefault();
        Map<String, CurrencySettings> settingsMap = new HashMap<>();
        boolean hiddenFromTops = false;

        return new CoinsUser(uuid, name, dateCreated, dateCreated, balance, settingsMap, hiddenFromTops);
    }

    public CoinsUser(@NotNull UUID uuid,
                     @NotNull String name,
                     long dateCreated,
                     long lastLogin,
                     @NotNull UserBalance balance,
                     @NotNull Map<String, CurrencySettings> settingsMap,
                     boolean hiddenFromTops) {
        super(uuid, name, dateCreated, lastLogin);
        this.balance = balance;
        this.settingsMap = new HashMap<>(settingsMap);
        this.setHiddenFromTops(hiddenFromTops);
    }

    @NotNull
    @Deprecated
    public Map<String, Double> getBalanceMap() {
        return this.balance.getBalanceMap();
    }

    @NotNull
    public UserBalance getBalance() {
        return this.balance;
    }

    @NotNull
    public BalanceLookup balanceLookup(@NotNull Currency currency) {
        return this.balance.lookup(currency);
    }

    /**
     * Edits user's balance of specific currency and fires the ChangeBalanceEvent event. If event was cancelled, the balance is set back to previous (old) value.
     *
     * @param currency Currency to edit balance of.
     * @param consumer balance function.
     */
    public void editBalance(@NotNull Currency currency, @NotNull Consumer<BalanceLookup> consumer) {
        double oldBalance = this.getBalance(currency);

        this.balance.edit(currency, consumer);

        ChangeBalanceEvent event = new ChangeBalanceEvent(this, currency, oldBalance, this.getBalance(currency));
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            this.balance.set(currency, oldBalance);
        }
    }

    public void resetBalance() {
        CoinsEngineAPI.getCurrencyManager().getCurrencies().forEach(this::resetBalance);
    }

    public void resetBalance(@NotNull Currency currency) {
        this.editBalance(currency, lookup -> lookup.set(currency.getStartValue()));
    }

    public boolean hasEnough(@NotNull Currency currency, double amount) {
        return this.balance.has(currency, amount);
    }

    public double getBalance(@NotNull Currency currency) {
        return this.balance.get(currency);
    }

    public void addBalance(@NotNull Currency currency, double amount) {
        this.editBalance(currency, lookup -> lookup.add(amount));
    }

    public void removeBalance(@NotNull Currency currency, double amount) {
        this.editBalance(currency, lookup -> lookup.remove(amount));
    }

    public void setBalance(@NotNull Currency currency, double amount) {
        this.editBalance(currency, lookup -> lookup.set(amount));
    }

    @NotNull
    public Map<String, CurrencySettings> getSettingsMap() {
        return settingsMap;
    }

    @NotNull
    public CurrencySettings getSettings(@NotNull Currency currency) {
        return this.settingsMap.computeIfAbsent(currency.getId(), k -> CurrencySettings.create(currency));
    }

    public boolean isHiddenFromTops() {
        return this.hiddenFromTops;
    }

    public void setHiddenFromTops(boolean hiddenFromTops) {
        this.hiddenFromTops = hiddenFromTops;
    }
}
