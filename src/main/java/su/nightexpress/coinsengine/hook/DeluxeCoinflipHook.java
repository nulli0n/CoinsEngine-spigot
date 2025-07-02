package su.nightexpress.coinsengine.hook;

import net.zithium.deluxecoinflip.api.DeluxeCoinflipAPI;
import net.zithium.deluxecoinflip.economy.provider.EconomyProvider;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyOperations;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public class DeluxeCoinflipHook {

    public static void setup(@NotNull CoinsEnginePlugin plugin) {
        DeluxeCoinflipAPI api = (DeluxeCoinflipAPI) plugin.getPluginManager().getPlugin(HookId.DELUXE_COINFLIP);
        if (api == null) return;

        plugin.getCurrencyManager().getCurrencies().forEach(currency -> {
            Provider provider = new Provider(plugin, currency);
            api.registerEconomyProvider(provider, plugin.getName());
        });
    }

    public static void shutdown() {

    }

    private static class Provider extends EconomyProvider {

        private final CoinsEnginePlugin plugin;
        private final CurrencyManager manager;
        private final Currency          currency;

        public Provider(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
            super("coinsengine_" + currency.getId());
            this.plugin = plugin;
            this.manager = plugin.getCurrencyManager();
            this.currency = currency;
        }

        @Override
        public void onEnable() {

        }

        @Override
        public String getDisplayName() {
            return this.currency.getName();
        }

        @Nullable
        private CoinsUser getUser(@NotNull OfflinePlayer offlinePlayer) {
            return this.plugin.getUserManager().getOrFetch(offlinePlayer.getUniqueId());
        }

        @Override
        public double getBalance(OfflinePlayer offlinePlayer) {
            CoinsUser user = this.getUser(offlinePlayer);
            return user == null ? 0 : user.getBalance(this.currency);
        }

        @Override
        public void withdraw(OfflinePlayer offlinePlayer, double amount) {
            CoinsUser user = this.getUser(offlinePlayer);
            if (user == null) return;

            this.manager.performOperation(CurrencyOperations.forRemoveSilently(this.currency, amount, user));
        }

        @Override
        public void deposit(OfflinePlayer offlinePlayer, double amount) {
            CoinsUser user = this.getUser(offlinePlayer);
            if (user == null) return;

            this.manager.performOperation(CurrencyOperations.forAddSilently(this.currency, amount, user));
        }
    }
}
