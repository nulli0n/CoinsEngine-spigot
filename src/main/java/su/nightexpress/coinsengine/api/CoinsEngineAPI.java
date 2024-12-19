package su.nightexpress.coinsengine.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.data.UserManager;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CoinsEngineAPI {

    private static CoinsEnginePlugin plugin;

    public static void load(@NotNull CoinsEnginePlugin plugin) {
        CoinsEngineAPI.plugin = plugin;
    }

    public static void unload() {
        plugin = null;
    }

    @NotNull
    public static UserManager getUserManager() {
        return plugin.getUserManager();
    }

    @NotNull
    public static CurrencyManager getCurrencyManager() {
        return plugin.getCurrencyManager();
    }

    @Nullable
    public static Currency getCurrency(@NotNull String id) {
        return getCurrencyManager().getCurrency(id);
    }

    public static boolean hasCurrency(@NotNull String id) {
        return getCurrency(id) != null;
    }

    public static void regsiterCurrency(@NotNull Currency currency) {
        getCurrencyManager().registerCurrency(currency);
    }



    public static double getBalance(@NotNull Player player, @NotNull Currency currency) {
        return getUserData(player).getBalance(currency);
    }

    public static double getBalance(@NotNull UUID playerId, @NotNull Currency currency) {
        CoinsUser user = getUserData(playerId);

        return user == null ? 0D : user.getBalance(currency);
    }

    public static double getBalance(@NotNull UUID playerId, @NotNull String currencyName) {
        Currency currency = getCurrency(currencyName);

        return currency == null ? 0D : getBalance(playerId, currency);
    }


    public static void addBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        editBalance(() -> getUserData(player), user -> user.addBalance(currency, amount));
    }

    public static void setBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        editBalance(() -> getUserData(player), user -> user.setBalance(currency, amount));
    }

    public static void removeBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        editBalance(() -> getUserData(player), user -> user.removeBalance(currency, amount));
    }


    public static boolean addBalance(@NotNull UUID playerId, @NotNull Currency currency, double amount) {
        return editBalance(() -> getUserData(playerId), user -> user.addBalance(currency, amount));
    }

    public static boolean setBalance(@NotNull UUID playerId, @NotNull Currency currency, double amount) {
        return editBalance(() -> getUserData(playerId), user -> user.setBalance(currency, amount));
    }

    public static boolean removeBalance(@NotNull UUID playerId, @NotNull Currency currency, double amount) {
        return editBalance(() -> getUserData(playerId), user -> user.removeBalance(currency, amount));
    }


    public static boolean addBalance(@NotNull UUID playerId, @NotNull String currencyName, double amount) {
        Currency currency = getCurrency(currencyName);
        return currency != null && addBalance(playerId, currency, amount);
    }

    public static boolean setBalance(@NotNull UUID playerId, @NotNull String currencyName, double amount) {
        Currency currency = getCurrency(currencyName);
        return currency != null && setBalance(playerId, currency, amount);
    }

    public static boolean removeBalance(@NotNull UUID playerId, @NotNull String currencyName, double amount) {
        Currency currency = getCurrency(currencyName);
        return currency != null && removeBalance(playerId, currency, amount);
    }


    private static boolean editBalance(@NotNull Supplier<CoinsUser> supplier, @NotNull Consumer<CoinsUser> consumer) {
        CoinsUser user = supplier.get();
        if (user == null) return false;

        consumer.accept(user);
        getUserManager().save(user);
        return true;
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
