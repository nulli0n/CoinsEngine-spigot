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

public class CoinsEngineAPI {

    public static final CoinsEngine PLUGIN = CoinsEngine.getPlugin(CoinsEngine.class);

    public static double getBalance(@NotNull Player player, @NotNull Currency currency) {
        return getUserData(player).getBalance(currency);
    }

    public static void addBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        getUserData(player).addBalance(currency, amount);
    }

    public static void setBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        getUserData(player).setBalance(currency, amount);
    }

    public static void removeBalance(@NotNull Player player, @NotNull Currency currency, double amount) {
        getUserData(player).removeBalance(currency, amount);
    }

    @NotNull
    public static CoinsUser getUserData(@NotNull Player player) {
        return PLUGIN.getUserManager().getUserData(player);
    }

    @Nullable
    public static CoinsUser getUserData(@NotNull String name) {
        return PLUGIN.getUserManager().getUserData(name);
    }

    @Nullable
    public static CoinsUser getUserData(@NotNull UUID uuid) {
        return PLUGIN.getUserManager().getUserData(uuid);
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
