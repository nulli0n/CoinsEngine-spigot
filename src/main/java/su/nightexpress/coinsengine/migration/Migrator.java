package su.nightexpress.coinsengine.migration;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.Map;

public abstract class Migrator {

    protected final CoinsEnginePlugin plugin;
    protected final String            name;

    public Migrator(@NotNull CoinsEnginePlugin plugin, @NotNull String name) {
        this.plugin = plugin;
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public Plugin getBackend() {
        return this.plugin.getPluginManager().getPlugin(this.name);
    }

    public abstract boolean canMigrate(@NotNull Currency currency);

    @NotNull
    public abstract Map<OfflinePlayer, Double> getBalances(@NotNull Currency currency);
}
