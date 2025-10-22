package su.nightexpress.coinsengine.currency.impl;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.operation.NotificationTarget;
import su.nightexpress.coinsengine.currency.operation.OperationContext;
import su.nightexpress.coinsengine.currency.operation.OperationResult;
import su.nightexpress.coinsengine.data.DataHandler;
import su.nightexpress.coinsengine.user.UserManager;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class EconomyCurrency extends AbstractCurrency implements Economy {

    private static final EconomyResponse NO_BANKS = new EconomyResponse(0D, 0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CoinsEngine does not support bank accounts!");

    private final CoinsEnginePlugin plugin;
    /*private final CurrencyManager   currencyManager;
    private final DataHandler       dataHandler;
    private final UserManager       userManager;*/

    public EconomyCurrency(@NotNull Path path,
                           @NotNull String id,
                           @NotNull CoinsEnginePlugin plugin,
                           @NotNull CurrencyManager currencyManager,
                           @NotNull DataHandler dataHandler,
                           @NotNull UserManager userManager) {
        super(path, id);

        this.plugin = plugin;
        /*this.currencyManager = currencyManager;
        this.dataHandler = dataHandler;
        this.userManager = userManager;*/
    }

    @Override
    public void onRegister() {
        ServicesManager services = Bukkit.getServer().getServicesManager();
        services.register(Economy.class, this, this.plugin, ServicePriority.High);
    }

    @Override
    public void onUnregister() {
        ServicesManager services = Bukkit.getServer().getServicesManager();
        services.unregister(Economy.class, this);
    }

    @Override
    public boolean isPrimary() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String currencyNamePlural() {
        return this.getName();
    }

    @Override
    public String currencyNameSingular() {
        return this.getName();
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
        return user == null ? 0D : user.getBalance(this);
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
        return user != null && user.hasEnough(this, amount);
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
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, Lang.ECONOMY_ERROR_INVALID_PLAYER.text());
        }

        OperationResult result = this.plugin.getCurrencyManager().give(this.operationContext(), user, this, amount);
        EconomyResponse.ResponseType type = result == OperationResult.SUCCESS ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE;

        return new EconomyResponse(amount, user.getBalance(this), type, null);
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
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, Lang.ECONOMY_ERROR_INVALID_PLAYER.text());
        }

        if (!user.hasEnough(this, amount)) {
            return new EconomyResponse(amount, user.getBalance(this), EconomyResponse.ResponseType.FAILURE, Lang.ECONOMY_ERROR_INSUFFICIENT_FUNDS.text());
        }

        OperationResult result = this.plugin.getCurrencyManager().remove(this.operationContext(), user, this, amount);
        EconomyResponse.ResponseType type = result == OperationResult.SUCCESS ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE;

        return new EconomyResponse(amount, user.getBalance(this), type, null);
    }

    @NotNull
    private OperationContext operationContext() {
        return OperationContext.custom("Vault Eco - " + this.name).silentFor(NotificationTarget.EXECUTOR, NotificationTarget.USER, NotificationTarget.CONSOLE_LOGGER);
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
