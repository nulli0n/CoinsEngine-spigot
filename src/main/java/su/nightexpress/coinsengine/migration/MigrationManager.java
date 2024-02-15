package su.nightexpress.coinsengine.migration;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.hook.HookId;
import su.nightexpress.coinsengine.migration.impl.PlayerPointsPlugin;
import su.nightexpress.coinsengine.migration.impl.VaultPlugin;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.Plugins;

import java.util.*;
import java.util.function.Supplier;

public class MigrationManager extends SimpleManager<CoinsEnginePlugin> {

    private final Map<String, MigrationPlugin> pluginMap;

    public MigrationManager(@NotNull CoinsEnginePlugin plugin) {
        super(plugin);
        this.pluginMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.registerMigrator(HookId.PLAYER_POINTS, () -> new PlayerPointsPlugin(this.plugin));
        this.registerMigrator(Plugins.VAULT, () -> new VaultPlugin(this.plugin));
    }

    @Override
    protected void onShutdown() {

    }

    public boolean registerMigrator(@NotNull String name, @NotNull Supplier<MigrationPlugin> supplier) {
        if (!Plugins.isInstalled(name)) return false;

        MigrationPlugin migrationPlugin = supplier.get();
        this.pluginMap.put(migrationPlugin.getPluginName().toLowerCase(), migrationPlugin);
        this.plugin.info("Detected plugin available for data migration: " + migrationPlugin.getPluginName());

        return true;
    }

    public boolean migrate(@NotNull String pluginName, @NotNull Currency currency) {
        MigrationPlugin migrationPlugin = this.getPlugin(pluginName);
        if (migrationPlugin == null) return false;

        this.migrate(migrationPlugin, currency);
        return true;
    }

    public void migrate(@NotNull MigrationPlugin migrationPlugin, @NotNull Currency currency) {
        Map<OfflinePlayer, Double> balances = migrationPlugin.getBalances(currency);
        balances.forEach((player, points) -> {
            String name = player.getName();
            if (name == null) return;

            UUID uuid = player.getUniqueId();
            CoinsUser user = this.plugin.getUserManager().getUserData(uuid);
            if (user == null) {
                user = new CoinsUser(this.plugin, uuid, name);
                this.plugin.getData().addUser(user);
            }
            user.getCurrencyData(currency).setBalance(points);
            this.plugin.getUserManager().save(user);
        });
    }

    @NotNull
    public List<String> getMigrationPluginNames() {
        return new ArrayList<>(this.getPluginMap().keySet());
    }

    @NotNull
    public Map<String, MigrationPlugin> getPluginMap() {
        return pluginMap;
    }

    @Nullable
    public MigrationPlugin getPlugin(@NotNull String name) {
        return this.getPluginMap().get(name.toLowerCase());
    }
}
