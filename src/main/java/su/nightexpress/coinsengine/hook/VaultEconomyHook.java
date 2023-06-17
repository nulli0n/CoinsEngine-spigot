package su.nightexpress.coinsengine.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.currency.impl.CurrencyEconomy;

public class VaultEconomyHook {

    private static CurrencyEconomy economy;

    public static void setup(@NotNull CoinsEngine plugin, @NotNull Currency currency) {
        economy = new CurrencyEconomy(plugin, currency);

        ServicesManager services = plugin.getServer().getServicesManager();
        services.register(Economy.class, economy, plugin, ServicePriority.High);

        plugin.info("Registered '" + currency.getId() + "' as Vault Economy!");
    }

    public static void shutdown() {
        if (economy != null) {
            ServicesManager services = Bukkit.getServer().getServicesManager();
            services.unregister(Economy.class, economy);
            economy = null;
        }
    }
}
