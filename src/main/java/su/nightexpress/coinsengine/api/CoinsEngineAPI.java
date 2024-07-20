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

    public static final CoinsEnginePlugin PLUGIN = CoinsEnginePlugin.getPlugin(CoinsEnginePlugin.class);

    @NotNull
    public static UserManager getUserManager() {
        return PLUGIN.getUserManager();
    }

    @NotNull
    public static CurrencyManager getCurrencyManager() {
        return PLUGIN.getCurrencyManager();
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
        getUserManager().saveAsync(user);
        return true;
    }


    @NotNull
    public static CoinsUser getUserData(@NotNull Player player) {
        return getUserManager().getUserData(player);
    }

    @Nullable
    public static CoinsUser getUserData(@NotNull String name) {
        return getUserManager().getUserData(name);
    }

    @Nullable
    public static CoinsUser getUserData(@NotNull UUID uuid) {
        return getUserManager().getUserData(uuid);
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
