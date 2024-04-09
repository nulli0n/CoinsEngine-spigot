package su.nightexpress.coinsengine;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.command.base.MigrateCommand;
import su.nightexpress.coinsengine.command.base.ResetCommand;
import su.nightexpress.coinsengine.command.base.WipeCommand;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.data.DataHandler;
import su.nightexpress.coinsengine.data.UserManager;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.hook.DeluxeCoinflipHook;
import su.nightexpress.coinsengine.hook.HookId;
import su.nightexpress.coinsengine.hook.LibreforgeHook;
import su.nightexpress.coinsengine.hook.PlaceholderAPIHook;
import su.nightexpress.coinsengine.migration.MigrationManager;
import su.nightexpress.coinsengine.util.CoinsLogger;
import su.nightexpress.nightcore.NightDataPlugin;
import su.nightexpress.nightcore.command.api.NightPluginCommand;
import su.nightexpress.nightcore.command.base.ReloadSubCommand;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.util.Plugins;

public class CoinsEnginePlugin extends NightDataPlugin<CoinsUser> {

    private CurrencyManager  currencyManager;
    private MigrationManager migrationManager;
    private DataHandler      dataHandler;
    private UserManager      userManager;

    private CoinsLogger coinsLogger;

    @Override
    public void enable() {
        this.coinsLogger = new CoinsLogger(this);
        this.registerCommands();
        
        this.dataHandler = new DataHandler(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this);
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
        NightPluginCommand generalCommand = this.getBaseCommand();
        generalCommand.addChildren(new ReloadSubCommand(this, Perms.COMMAND_RELOAD));
        generalCommand.addChildren(new ResetCommand(this));
        generalCommand.addChildren(new WipeCommand(this));
        generalCommand.addChildren(new MigrateCommand(this));
    }

    @Override
    public void disable() {
        if (Plugins.hasPlaceholderAPI()) {
            PlaceholderAPIHook.shutdown();
        }
        if (this.migrationManager != null) this.migrationManager.shutdown();
        if (this.currencyManager != null) this.currencyManager.shutdown();
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
        return coinsLogger;
    }

    @NotNull
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    @NotNull
    public MigrationManager getMigrationManager() {
        return migrationManager;
    }

    @Override
    @NotNull
    public DataHandler getData() {
        return this.dataHandler;
    }

    @NotNull
    @Override
    public UserManager getUserManager() {
        return userManager;
    }
}
