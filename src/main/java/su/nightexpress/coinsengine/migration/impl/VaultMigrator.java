package su.nightexpress.coinsengine.migration.impl;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.migration.Migrator;
import su.nightexpress.nightcore.util.Plugins;

import java.util.HashMap;
import java.util.Map;

public class VaultMigrator extends Migrator {

    public VaultMigrator(@NotNull CoinsEnginePlugin plugin) {
        super(plugin, Plugins.VAULT);
    }

    @Override
    public boolean canMigrate(@NotNull Currency currency) {
        Currency vaultCurrency = this.plugin.getCurrencyManager().getVaultCurrency().orElse(null);
        return vaultCurrency != currency;
    }

    @Override
    @NotNull
    public Map<OfflinePlayer, Double> getBalances(@NotNull Currency currency) {
        Map<OfflinePlayer, Double> balances = new HashMap<>();

        RegisteredServiceProvider<Economy> provider = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (provider == null) return balances;

        Economy economy = provider.getProvider();

        for (OfflinePlayer offlinePlayer : this.plugin.getServer().getOfflinePlayers()) {
            try {
                balances.put(offlinePlayer, economy.getBalance(offlinePlayer));
            }
            catch (Exception exception) {
                this.plugin.error("Could not convert Vault <-> Economy balance for '" + offlinePlayer.getUniqueId() + "'! See stacktrace for details:");
                exception.printStackTrace();
            }
        }

        return balances;
    }
}
