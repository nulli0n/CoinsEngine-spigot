package su.nightexpress.coinsengine.migration.impl;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.migration.MigrationPlugin;
import su.nightexpress.nightcore.integration.VaultHook;
import su.nightexpress.nightcore.util.Plugins;

import java.util.HashMap;
import java.util.Map;

public class VaultPlugin extends MigrationPlugin {

    public VaultPlugin(@NotNull CoinsEnginePlugin plugin) {
        super(plugin, Plugins.VAULT);
    }

    @Override
    @NotNull
    public Map<OfflinePlayer, Double> getBalances(@NotNull Currency currency) {
        Map<OfflinePlayer, Double> balances = new HashMap<>();

        Economy economy = VaultHook.getEconomy();
        if (economy == null) return balances;

        for (OfflinePlayer offlinePlayer : this.plugin.getServer().getOfflinePlayers()) {
            try {
                balances.put(offlinePlayer, economy.getBalance(offlinePlayer));
            }
            catch (Exception exception) {
                this.plugin.error("Could not convert Vault <-> Economy balance for '" + offlinePlayer.getUniqueId() + "'! See error for details:");
                exception.printStackTrace();
            }
        }

        return balances;
    }
}
