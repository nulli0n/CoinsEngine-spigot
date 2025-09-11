package su.nightexpress.coinsengine.hook.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyOperations;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.user.BalanceLookup;

import java.util.Collections;
import java.util.List;

public class CurrencyEconomy implements Economy {

    private static final EconomyResponse NO_BANKS = new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CoinsEngine does not support bank accounts!");

    private final CoinsEnginePlugin plugin;
    private final CurrencyManager   manager;
    private final Currency          currency;

    public CurrencyEconomy(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
        this.plugin = plugin;
        this.manager = plugin.getCurrencyManager();
        this.currency = currency;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return this.plugin.getName() + "_" + this.currency.getId();
    }

    @Override
    public String format(double amount) {
        return this.currency.format(amount);
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String currencyNamePlural() {
        return this.currency.getName();
    }

    @Override
    public String currencyNameSingular() {
        return this.currency.getName();
    }



    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return this.createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }



    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return this.getBalance(player);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        CoinsUser user = this.plugin.getUserManager().getOrFetch(player.getUniqueId());
        return this.getBalance(user);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return this.getBalance(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        CoinsUser user = this.plugin.getUserManager().getOrFetch(playerName);
        return this.getBalance(user);
    }

    private double getBalance(@Nullable CoinsUser user) {
        return user == null ? 0D : user.getBalance(this.currency);
    }



    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return this.hasAccount(player);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return this.plugin.getDataHandler().isUserExists(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return this.hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(String playerName) {
        return this.plugin.getDataHandler().isUserExists(playerName);
    }



    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return this.has(player, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        CoinsUser user = this.plugin.getUserManager().getOrFetch(player.getUniqueId());
        return this.has(user, amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return this.has(playerName, amount);
    }

    @Override
    public boolean has(String playerName, double amount) {
        CoinsUser user = this.plugin.getUserManager().getOrFetch(playerName);
        return this.has(user, amount);
    }

    private boolean has(@Nullable CoinsUser user, double amount) {
        return user != null && user.hasEnough(this.currency, amount);
    }



    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        CoinsUser user = this.plugin.getUserManager().getOrFetch(player.getUniqueId());
        return this.depositUser(user, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return this.depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        CoinsUser user = this.plugin.getUserManager().getOrFetch(playerName);
        return this.depositUser(user, amount);
    }

    @NotNull
    private EconomyResponse depositUser(@Nullable CoinsUser user, double amount) {
        if (user == null) {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, Lang.ECONOMY_ERROR_INVALID_PLAYER.getLegacy());
        }
        if (!this.manager.canPerformOperations()) {
            return new EconomyResponse(amount, user.getBalance(this.currency), EconomyResponse.ResponseType.FAILURE, "Operations are not available at this time.");
        }

        this.manager.performOperation(CurrencyOperations.forAddSilently(this.currency, amount, user));

        return new EconomyResponse(amount, user.getBalance(this.currency), EconomyResponse.ResponseType.SUCCESS, null);
    }



    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        CoinsUser user = this.plugin.getUserManager().getOrFetch(player.getUniqueId());
        return this.withdrawUser(user, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return this.withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        CoinsUser user = this.plugin.getUserManager().getOrFetch(playerName);
        return this.withdrawUser(user, amount);
    }

    @NotNull
    private EconomyResponse withdrawUser(@Nullable CoinsUser user, double amount) {
        if (user == null) {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, Lang.ECONOMY_ERROR_INVALID_PLAYER.getLegacy());
        }

        BalanceLookup lookup = user.balanceLookup(this.currency);

        if (!user.hasEnough(this.currency, amount)) {
            return new EconomyResponse(amount, lookup.balance(), EconomyResponse.ResponseType.FAILURE, Lang.ECONOMY_ERROR_INSUFFICIENT_FUNDS.getLegacy());
        }
        if (!this.manager.canPerformOperations()) {
            return new EconomyResponse(amount, user.getBalance(this.currency), EconomyResponse.ResponseType.FAILURE, "Operations are not available at this time.");
        }

        this.manager.performOperation(CurrencyOperations.forRemoveSilently(this.currency, amount, user));

        return new EconomyResponse(amount, lookup.balance(), EconomyResponse.ResponseType.SUCCESS, null);
    }



    @Override
    public EconomyResponse createBank(String name, String player) {
        return NO_BANKS;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return NO_BANKS;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return NO_BANKS;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return NO_BANKS;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return NO_BANKS;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return NO_BANKS;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return NO_BANKS;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return NO_BANKS;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return NO_BANKS;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return NO_BANKS;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return NO_BANKS;
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }
}
