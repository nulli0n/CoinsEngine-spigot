package su.nightexpress.coinsengine.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.data.UserManager;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CoinsEngineAPI {

    public static final CoinsEngine PLUGIN = CoinsEngine.getPlugin(CoinsEngine.class);

    public static double getBalance(@NotNull Player player, @NotNull Currency currency) {
        return getUserData(player).getCurrencyData(currency).getBalance();
    }

    public static void addBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        CoinsUser user = getUserData(player);
        user.getCurrencyData(currency).addBalance(amount);
        user.saveData(PLUGIN);
    }

    public static void setBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        CoinsUser user = getUserData(player);
        user.getCurrencyData(currency).setBalance(amount);
        user.saveData(PLUGIN);
    }

    public static void removeBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        CoinsUser user = getUserData(player);
        user.getCurrencyData(currency).removeBalance(amount);
        user.saveData(PLUGIN);
    }

    @NotNull
    public static CoinsUser getUserData(@NotNull Player player) {
        return PLUGIN.getUserManager().getUserData(player);
    }

    @Nullable
    public static CoinsUser getUserData(@NotNull String name) {
        return PLUGIN.getUserManager().getUserData(name);
    }

    @NotNull
    public static CompletableFuture<CoinsUser> getUserDataAsync(@NotNull String name) {
        return PLUGIN.getUserManager().getUserDataAsync(name);
    }

    @Nullable
    public static CoinsUser getUserData(@NotNull UUID uuid) {
        return PLUGIN.getUserManager().getUserData(uuid);
    }

    @NotNull
    public static CompletableFuture<CoinsUser> getUserDataAsync(@NotNull UUID uuid) {
        return PLUGIN.getUserManager().getUserDataAsync(uuid);
    }

    @Nullable
    public static Currency getCurrency(@NotNull String id) {
        return getCurrencyManager().getCurrency(id);
    }

    public static boolean hasCurrency(@NotNull String id) {
        return getCurrency(id) != null;
    }

    @NotNull
    public static UserManager getUserManager() {
        return PLUGIN.getUserManager();
    }

    @NotNull
    public static CurrencyManager getCurrencyManager() {
        return PLUGIN.getCurrencyManager();
    }
}
