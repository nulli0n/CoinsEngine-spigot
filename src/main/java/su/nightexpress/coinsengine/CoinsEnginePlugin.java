package su.nightexpress.coinsengine;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.command.impl.BasicCommands;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.data.DataHandler;
import su.nightexpress.coinsengine.data.UserManager;
import su.nightexpress.coinsengine.hook.DeluxeCoinflipHook;
import su.nightexpress.coinsengine.hook.HookId;
import su.nightexpress.coinsengine.hook.LibreforgeHook;
import su.nightexpress.coinsengine.hook.PlaceholderAPIHook;
import su.nightexpress.coinsengine.migration.MigrationManager;
import su.nightexpress.coinsengine.util.CoinsLogger;
import su.nightexpress.nightcore.NightPlugin;
import su.nightexpress.nightcore.command.experimental.ImprovedCommands;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.util.Plugins;

public class CoinsEnginePlugin extends NightPlugin implements ImprovedCommands {

    private CurrencyManager  currencyManager;
    private MigrationManager migrationManager;
    private DataHandler      dataHandler;
    private UserManager      userManager;

    private CoinsLogger coinsLogger;

    @Override
    public void enable() {
        CoinsEngineAPI.load(this);

        this.coinsLogger = new CoinsLogger(this);
        this.registerCommands();
        
        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this, this.dataHandler);
        this.userManager.setup();

        this.currencyManager = new CurrencyManager(this);
        this.currencyManager.setup();

        this.migrationManager = new MigrationManager(this);
        this.migrationManager.setup();

        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderAPIHook.setup(this);
        }

        if (Plugins.isInstalled(HookId.ECO)) {
            LibreforgeHook.setup(this);
        }

        if (Plugins.isInstalled(HookId.DELUXE_COINFLIP)) {
            this.runTask(task -> DeluxeCoinflipHook.setup(this));
        }
    }

    private void registerCommands() {
        BasicCommands.load(this);
    }

    @Override
    public void disable() {
        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderAPIHook.shutdown();
        }

        if (this.migrationManager != null) this.migrationManager.shutdown();
        if (this.userManager != null) this.userManager.shutdown();
        if (this.dataHandler != null) this.dataHandler.shutdown();
        if (this.currencyManager != null) this.currencyManager.shutdown();

        CoinsEngineAPI.unload();
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
    public CoinsLogger getCoinsLogger() {
        return this.coinsLogger;
    }

    @NotNull
    public CurrencyManager getCurrencyManager() {
        return this.currencyManager;
    }

    @NotNull
    public MigrationManager getMigrationManager() {
        return this.migrationManager;
    }

    @NotNull
    public DataHandler getData() {
        return this.dataHandler;
    }

    @NotNull
    public UserManager getUserManager() {
        return this.userManager;
    }
}
