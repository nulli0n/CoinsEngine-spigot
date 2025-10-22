package su.nightexpress.coinsengine.migration;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.coinsengine.data.DataHandler;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.hook.HookPlugin;
import su.nightexpress.coinsengine.migration.command.MigrationCommands;
import su.nightexpress.coinsengine.migration.impl.PlayerPointsMigrator;
import su.nightexpress.coinsengine.user.UserManager;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Plugins;

import java.util.*;
import java.util.function.Supplier;

public class MigrationManager extends SimpleManager<CoinsEnginePlugin> {

    private final DataHandler dataHandler;
    private final UserManager userManager;
    private final CurrencyRegistry currencyRegistry;
    private final CurrencyManager currencyManager;

    private final Map<String, Migrator> migrators;

    public MigrationManager(@NotNull CoinsEnginePlugin plugin,
                            @NotNull DataHandler dataHandler,
                            @NotNull UserManager userManager,
                            @NotNull CurrencyRegistry currencyRegistry,
                            @NotNull CurrencyManager currencyManager) {
        super(plugin);
        this.dataHandler = dataHandler;
        this.userManager = userManager;
        this.currencyRegistry = currencyRegistry;
        this.currencyManager = currencyManager;
        this.migrators = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.registerMigrator(HookPlugin.PLAYER_POINTS, () -> new PlayerPointsMigrator(this.plugin));

        // Schedule to ensure 3rd party economy plugins are loaded.
        this.plugin.runTask(() -> {
            if (!this.currencyRegistry.hasPrimary()) {
                this.registerMigrator(HookPlugin.VAULT, () -> MigratorFactory.forVault(this.plugin));
            }
        });

        new MigrationCommands(this.plugin, this, this.currencyRegistry).load();
    }

    @Override
    protected void onShutdown() {
        this.migrators.clear();
    }

    public boolean registerMigrator(@NotNull String name, @NotNull Supplier<Migrator> supplier) {
        if (!Plugins.isInstalled(name)) return false;

        Migrator migrator = supplier.get();
        if (migrator == null) return false;

        this.migrators.put(LowerCase.INTERNAL.apply(migrator.getName()), migrator);
        this.plugin.info("Available balance data migration from " + migrator.getName() + ".");

        return true;
    }

    public boolean startMigration(@NotNull CommandSender sender, @NotNull String name, @NotNull Currency currency) {
        if (!this.currencyManager.canPerformOperations()) {
            Lang.MIGRATION_START_BLOCKED.message().send(sender);
            return false;
        }

        Migrator migrator = this.getMigrator(name);
        if (migrator == null) {
            Lang.MIGRATION_START_BAD_PLUGIN.message().send(sender);
            return false;
        }

        if (!migrator.canMigrate(currency)) {
            Lang.MIGRATION_START_BAD_CURRENCY.message().send(sender, replacer -> replacer
                .replace(Placeholders.GENERIC_NAME, migrator.getName())
                .replace(currency.replacePlaceholders())
            );
            return false;
        }

        this.plugin.runTaskAsync(task -> {
            this.currencyManager.disableOperations();
            Lang.MIGRATION_STARTED.message().send(sender, replacer -> replacer.replace(Placeholders.GENERIC_NAME, migrator.getName()));
            this.migrate(migrator, currency);
            Lang.MIGRATION_COMPLETED.message().send(sender, replacer -> replacer.replace(Placeholders.GENERIC_NAME, migrator.getName()));
            this.currencyManager.allowOperations();
        });

        return true;
    }

    public void migrate(@NotNull Migrator migrator, @NotNull Currency currency) {
        Map<OfflinePlayer, Double> balances = migrator.getBalances(currency);
        balances.forEach((player, amount) -> {
            String name = player.getName();
            if (name == null) return;

            UUID uuid = player.getUniqueId();
            CoinsUser user = this.userManager.getOrFetch(uuid);
            if (user == null) {
                user = this.userManager.create(uuid, name);
                this.dataHandler.insertUser(user);
            }

            user.setBalance(currency, amount);
            this.userManager.save(user);
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
        return this.migrators.get(LowerCase.INTERNAL.apply(name));
    }
}
