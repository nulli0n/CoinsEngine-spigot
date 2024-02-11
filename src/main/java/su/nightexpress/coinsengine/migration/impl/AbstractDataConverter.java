package su.nightexpress.coinsengine.migration.impl;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;

public abstract class AbstractDataConverter {

    protected final CoinsEngine plugin;
    protected final String      pluginName;

    public AbstractDataConverter(@NotNull CoinsEngine plugin, @NotNull String pluginName) {
        this.plugin = plugin;
        this.pluginName = pluginName;
    }

    @NotNull
    public String getPluginName() {
        return pluginName;
    }

    @Nullable
    public Plugin getTargetPlugin() {
        return this.plugin.getPluginManager().getPlugin(this.getPluginName());
    }

    public abstract void migrate(@NotNull Currency currency);
}
