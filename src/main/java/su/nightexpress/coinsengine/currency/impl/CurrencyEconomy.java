package su.nightexpress.coinsengine.currency.impl;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencyData;

import java.util.ArrayList;
import java.util.List;

public class CurrencyEconomy extends AbstractEconomy {

    private final CoinsEnginePlugin plugin;
    private final Currency          currency;

    public CurrencyEconomy(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
        this.plugin = plugin;
        this.currency = currency;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return this.plugin.getName();
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
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return this.createPlayerAccount(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return this.getBalance(player);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        CoinsUser user = this.plugin.getUserManager().getUserData(player.getUniqueId());
        return user == null ? 0D : user.getCurrencyData(this.currency).getBalance();
    }

    @Override
    public double getBalance(String playerName, String world) {
        return this.getBalance(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        CoinsUser user = this.plugin.getUserManager().getUserData(playerName);
        return user == null ? 0D : user.getCurrencyData(this.currency).getBalance();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return this.hasAccount(player);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return this.plugin.getData().isUserExists(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return this.hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(String playerName) {
        return this.plugin.getData().isUserExists(playerName);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return this.has(player, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        CoinsUser user = this.plugin.getUserManager().getUserData(player.getUniqueId());
        return user != null && user.getCurrencyData(this.currency).getBalance() >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return this.has(playerName, amount);
    }

    @Override
    public boolean has(String playerName, double amount) {
        CoinsUser user = this.plugin.getUserManager().getUserData(playerName);
        return user != null && user.getCurrencyData(this.currency).getBalance() >= amount;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        CoinsUser user = this.plugin.getUserManager().getUserData(player.getUniqueId());
        if (user == null) {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player not found.");
        }

        CurrencyData data = user.getCurrencyData(this.currency);
        data.addBalance(amount);
        this.plugin.getUserManager().saveAsync(user);
        double balance = data.getBalance();

        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return this.depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        CoinsUser user = this.plugin.getUserManager().getUserData(playerName);
        if (user == null) {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player not found.");
        }

        CurrencyData data = user.getCurrencyData(this.currency);
        data.addBalance(amount);
        this.plugin.getUserManager().saveAsync(user);
        double balance = data.getBalance();

        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        CoinsUser user = this.plugin.getUserManager().getUserData(player.getUniqueId());
        if (user == null) {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player not found.");
        }

        CurrencyData data = user.getCurrencyData(this.currency);
        data.removeBalance(amount);
        this.plugin.getUserManager().saveAsync(user);
        double balance = data.getBalance();

        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return this.withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        CoinsUser user = this.plugin.getUserManager().getUserData(playerName);
        if (user == null) {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Player not found.");
        }

        CurrencyData data = user.getCurrencyData(this.currency);
        data.removeBalance(amount);
        this.plugin.getUserManager().saveAsync(user);
        double balance = data.getBalance();

        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SunLightEco does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SunLightEco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SunLightEco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SunLightEco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SunLightEco does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SunLightEco does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SunLightEco does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "SunLightEco does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }
}
