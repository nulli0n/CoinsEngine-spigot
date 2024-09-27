package su.nightexpress.coinsengine.currency;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.impl.CurrencyCommands;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.impl.ConfigCurrency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.hook.vault.VaultHook;
import su.nightexpress.coinsengine.util.TopEntry;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class CurrencyManager extends AbstractManager<CoinsEnginePlugin> {

    private final Map<String, Currency>       currencyMap;
    private final Map<String, List<TopEntry>> totalBalanceMap;

    public CurrencyManager(@NotNull CoinsEnginePlugin plugin) {
        super(plugin);
        this.currencyMap = new HashMap<>();
        this.totalBalanceMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        this.createDefaults();

        for (File file : FileUtil.getConfigFiles(plugin.getDataFolder() + Config.DIR_CURRENCIES)) {
            ConfigCurrency currency = new ConfigCurrency(plugin, file);
            if (currency.load()) {
                this.registerCurrency(currency);
            }
        }

        this.addTask(this.plugin.createAsyncTask(this::updateBalances).setSecondsInterval(Config.TOP_UPDATE_INTERVAL.get()));
    }

    @Override
    protected void onShutdown() {
        if (Plugins.hasVault()) {
            VaultHook.shutdown();
        }
        this.getCurrencies().forEach(this::unregisterCurrency);
        this.currencyMap.clear();
        this.totalBalanceMap.clear();
    }

    private void createDefaults() {
        File dir = new File(plugin.getDataFolder() + Config.DIR_CURRENCIES);
        if (dir.exists()) return;

        this.createCurrency("coins", currency -> {
            currency.setSymbol("⛂");
            currency.setIcon(new ItemStack(Material.SUNFLOWER));
            currency.setDecimal(false);
        });

        this.createCurrency("money", currency -> {
            currency.setSymbol("$");
            currency.setIcon(new ItemStack(Material.GOLD_NUGGET));
            currency.setDecimal(true);
            currency.setVaultEconomy(true);
        });
    }

    @NotNull
    public Map<String, List<TopEntry>> getTotalBalanceMap() {
        return this.totalBalanceMap;
    }

    @NotNull
    public List<TopEntry> getTopBalances(@NotNull Currency currency) {
        return new ArrayList<>(this.totalBalanceMap.getOrDefault(currency.getId(), Collections.emptyList()));
    }

    public double getTotalBalance(@NotNull Currency currency) {
        return this.getTopBalances(currency).stream().mapToDouble(TopEntry::balance).sum();
    }

    public void updateBalances() {
        this.totalBalanceMap.clear();

        Map<Currency, Map<String, Double>> balances = this.plugin.getData().getBalances();
        AtomicInteger counter = new AtomicInteger(0);

        balances.forEach((currency, balanceMap) -> {
            List<TopEntry> entries = this.totalBalanceMap.computeIfAbsent(currency.getId(), k -> new ArrayList<>());
            Lists.sortDescent(balanceMap).forEach((name, balance) -> {
                entries.add(new TopEntry(counter.incrementAndGet(), balance, name));
            });
        });
    }

    public void registerCurrency(@NotNull Currency currency) {
        if (this.getCurrency(currency.getId()) != null) {
            this.plugin.error("Could not register duplicated currency: '" + currency.getId() + "'!");
            return;
        }

        this.plugin.getData().createCurrencyColumn(currency);

        CurrencyCommands.loadForCurrency(plugin, currency);

        if (currency.isVaultEconomy() && this.getVaultCurrency().isEmpty()) {
            if (Plugins.hasVault()) {
                VaultHook.setup(this.plugin, currency);
                if (Config.ECONOMY_COMMAND_SHORTCUTS_ENABLED.get()) {
                    CurrencyCommands.loadForEconomy(plugin, currency);
                }
            }
            else {
                this.plugin.error("Found Vault Economy currency, but Vault is not installed!");
            }
        }

        this.currencyMap.put(currency.getId(), currency);
        this.plugin.info("Registered: '" + currency.getId() + "' currency!");
    }

    public boolean unregisterCurrency(@NotNull Currency currency) {
        return this.unregisterCurrency(currency.getId());
    }

    public boolean unregisterCurrency(@NotNull String id) {
        Currency currency = this.currencyMap.remove(id);
        if (currency == null) return false;

        CurrencyCommands.unloadForCurrency(this.plugin, currency);

        if (currency.isVaultEconomy()) {
            CurrencyCommands.unloadForEconomy(this.plugin);
        }

        this.totalBalanceMap.remove(id);
        this.plugin.info("Currency unregistered: '" + currency.getId() + "'!");
        return true;
    }

    @Nullable
    public Currency getCurrency(@NotNull String id) {
        return this.currencyMap.get(id.toLowerCase());
    }

    @NotNull
    public Map<String, Currency> getCurrencyMap() {
        return this.currencyMap;
    }

    @NotNull
    public List<String> getCurrencyIds() {
        return new ArrayList<>(this.currencyMap.keySet());
    }

    @NotNull
    public Optional<Currency> getVaultCurrency() {
        return this.getCurrencies().stream().filter(Currency::isVaultEconomy).findFirst();
    }

    @NotNull
    public Set<Currency> getCurrencies() {
        return new HashSet<>(this.currencyMap.values());
    }

    public boolean createCurrency(@NotNull String id, @NotNull Consumer<ConfigCurrency> consumer) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (id.isEmpty()) return false;

        File file = new File(plugin.getDataFolder() + Config.DIR_CURRENCIES, id + ".yml");
        ConfigCurrency currency = new ConfigCurrency(this.plugin, file);
        currency.setName(StringUtil.capitalizeUnderscored(id));
        currency.setFormat(Placeholders.GENERIC_AMOUNT + Placeholders.CURRENCY_SYMBOL);
        currency.setFormatShort(Placeholders.CURRENCY_SYMBOL + Placeholders.GENERIC_AMOUNT + Placeholders.CURRENCY_SHORT_SYMBOL);
        currency.setCommandAliases(new String[]{id});
        currency.setPermissionRequired(false);
        currency.setTransferAllowed(true);
        currency.setStartValue(0);
        currency.setMaxValue(-1);
        currency.setExchangeAllowed(true);
        consumer.accept(currency);
        currency.save();

        return true;
    }

    public boolean exchange(@NotNull Player player, @NotNull Currency from, @NotNull Currency to, double amount) {
        if (!from.isExchangeAllowed()) {
            Lang.CURRENCY_EXCHANGE_ERROR_DISABLED.getMessage().replace(from.replacePlaceholders()).send(player);
            return false;
        }

        CoinsUser user = this.plugin.getUserManager().getUserData(player);
        if (user.getBalance(from) < amount) {
            Lang.CURRENCY_EXCHANGE_ERROR_LOW_BALANCE.getMessage()
                .replace(from.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, from.format(amount))
                .send(player);
            return false;
        }

        double rate = from.getExchangeRate(to);
        if (rate <= 0D) {
            Lang.CURRENCY_EXCHANGE_ERROR_NO_RATE.getMessage()
                .replace(from.replacePlaceholders())
                .replace(Placeholders.GENERIC_NAME, to.getName())
                .send(player);
            return false;
        }

        amount = from.fine(amount);

        if (amount <= 0D) {
            Lang.CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT.getMessage().replace(from.replacePlaceholders()).send(player);
            return false;
        }

        double result = to.fine(amount * rate);
        if (result <= 0D) {
            Lang.CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT.getMessage().replace(from.replacePlaceholders()).send(player);
            return false;
        }

        double has = user.getBalance(to) + result;
        if (!to.isUnderLimit(has)) {
            Lang.CURRENCY_EXCHANGE_ERROR_LIMIT_EXCEED.getMessage()
                .replace(Placeholders.GENERIC_AMOUNT, to.format(result))
                .replace(Placeholders.GENERIC_MAX, to.format(to.getMaxValue()))
                .send(player);
            return false;
        }

        user.removeBalance(from, amount);
        user.addBalance(to, result);
        this.plugin.getUserManager().scheduleSave(user);

        Lang.CURRENCY_EXCHANGE_SUCCESS.getMessage()
            .replace(from.replacePlaceholders())
            .replace(Placeholders.GENERIC_BALANCE, from.format(amount))
            .replace(Placeholders.GENERIC_AMOUNT, to.format(result))
            .send(player);

        this.plugin.getCoinsLogger().logExchange(user, from, to, amount, result);
        return true;
    }
}
