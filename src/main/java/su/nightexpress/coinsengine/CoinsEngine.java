package su.nightexpress.coinsengine;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.data.UserDataHolder;
import su.nexmedia.engine.command.list.ReloadSubCommand;
import su.nexmedia.engine.utils.EngineUtils;
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
import su.nightexpress.coinsengine.hook.PlaceholderAPIHook;

public class CoinsEngine extends NexPlugin<CoinsEngine> implements UserDataHolder<CoinsEngine, CoinsUser> {

    private CurrencyManager currencyManager;
    private DataHandler     dataHandler;
    private UserManager     userManager;

    @Override
    @NotNull
    protected CoinsEngine getSelf() {
        return this;
    }

    @Override
    public void enable() {
        this.currencyManager = new CurrencyManager(this);
        this.currencyManager.setup();

        if (EngineUtils.hasPlaceholderAPI()) {
            PlaceholderAPIHook.setup();
        }
    }

    @Override
    public void disable() {
        if (EngineUtils.hasPlaceholderAPI()) {
            PlaceholderAPIHook.shutdown();
        }
        if (this.currencyManager != null) {
            this.currencyManager.shutdown();
            this.currencyManager = null;
        }
    }

    @Override
    public boolean setupDataHandlers() {
        this.dataHandler = DataHandler.getInstance(this);
        this.dataHandler.setup();

        this.userManager = new UserManager(this);
        this.userManager.setup();

        return true;
    }

    @Override
    public void loadConfig() {
        this.getConfig().initializeOptions(Config.class);
    }

    @Override
    public void loadLang() {
        this.getLangManager().loadMissing(Lang.class);
    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<CoinsEngine> generalCommand) {
        generalCommand.addChildren(new ReloadSubCommand<>(this, Perms.COMMAND_RELOAD));
        generalCommand.addChildren(new ResetCommand(this));
        generalCommand.addChildren(new WipeCommand(this));
        generalCommand.addChildren(new MigrateCommand(this));
    }

    @Override
    public void registerHooks() {

    }

    @Override
    public void registerPermissions() {
        this.registerPermissions(Perms.class);
    }

    @NotNull
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
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
