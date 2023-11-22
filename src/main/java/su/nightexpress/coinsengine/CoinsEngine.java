package su.nightexpress.coinsengine;

import fun.lewisdev.deluxecoinflip.api.DeluxeCoinflipAPI;
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
import su.nightexpress.coinsengine.hook.DeluxeCoinflipHook;
import su.nightexpress.coinsengine.hook.HookId;
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

        /*this.getCurrencyManager().getVaultCurrency().ifPresent((currency) -> {
            int count = 0;
            for (JYML cfg : JYML.loadAll(this.getDataFolder() + "/data/")) {
                try {
                    UUID uuid = UUID.fromString(cfg.getString("UUID", ""));
                    double balance = cfg.getDouble("Balance");
                    if (!this.getData().isUserExists(uuid) && balance > 0D) {
                        OfflinePlayer offlinePlayer = this.getServer().getOfflinePlayer(uuid);
                        String name = offlinePlayer.getName();
                        if (name == null) name = "UnknownName" + (count++);

                        CoinsUser user = new CoinsUser(this, uuid, name);
                        user.getCurrencyData(currency).setBalance(balance);
                        this.getData().addUser(user);
                        this.info("Migrated user balance: '" + uuid + "' with " + balance);
                    }
                }
                catch (IllegalArgumentException ignored) {

                }
            }
        });

        try (BufferedReader reader = new BufferedReader(new FileReader(this.getDataFolder() + "/variables.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("shards") || line.startsWith("shinyshards")) {
                    String[] splitType = line.split("::");
                    String curId = splitType[0];

                    Currency currency = this.getCurrencyManager().getCurrency(curId);
                    if (currency == null) continue;

                    String[] data = splitType[1].split(",");
                    UUID userId = UUID.fromString(data[0].trim());
                    long amount = Long.parseLong(data[2].trim(), 16);
                    if (amount == 0) continue;

                    boolean created = true;
                    CoinsUser user = this.getData().getUser(userId);
                    if (user == null) {
                        user = new CoinsUser(this, userId, userId.toString());
                        created = false;
                    }

                    user.getCurrencyData(currency).setBalance(amount);
                    this.info("Migrated skript data: Currency: " + curId + ", User Id: " + userId + ", Amount: " + amount);

                    if (!created) {
                        this.getData().addUser(user);
                    }
                    else {
                        this.getData().saveUser(user);
                    }
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }*/

        if (EngineUtils.hasPlaceholderAPI()) {
            PlaceholderAPIHook.setup(this);
        }
        if (EngineUtils.hasPlugin(HookId.DELUXE_COINFLIP)) {
            DeluxeCoinflipHook.setup(this);
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
