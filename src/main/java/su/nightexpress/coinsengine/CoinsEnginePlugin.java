package su.nightexpress.coinsengine;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.command.currency.CurrencyCommands;
import su.nightexpress.coinsengine.command.impl.BasicCommands;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.data.DataHandler;
import su.nightexpress.coinsengine.data.UserManager;
import su.nightexpress.coinsengine.hook.DeluxeCoinflipHook;
import su.nightexpress.coinsengine.hook.HookId;
import su.nightexpress.coinsengine.hook.PlaceholderAPIHook;
import su.nightexpress.coinsengine.migration.MigrationManager;
import su.nightexpress.coinsengine.tops.TopManager;
import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.command.experimental.ImprovedCommands;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.util.Plugins;

import java.util.Optional;

public class CoinsEnginePlugin extends NightPlugin implements ImprovedCommands {

    private DataHandler      dataHandler;
    private UserManager      userManager;
    private CurrencyManager  currencyManager;
    private TopManager       topManager;
    private MigrationManager migrationManager;

    @Override
    public void enable() {
        this.loadEngine();
        
        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this, this.dataHandler);
        this.userManager.setup();

        this.currencyManager = new CurrencyManager(this);
        this.currencyManager.setup();

        if (Config.isTopsEnabled()) {
            this.topManager = new TopManager(this);
            this.topManager.setup();
        }

        if (Config.isMigrationEnabled()) {
            this.migrationManager = new MigrationManager(this);
            this.migrationManager.setup();
        }

        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderAPIHook.setup(this);
        }

        if (Plugins.isInstalled(HookId.DELUXE_COINFLIP)) {
            this.runTask(() -> DeluxeCoinflipHook.setup(this));
        }
    }

    private void loadEngine() {
        CoinsEngineAPI.load(this);
        CurrencyCommands.load(this);
        BasicCommands.load(this);
    }

    @Override
    public void disable() {
        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderAPIHook.shutdown();
        }

        if (this.topManager != null) this.topManager.shutdown();
        if (this.migrationManager != null) this.migrationManager.shutdown();
        if (this.userManager != null) this.userManager.shutdown();
        if (this.dataHandler != null) this.dataHandler.shutdown();
        if (this.currencyManager != null) this.currencyManager.shutdown();

        CurrencyCommands.clear();
        CoinsEngineAPI.clear();
    }

    @Override
    @NotNull
    protected PluginDetails getDefaultDetails() {
        return PluginDetails.create("Economy", new String[]{"coinsengine", "coe"})
            .setConfigClass(Config.class)
            .setLangClass(Lang.class)
            .setPermissionsClass(Perms.class);
    }

    @NotNull
    public CurrencyManager getCurrencyManager() {
        return this.currencyManager;
    }

    @NotNull
    public Optional<TopManager> getTopManager() {
        return Optional.ofNullable(this.topManager);
    }

    @NotNull
    public Optional<MigrationManager> getMigrationManager() {
        return Optional.ofNullable(this.migrationManager);
    }

    @NotNull
    public DataHandler getDataHandler() {
        return this.dataHandler;
    }

    @NotNull
    public UserManager getUserManager() {
        return this.userManager;
    }
}
