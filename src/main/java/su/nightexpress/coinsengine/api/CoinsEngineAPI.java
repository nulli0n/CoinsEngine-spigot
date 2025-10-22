package su.nightexpress.coinsengine.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandManager;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.coinsengine.currency.operation.NotificationTarget;
import su.nightexpress.coinsengine.currency.operation.OperationContext;
import su.nightexpress.coinsengine.currency.operation.OperationResult;
import su.nightexpress.coinsengine.user.UserManager;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CoinsEngineAPI {

    private static CoinsEnginePlugin plugin;

    public static void load(@NotNull CoinsEnginePlugin plugin) {
        CoinsEngineAPI.plugin = plugin;
    }

    public static void clear() {
        plugin = null;
    }

    public static boolean isLoaded() {
        return plugin != null;
    }

    @NotNull
    public static CoinsEnginePlugin plugin() {
        if (plugin == null) throw new IllegalStateException("API is not yet initialized!");

        return plugin;
    }

    @NotNull
    public static UserManager getUserManager() {
        return plugin().getUserManager();
    }

    @NotNull
    public static CurrencyManager getCurrencyManager() {
        return plugin().getCurrencyManager();
    }

    @NotNull
    public static CurrencyRegistry getCurrencyRegistry() {
        return plugin().getCurrencyRegistry();
    }

    @NotNull
    public static CommandManager getCommandManager() {
        return plugin().getCommander();
    }

    @NotNull
    public static Collection<Currency> getCurrencies() { // keep it Collection for the API compatibility
        return getCurrencyRegistry().getCurrencies();
    }

    @Nullable
    public static Currency getCurrency(@NotNull String id) {
        return getCurrencyRegistry().getById(id);
    }

    public static boolean hasCurrency(@NotNull String id) {
        return getCurrencyRegistry().isRegistered(id);
    }

    public static void regsiterCurrency(@NotNull Currency currency) {
        getCurrencyManager().registerCurrency(currency);
    }

    public static void regsiterCurrencyWithCommands(@NotNull Currency currency) {
        regsiterCurrency(currency);
        getCommandManager().getCurrencyCommands().loadCommands(currency);
    }

    public static void unregsiterCurrency(@NotNull Currency currency) {
        getCommandManager().getCurrencyCommands().unregisterCommands(currency);
        getCurrencyManager().unregisterCurrency(currency);
    }



    public static double getBalance(@NotNull UUID playerId, @NotNull String currencyName) {
        Currency currency = getCurrency(currencyName);

        return currency == null ? 0D : getBalance(playerId, currency);
    }

    public static double getBalance(@NotNull UUID playerId, @NotNull Currency currency) {
        CoinsUser user = getUserData(playerId);

        return user == null ? 0D : user.getBalance(currency);
    }

    public static double getBalance(@NotNull Player player, @NotNull Currency currency) {
        return getUserData(player).getBalance(currency);
    }



    public static boolean addBalance(@NotNull UUID playerId, @NotNull String currencyName, double amount) {
        Currency currency = getCurrency(currencyName);
        return currency != null && addBalance(playerId, currency, amount);
    }

    public static boolean addBalance(@NotNull UUID playerId, @NotNull Currency currency, double amount) {
        return addBalance(playerId, currency, amount, operationContext());
    }

    public static boolean addBalance(@NotNull UUID playerId, @NotNull Currency currency, double amount, @NotNull OperationContext context) {
        return editBalance(playerId, user -> getCurrencyManager().give(context, user, currency, amount));
    }

    public static void addBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        addBalance(player, currency, amount, operationContext());
    }

    public static boolean addBalance(@NotNull Player player, @NotNull Currency currency, double amount, @NotNull OperationContext context) {
        return getCurrencyManager().give(context, player, currency, amount) == OperationResult.SUCCESS;
    }


    public static boolean removeBalance(@NotNull UUID playerId, @NotNull String currencyName, double amount) {
        Currency currency = getCurrency(currencyName);
        return currency != null && removeBalance(playerId, currency, amount);
    }

    public static boolean removeBalance(@NotNull UUID playerId, @NotNull Currency currency, double amount) {
        return removeBalance(playerId, currency, amount, operationContext());
    }

    public static void removeBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        removeBalance(player, currency, amount, operationContext());
    }

    public static boolean removeBalance(@NotNull UUID playerId, @NotNull Currency currency, double amount, @NotNull OperationContext context) {
        return editBalance(playerId, user -> getCurrencyManager().remove(context, user, currency, amount));
    }

    public static boolean removeBalance(@NotNull Player player, @NotNull Currency currency, double amount, @NotNull OperationContext context) {
        return getCurrencyManager().remove(context, player, currency, amount) == OperationResult.SUCCESS;
    }


    public static boolean setBalance(@NotNull UUID playerId, @NotNull String currencyName, double amount) {
        Currency currency = getCurrency(currencyName);
        return currency != null && setBalance(playerId, currency, amount);
    }

    public static boolean setBalance(@NotNull UUID playerId, @NotNull Currency currency, double amount) {
        return setBalance(playerId, currency, amount, operationContext());
    }

    public static void setBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        setBalance(player, currency, amount, operationContext());
    }

    public static boolean setBalance(@NotNull UUID playerId, @NotNull Currency currency, double amount, @NotNull OperationContext context) {
        return editBalance(playerId, user -> getCurrencyManager().set(context, user, currency, amount));
    }

    public static boolean setBalance(@NotNull Player player, @NotNull Currency currency, double amount, @NotNull OperationContext context) {
        return getCurrencyManager().set(context, player, currency, amount) == OperationResult.SUCCESS;
    }

    private static boolean editBalance(@NotNull UUID id, @NotNull Function<CoinsUser, OperationResult> function) {
        CoinsUser user = getUserData(id);
        if (user == null) return false;

        OperationResult result = function.apply(user);
        return result == OperationResult.SUCCESS;
    }

    @NotNull
    private static OperationContext operationContext() {
        return OperationContext.custom("API").silentFor(NotificationTarget.USER, NotificationTarget.EXECUTOR, NotificationTarget.CONSOLE_LOGGER);
    }


    @NotNull
    public static CoinsUser getUserData(@NotNull Player player) {
        return getUserManager().getOrFetch(player);
    }

    @Nullable
    public static CoinsUser getUserData(@NotNull String name) {
        return getUserManager().getOrFetch(name);
    }

    @Nullable
    public static CoinsUser getUserData(@NotNull UUID uuid) {
        return getUserManager().getOrFetch(uuid);
    }

    @NotNull
    public static CompletableFuture<CoinsUser> getUserDataAsync(@NotNull String name) {
        return getUserManager().getUserDataAsync(name);
    }

    @NotNull
    public static CompletableFuture<CoinsUser> getUserDataAsync(@NotNull UUID uuid) {
        return getUserManager().getUserDataAsync(uuid);
    }
}
