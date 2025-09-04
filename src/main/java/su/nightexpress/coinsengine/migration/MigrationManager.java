package su.nightexpress.coinsengine.migration;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.hook.HookId;
import su.nightexpress.coinsengine.migration.command.MigrationCommands;
import su.nightexpress.coinsengine.migration.impl.PlayerPointsMigrator;
import su.nightexpress.coinsengine.migration.impl.VaultMigrator;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.Plugins;

import java.util.*;
import java.util.function.Supplier;

public class MigrationManager extends SimpleManager<CoinsEnginePlugin> {

    private final Map<String, Migrator> migrators;

    public MigrationManager(@NotNull CoinsEnginePlugin plugin) {
        super(plugin);
        this.migrators = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.registerMigrator(HookId.PLAYER_POINTS, () -> new PlayerPointsMigrator(this.plugin));
        this.registerMigrator(Plugins.VAULT, () -> new VaultMigrator(this.plugin));

        MigrationCommands.load(this.plugin, this);
    }

    @Override
    protected void onShutdown() {
        this.migrators.clear();
    }

    public boolean registerMigrator(@NotNull String name, @NotNull Supplier<Migrator> supplier) {
        if (!Plugins.isInstalled(name)) return false;

        Migrator migrator = supplier.get();
        this.migrators.put(migrator.getName().toLowerCase(), migrator);
        this.plugin.info("Available balance data migration from " + migrator.getName() + ".");

        return true;
    }

    public boolean startMigration(@NotNull CommandSender sender, @NotNull String name, @NotNull Currency currency) {
        if (!this.plugin.getCurrencyManager().canPerformOperations()) {
            Lang.MIGRATION_START_BLOCKED.getMessage().send(sender);
            return false;
        }

        Migrator migrator = this.getMigrator(name);
        if (migrator == null) {
            Lang.MIGRATION_START_BAD_PLUGIN.getMessage().send(sender);
            return false;
        }

        if (!migrator.canMigrate(currency)) {
            Lang.MIGRATION_START_BAD_CURRENCY.getMessage().send(sender, replacer -> replacer
                .replace(Placeholders.GENERIC_NAME, migrator.getName())
                .replace(currency.replacePlaceholders())
            );
            return false;
        }

        this.plugin.runTaskAsync(() -> {
            this.plugin.getCurrencyManager().disableOperations();
            Lang.MIGRATION_STARTED.getMessage().send(sender, replacer -> replacer.replace(Placeholders.GENERIC_NAME, migrator.getName()));
            this.migrate(migrator, currency);
            Lang.MIGRATION_COMPLETED.getMessage().send(sender, replacer -> replacer.replace(Placeholders.GENERIC_NAME, migrator.getName()));
            this.plugin.getCurrencyManager().allowOperations();
        });

        return true;
    }

    public void migrate(@NotNull Migrator migrator, @NotNull Currency currency) {
        Map<OfflinePlayer, Double> balances = migrator.getBalances(currency);
        balances.forEach((player, amount) -> {
            String name = player.getName();
            if (name == null) return;

            UUID uuid = player.getUniqueId();
            CoinsUser user = this.plugin.getUserManager().getOrFetch(uuid);
            if (user == null) {
                user = CoinsUser.create(uuid, name);
                this.plugin.getDataHandler().insertUser(user);
            }

            user.setBalance(currency, amount);
            this.plugin.getUserManager().save(user);
        });
    }

    @NotNull
    public List<String> getMigratorNames() {
        return new ArrayList<>(this.migrators.keySet());
    }

    @NotNull
    public Map<String, Migrator> getMigratorMap() {
        return this.migrators;
    }

    @Nullable
    public Migrator getMigrator(@NotNull String name) {
        return this.migrators.get(name.toLowerCase());
    }
}
