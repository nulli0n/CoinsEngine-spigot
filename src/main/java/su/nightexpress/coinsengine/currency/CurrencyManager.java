package su.nightexpress.coinsengine.currency;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.COEFiles;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.impl.AbstractCurrency;
import su.nightexpress.coinsengine.currency.impl.NormalCurrency;
import su.nightexpress.coinsengine.currency.operation.NotificationTarget;
import su.nightexpress.coinsengine.currency.operation.OperationContext;
import su.nightexpress.coinsengine.currency.operation.OperationExecutor;
import su.nightexpress.coinsengine.currency.operation.OperationResult;
import su.nightexpress.coinsengine.data.DataHandler;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencySettings;
import su.nightexpress.coinsengine.hook.HookPlugin;
import su.nightexpress.coinsengine.user.UserManager;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.db.AbstractUser;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Strings;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CurrencyManager extends AbstractManager<CoinsEnginePlugin> {

    private final CurrencyRegistry registry;
    private final DataHandler      dataHandler;
    private final UserManager      userManager;

    private boolean        operationsAllowed;
    private CurrencyLogger logger;

    public CurrencyManager(@NotNull CoinsEnginePlugin plugin, @NotNull CurrencyRegistry registry, @NotNull DataHandler dataHandler, @NotNull UserManager userManager) {
        super(plugin);
        this.registry = registry;
        this.dataHandler = dataHandler;
        this.userManager = userManager;
        this.allowOperations();
    }

    @Override
    protected void onLoad() {
        this.createDefaults();
        this.migrateSettings();
        FileUtil.findYamlFiles(this.getDirectory()).forEach(this::loadCurrency);

        try {
            this.loadLogger();
        }
        catch (IOException | IllegalArgumentException exception) {
            this.plugin.error("Could not create operations logger: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    @Override
    protected void onShutdown() {
        this.registry.getCurrencies().forEach(this::unregisterCurrency);

        if (this.logger != null) this.logger.shutdown();
        this.disableOperations();
    }

    private void migrateSettings() {
        FileUtil.findYamlFiles(this.getDirectory()).forEach(path -> {
            String fileName = path.getFileName().toString();
            if (!fileName.endsWith(FileConfig.EXTENSION)) return;

            FileConfig config = FileConfig.load(path);
            if (!config.contains("Economy")) return;

            if (config.getBoolean("Economy.Vault")) {
                String name = fileName.substring(0, fileName.length() - FileConfig.EXTENSION.length());
                Config.INTEGRATION_VAULT_ECONOMY_CURRENCY.set(name);
                Config.INTEGRATION_VAULT_ECONOMY_CURRENCY.write(this.plugin.getConfig());
            }

            config.remove("Economy");
            config.saveChanges();
        });
    }

    private void loadCurrency(@NotNull Path path) throws IllegalStateException {
        String fileName = path.getFileName().toString();
        if (!fileName.endsWith(FileConfig.EXTENSION)) return;

        String name = fileName.substring(0, fileName.length() - FileConfig.EXTENSION.length());
        String id = Strings.varStyle(name).orElseThrow(() -> new IllegalStateException("Malformed file name '" + fileName + "'"));

        boolean isVault = Plugins.isInstalled(HookPlugin.VAULT) && Config.INTEGRATION_VAULT_ENABLED.get();
        boolean isGoodId = Config.INTEGRATION_VAULT_ECONOMY_CURRENCY.get().equalsIgnoreCase(id);

        AbstractCurrency currency;

        if (isVault && isGoodId) {
            currency = CurrencyFactory.createEconomy(path, id, this.plugin, this, this.dataHandler, this.userManager);
        }
        else {
            currency = CurrencyFactory.createNormal(path, id);
        }

        // Currently useless, but will be useful once we remake the plugin reload system.
        if (currency.isPrimary() && this.registry.hasPrimary()) {
            this.plugin.warn("Could not load primary currency '" + currency.getId() + "' as there is already one present. Reboot the server if you want to change your primary currency.");
            return;
        }

        currency.load();

        this.registerCurrency(currency);
    }

    private void createDefaults() {
        File dir = new File(this.getDirectory());
        if (dir.exists()) return;

        this.createCurrency("coins", currency -> {
            currency.setSymbol("⛂");
            currency.setIcon(NightItem.fromType(Material.SUNFLOWER));
            currency.setDecimal(false);
        });

        this.createCurrency("money", currency -> {
            currency.setSymbol("$");
            currency.setFormat(Placeholders.CURRENCY_SYMBOL + Placeholders.GENERIC_AMOUNT);
            currency.setFormat(currency.getFormat());
            currency.setIcon(NightItem.fromType(Material.GOLD_NUGGET));
            currency.setDecimal(true);
        });
    }

    private void loadLogger() throws IOException, IllegalArgumentException {
        boolean logToConsole = Config.LOGS_TO_CONSOLE.get();
        boolean logToFile = Config.LOGS_TO_FILE.get();
        if (!logToConsole && !logToFile) return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.LOGS_DATE_FORMAT.get());
        Path filePath = Paths.get(this.plugin.getDataFolder().getAbsolutePath(), COEFiles.FILE_OPERATIONS);

        this.logger = new CurrencyLogger(this.plugin, formatter, filePath, logToConsole, logToFile);
        this.addAsyncTask(() -> this.logger.write(), Config.LOGS_WRITE_INTERVAL.get());
    }

    @NotNull
    public String getDirectory() {
        return this.plugin.getDataFolder() + COEFiles.DIR_CURRENCIES;
    }

    public void registerCurrency(@NotNull Currency currency) {
        if (this.registry.isRegistered(currency.getId())) {
            this.plugin.error("Could not register duplicated currency: '" + currency.getId() + "'!");
            return;
        }

        if (DataHandler.isCurrencyColumnCached(currency)) {
            this.plugin.error("Currency '" + currency.getId() + "' tried to use column '" + currency.getColumnName() + "' which is already used by other currency!");
            return;
        }

        this.registry.add(currency);
        this.dataHandler.onCurrencyRegister(currency);
        this.plugin.info("Currency registered: '" + currency.getId() + "'.");
    }

    public boolean unregisterCurrency(@NotNull Currency currency) {
        return this.unregisterCurrency(currency.getId());
    }

    public boolean unregisterCurrency(@NotNull String id) {
        Currency currency = this.registry.remove(id);
        if (currency == null) return false;

        this.dataHandler.onCurrencyUnload(currency);
        this.plugin.info("Currency unregistered: '" + currency.getId() + "'.");
        return true;
    }

    @NotNull
    @Deprecated
    public Collection<Currency> getCurrencies() {
        return this.registry.getCurrencies();
    }

    public void allowOperations() {
        this.operationsAllowed = true;
        this.dataHandler.setSynchronizationActive(true);
    }

    public void disableOperations() {
        this.operationsAllowed = false;
        this.dataHandler.setSynchronizationActive(false);
    }

    public boolean canPerformOperations() {
        return this.operationsAllowed;
    }

    private boolean assertOperationsEnabled(@NotNull OperationContext context) {
        if (!this.canPerformOperations()) {
            context.getBukkitSender().ifPresent(sender -> Lang.CURRENCY_OPERATION_DISABLED.message().send(sender));
            return false;
        }
        return true;
    }

    @NotNull
    public NormalCurrency createCurrency(@NotNull String id, @NotNull Consumer<NormalCurrency> consumer) {
        Path path = Paths.get(this.getDirectory(), FileConfig.withExtension(id));
        NormalCurrency currency = new NormalCurrency(path, id);

        consumer.accept(currency);
        currency.write();
        return currency;
    }

    public boolean createCurrency(@NotNull CommandSender sender, @NotNull String name, @NotNull String symbol, boolean decimals) {
        String id = Strings.varStyle(name).orElse(null);
        if (id == null) {
            Lang.CURRENCY_CREATE_BAD_NAME.message().send(sender);
            return false;
        }

        if (this.registry.isRegistered(id)) {
            Lang.CURRENCY_CREATE_DUPLICATED.message().send(sender);
            return false;
        }

        NormalCurrency created = this.createCurrency(id, currency -> {
            currency.setSymbol(symbol);
            currency.setDecimal(decimals);
        });

        created.updateMessagePrefix();

        this.registerCurrency(created);
        this.plugin.getCommander().getCurrencyCommands().loadCommands(created);

        Lang.CURRENCY_CREATE_SUCCESS.message().send(sender, replacer -> replacer.replace(created.replacePlaceholders()));
        return true;
    }

    public void resetBalances(@NotNull CommandSender sender) {
        this.resetBalances(sender, null);
    }

    public void resetBalances(@NotNull CommandSender sender, @Nullable Currency currency) {
        if (!this.canPerformOperations()) {
            Lang.RESET_ALL_START_BLOCKED.message().send(sender);
            return;
        }

        this.plugin.runTaskAsync(task -> {
            this.disableOperations();
            if (currency == null) {
                Collection<Currency> currencies = this.registry.getCurrencies();

                Lang.RESET_ALL_STARTED_GLOBAL.message().send(sender);
                this.dataHandler.resetBalances(currencies);
                this.userManager.getLoaded().forEach(user -> user.resetBalance(currencies));
                Lang.RESET_ALL_COMPLETED_GLOBAL.message().send(sender);
            }
            else {
                Lang.RESET_ALL_STARTED_CURRENCY.message().send(sender, replacer -> replacer.replace(currency.replacePlaceholders()));
                this.dataHandler.resetBalances(currency);
                this.userManager.getLoaded().forEach(user -> user.resetBalance(currency));
                Lang.RESET_ALL_COMPLETED_CURRENCY.message().send(sender, replacer -> replacer.replace(currency.replacePlaceholders()));
            }
            this.allowOperations();
        });
    }

    public void showBalance(@NotNull CommandSender sender, @NotNull Currency currency) {
        this.showBalance(sender, sender.getName(), currency);
    }

    public void showBalance(@NotNull CommandSender sender, @NotNull String name, @NotNull Currency currency) {
        boolean isOwn = sender.getName().equalsIgnoreCase(name);

        this.userManager.manageUser(name, user -> {
            if (user == null) {
                CoreLang.ERROR_INVALID_PLAYER.withPrefix(this.plugin).send(sender);
                return;
            }

            currency.sendPrefixed((isOwn ? Lang.CURRENCY_BALANCE_DISPLAY_OWN : Lang.CURRENCY_BALANCE_DISPLAY_OTHERS), sender, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
            );
        });
    }

    public void showWallet(@NotNull Player player) {
        this.showWallet(player, player.getName());
    }

    public void showWallet(@NotNull CommandSender sender, @NotNull String name) {
        boolean isOwn = sender.getName().equalsIgnoreCase(name);

        this.userManager.manageUser(name, user -> {
            if (user == null) {
                CoreLang.ERROR_INVALID_PLAYER.withPrefix(this.plugin).send(sender);
                return;
            }

            (isOwn ? Lang.CURRENCY_WALLET_OWN : Lang.CURRENCY_WALLET_OTHERS).message().send(sender, replacer -> replacer
                .replace(Placeholders.GENERIC_ENTRY, list -> {
                    this.registry.getCurrencies().stream().sorted(Comparator.comparing(Currency::getId)).forEach(currency -> {
                        if (sender instanceof Player player && !currency.hasPermission(player)) return;

                        list.add(Replacer.create()
                            .replace(currency.replacePlaceholders())
                            .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                            .apply(Lang.CURRENCY_WALLET_ENTRY.text())
                        );
                    });
                })
                .replace(Placeholders.PLAYER_NAME, user.getName())
            );
        });
    }

    public void togglePayments(@NotNull Player player, @NotNull Currency currency) {
        this.togglePayments(player, player.getName(), currency, false);
    }

    public void togglePayments(@NotNull CommandSender sender, @NotNull String name, @NotNull Currency currency, boolean silent) {
        boolean isOwn = sender.getName().equalsIgnoreCase(name);

        this.userManager.manageUser(name, user -> {
            if (user == null) {
                CoreLang.ERROR_INVALID_PLAYER.withPrefix(this.plugin).send(sender);
                return;
            }

            CurrencySettings settings = user.getSettings(currency);
            settings.setPaymentsEnabled(!settings.isPaymentsEnabled());
            this.userManager.save(user);

            if (!isOwn) {
                currency.sendPrefixed(Lang.COMMAND_CURRENCY_PAYMENTS_TARGET, sender, replacer -> replacer
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_STATE, CoreLang.STATE_ENABLED_DISALBED.get(settings.isPaymentsEnabled()))
                );
            }

            Player target = user.getPlayer();
            if (!silent && target != null) {
                currency.sendPrefixed(Lang.COMMAND_CURRENCY_PAYMENTS_TOGGLE, target, replacer -> replacer
                    .replace(Placeholders.GENERIC_STATE, CoreLang.STATE_ENABLED_DISALBED.get(settings.isPaymentsEnabled()))
                );
            }
        });
    }

    @NotNull
    public OperationResult give(@NotNull OperationContext context, @NotNull Player player, @NotNull Currency currency, double amount) {
        return this.give(context, this.userManager.getOrFetch(player), currency, amount);
    }

    @NotNull
    public OperationResult give(@NotNull OperationContext context, @NotNull CoinsUser user, @NotNull Currency currency, double amount) {
        if (!this.assertOperationsEnabled(context)) return OperationResult.FAILURE;

        OperationExecutor executor = context.getExecutor();

        user.addBalance(currency, amount);
        this.userManager.save(user);

        if (this.logger != null && context.shouldNotifyLogger()) {
            this.logger.addEntry(context, "[%s] %s gave %s to %s. New balance: %s"
                .formatted(currency.getId(), executor.getName(), currency.format(amount), user.getName(), currency.format(user.getBalance(currency)))
            );
        }

        if (context.shouldNotify(NotificationTarget.EXECUTOR)) {
            executor.getBukkitSender().ifPresent(sender -> {
                currency.sendPrefixed(Lang.COMMAND_CURRENCY_GIVE_DONE, sender, replacer -> replacer
                    .replace(Placeholders.PLAYER_NAME, user::getName)
                    .replace(Placeholders.GENERIC_AMOUNT, () -> currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, () -> currency.format(user.getBalance(currency)))
                );
            });
        }

        if (context.shouldNotify(NotificationTarget.USER)) {
            Player target = user.getPlayer();
            if (target != null) {
                currency.sendPrefixed(Lang.COMMAND_CURRENCY_GIVE_NOTIFY, target, replacer -> replacer
                    .replace(Placeholders.GENERIC_AMOUNT, () -> currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, () -> currency.format(user.getBalance(currency)))
                );
            }
        }

        return OperationResult.SUCCESS;
    }

    @NotNull
    public OperationResult giveAll(@NotNull OperationContext context, @NotNull Currency currency, double amount) {
        if (!this.assertOperationsEnabled(context)) return OperationResult.FAILURE;

        OperationExecutor executor = context.getExecutor();
        Set<CoinsUser> users = this.userManager.getLoaded();

        users.forEach(user -> {
            Player target = user.getPlayer();
            if (target == null) return; // Only online players should be affected.

            user.addBalance(currency, amount);
            this.userManager.save(user);

            if (context.shouldNotify(NotificationTarget.USER)) {
                currency.sendPrefixed(Lang.COMMAND_CURRENCY_GIVE_NOTIFY, target, replacer -> replacer
                    .replace(Placeholders.GENERIC_AMOUNT, () -> currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, () -> currency.format(user.getBalance(currency)))
                );
            }
        });

        if (this.logger != null && context.shouldNotifyLogger()) {
            this.logger.addEntry(context, "[%s] %s gave %s to all online players. Affected players (%s): %s"
                .formatted(currency.getId(), executor.getName(), currency.format(amount), users.size(), users.stream().map(AbstractUser::getName).collect(Collectors.joining(", ")))
            );
        }

        if (context.shouldNotify(NotificationTarget.EXECUTOR)) {
            executor.getBukkitSender().ifPresent(sender -> {
                currency.sendPrefixed(Lang.COMMAND_CURRENCY_GIVE_ALL_DONE, sender, replacer -> replacer
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                );
            });
        }

        return OperationResult.SUCCESS;
    }

    @NotNull
    public OperationResult remove(@NotNull OperationContext context, @NotNull Player player, @NotNull Currency currency, double amount) {
        return this.remove(context, this.userManager.getOrFetch(player), currency, amount);
    }

    @NotNull
    public OperationResult remove(@NotNull OperationContext context, @NotNull CoinsUser user, @NotNull Currency currency, double amount) {
        if (!this.assertOperationsEnabled(context)) return OperationResult.FAILURE;

        OperationExecutor executor = context.getExecutor();

        user.removeBalance(currency, amount);
        this.userManager.save(user);

        if (this.logger != null && context.shouldNotifyLogger()) {
            this.logger.addEntry(context, "[%s] %s took %s from %s's balance. New balance: %s"
                .formatted(currency.getId(), executor.getName(), currency.format(amount), user.getName(), currency.format(user.getBalance(currency))));
        }

        if (context.shouldNotify(NotificationTarget.EXECUTOR)) {
            executor.getBukkitSender().ifPresent(sender -> {
                currency.sendPrefixed(Lang.COMMAND_CURRENCY_TAKE_DONE, sender, replacer -> replacer
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            });
        }

        if (context.shouldNotify(NotificationTarget.USER)) {
            Optional.ofNullable(user.getPlayer()).ifPresent(target -> {
                currency.sendPrefixed(Lang.COMMAND_CURRENCY_TAKE_NOTIFY, target, replacer -> replacer
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            });
        }

        return OperationResult.SUCCESS;
    }

    @NotNull
    public OperationResult set(@NotNull OperationContext context, @NotNull Player player, @NotNull Currency currency, double amount) {
        return this.set(context, this.userManager.getOrFetch(player), currency, amount);
    }

    @NotNull
    public OperationResult set(@NotNull OperationContext context, @NotNull CoinsUser user, @NotNull Currency currency, double amount) {
        if (!this.assertOperationsEnabled(context)) return OperationResult.FAILURE;

        OperationExecutor executor = context.getExecutor();

        user.setBalance(currency, amount);
        this.userManager.save(user);

        if (this.logger != null && context.shouldNotifyLogger()) {
            this.logger.addEntry(context, "[%s] %s set %s's balance to %s. New balance: %s"
                .formatted(currency.getId(), executor.getName(), user.getName(), currency.format(amount), currency.format(user.getBalance(currency)))
            );
        }

        if (context.shouldNotify(NotificationTarget.EXECUTOR)) {
            executor.getBukkitSender().ifPresent(sender -> {
                currency.sendPrefixed(Lang.COMMAND_CURRENCY_SET_DONE, sender, replacer -> replacer
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            });
        }

        if (context.shouldNotify(NotificationTarget.USER)) {
            Optional.ofNullable(user.getPlayer()).ifPresent(target -> {
                currency.sendPrefixed(Lang.COMMAND_CURRENCY_SET_NOTIFY, target, replacer -> replacer
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            });
        }

        return OperationResult.SUCCESS;
    }

    @NotNull
    public OperationResult reset(@NotNull OperationContext context, @NotNull Player player, @NotNull Currency currency) {
        return this.reset(context, this.userManager.getOrFetch(player), currency);
    }

    @NotNull
    public OperationResult reset(@NotNull OperationContext context, @NotNull CoinsUser user, @NotNull Currency currency) {
        if (!this.assertOperationsEnabled(context)) return OperationResult.FAILURE;

        OperationExecutor executor = context.getExecutor();

        user.resetBalance(currency);
        this.userManager.save(user);

        if (this.logger != null && context.shouldNotifyLogger()) {
            this.logger.addEntry(context, "[%s] %s reset %s's balance of %s to %s."
                .formatted(currency.getId(), executor.getName(), user.getName(), currency.getName(), currency.format(user.getBalance(currency)))
            );
        }

        if (context.shouldNotify(NotificationTarget.EXECUTOR)) {
            executor.getBukkitSender().ifPresent(sender -> {
                currency.sendPrefixed(Lang.CURRENCY_OPERATION_RESET_FEEDBACK, sender, replacer -> replacer
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            });
        }

        if (context.shouldNotify(NotificationTarget.USER)) {
            Optional.ofNullable(user.getPlayer()).ifPresent(target -> {
                currency.sendPrefixed(Lang.CURRENCY_OPERATION_RESET_NOTIFY, target, replacer -> replacer
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                );
            });
        }

        return OperationResult.SUCCESS;
    }

    public boolean send(@NotNull Player sender, @NotNull String targetName, @NotNull Currency currency, double rawAmount) {
        OperationContext context = OperationContext.of(sender);

        if (!this.assertOperationsEnabled(context)) return false;

        double amount = currency.floorIfNeeded(rawAmount);
        if (amount <= 0D) return false;

        if (sender.getName().equalsIgnoreCase(targetName)) {
            CoreLang.COMMAND_EXECUTION_NOT_YOURSELF.withPrefix(this.plugin).send(sender);
            return false;
        }

        double minAmount = currency.getMinTransferAmount();
        if (minAmount > 0 && amount < minAmount) {
            currency.sendPrefixed(Lang.CURRENCY_SEND_ERROR_TOO_LOW, sender, replacer -> replacer.replace(Placeholders.GENERIC_AMOUNT, currency.format(minAmount)));
            return false;
        }

        CoinsUser fromUser = this.userManager.getOrFetch(sender);
        if (amount > fromUser.getBalance(currency)) {
            currency.sendPrefixed(Lang.CURRENCY_SEND_ERROR_NOT_ENOUGH, sender);
            return false;
        }

        this.userManager.manageUser(targetName, targetUser -> {
            if (targetUser == null) {
                CoreLang.ERROR_INVALID_PLAYER.withPrefix(this.plugin).send(sender);
                return;
            }

            CurrencySettings settings = targetUser.getSettings(currency);
            if (!settings.isPaymentsEnabled()) {
                currency.sendPrefixed(Lang.CURRENCY_SEND_ERROR_NO_PAYMENTS, sender, replacer -> replacer
                    .replace(Placeholders.PLAYER_NAME, targetUser.getName())
                );
                return;
            }

            targetUser.addBalance(currency, amount);
            fromUser.removeBalance(currency, amount);

            this.userManager.save(targetUser);
            this.userManager.save(fromUser);

            currency.sendPrefixed(Lang.CURRENCY_SEND_DONE_SENDER, sender, replacer -> replacer
                .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                .replace(Placeholders.GENERIC_BALANCE, fromUser.getBalance(currency))
                .replace(Placeholders.PLAYER_NAME, targetUser.getName())
            );

            Optional.ofNullable(targetUser.getPlayer()).ifPresent(target -> {
                currency.sendPrefixed(Lang.CURRENCY_SEND_DONE_NOTIFY, target, replacer -> replacer
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, targetUser.getBalance(currency))
                    .replace(Placeholders.PLAYER_NAME, sender.getName())
                );
            });

            this.logger.addEntry(context, "[%s] %s paid %s to %s. New balances: %s and %s.".formatted(
                currency.getId(),
                sender.getName(),
                currency.format(amount),
                targetUser.getName(),
                currency.format(fromUser.getBalance(currency)),
                currency.format(targetUser.getBalance(currency))
            ));
        });

        return true;
    }

    public boolean exchange(@NotNull Player player, @NotNull Currency sourceCurrency, @NotNull Currency targetCurrency, double initAmount) {
        OperationContext context = OperationContext.of(player);

        if (!this.assertOperationsEnabled(context)) return false;

        if (!sourceCurrency.isExchangeAllowed()) {
            sourceCurrency.sendPrefixed(Lang.CURRENCY_EXCHANGE_ERROR_DISABLED, player);
            return false;
        }

        double amount = sourceCurrency.floorIfNeeded(initAmount);
        if (amount <= 0D) {
            sourceCurrency.sendPrefixed(Lang.CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT, player);
            return false;
        }

        CoinsUser user = this.userManager.getOrFetch(player);
        if (user.getBalance(sourceCurrency) < amount) {
            sourceCurrency.sendPrefixed(Lang.CURRENCY_EXCHANGE_ERROR_LOW_BALANCE, player, replacer -> replacer
                .replace(Placeholders.GENERIC_AMOUNT, sourceCurrency.format(amount))
            );
            return false;
        }

        if (!sourceCurrency.canExchangeTo(targetCurrency)) {
            sourceCurrency.sendPrefixed(Lang.CURRENCY_EXCHANGE_ERROR_NO_RATE, player, replacer -> replacer
                .replace(Placeholders.GENERIC_NAME, targetCurrency.getName())
            );
            return false;
        }

        double result = sourceCurrency.getExchangeResult(targetCurrency, amount);
        if (result <= 0D) {
            sourceCurrency.sendPrefixed(Lang.CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT, player);
            return false;
        }

        double newBalance = user.getBalance(targetCurrency) + result;
        if (!targetCurrency.isUnderLimit(newBalance)) {
            targetCurrency.sendPrefixed(Lang.CURRENCY_EXCHANGE_ERROR_LIMIT_EXCEED, player, replacer -> replacer
                .replace(Placeholders.GENERIC_AMOUNT, targetCurrency.format(result))
                .replace(Placeholders.GENERIC_MAX, targetCurrency.format(targetCurrency.getMaxValue()))
            );
            return false;
        }

        user.removeBalance(sourceCurrency, amount);
        user.addBalance(targetCurrency, result);
        this.userManager.save(user);

        sourceCurrency.sendPrefixed(Lang.CURRENCY_EXCHANGE_SUCCESS, player, replacer -> replacer
            .replace(Placeholders.GENERIC_BALANCE, sourceCurrency.format(amount))
            .replace(Placeholders.GENERIC_AMOUNT, targetCurrency.format(result))
        );

        this.logger.addEntry(context, "[%s] %s exchanged %s for %s [%s]. New balances: %s and %s."
            .formatted(
                sourceCurrency.getId(),
                user.getName(),
                sourceCurrency.format(amount),
                targetCurrency.format(result),
                targetCurrency.getId(),
                sourceCurrency.format(user.getBalance(sourceCurrency)),
                targetCurrency.format(user.getBalance(targetCurrency))
            )
        );

        return true;
    }
}
