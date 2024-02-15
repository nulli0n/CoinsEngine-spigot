package su.nightexpress.coinsengine.migration;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.Map;

public abstract class MigrationPlugin {

    protected final CoinsEnginePlugin plugin;
    protected final String            pluginName;

    public MigrationPlugin(@NotNull CoinsEnginePlugin plugin, @NotNull String pluginName) {
        this.plugin = plugin;
        this.pluginName = pluginName;
    }

    @NotNull
    public String getPluginName() {
        return pluginName;
    }

    @Nullable
    public Plugin getBackendPlugin() {
        return this.plugin.getPluginManager().getPlugin(this.getPluginName());
    }

    @NotNull
    public abstract Map<OfflinePlayer, Double> getBalances(@NotNull Currency currency);
}
