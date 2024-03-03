package su.nightexpress.coinsengine.currency;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.CurrencyMainCommand;
import su.nightexpress.coinsengine.command.currency.impl.BalanceCommand;
import su.nightexpress.coinsengine.command.currency.impl.SendCommand;
import su.nightexpress.coinsengine.command.currency.impl.TopCommand;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.impl.ConfigCurrency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.hook.VaultEconomyHook;
import su.nightexpress.coinsengine.util.Logger;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CurrencyManager extends AbstractManager<CoinsEnginePlugin> {

    private final Map<String, Currency>                     currencyMap;
    private final Map<Currency, List<Pair<String, Double>>> balanceMap;

    public CurrencyManager(@NotNull CoinsEnginePlugin plugin) {
        super(plugin);
        this.currencyMap = new HashMap<>();
        this.balanceMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        this.createDefaults();

        for (File file : FileUtil.getFiles(plugin.getDataFolder() + Config.DIR_CURRENCIES, false)) {
            ConfigCurrency currency = new ConfigCurrency(plugin, file);
            if (currency.load()) {
                this.registerCurrency(currency);
            }
        }

        this.getVaultCurrency().ifPresent(currency -> {
            if (Plugins.hasVault()) {
                VaultEconomyHook.setup(this.plugin, currency);
                if (Config.ECONOMY_COMMAND_SHORTCUTS_ENABLED.get()) {
                    this.plugin.getCommandManager().registerCommand(new BalanceCommand(plugin, currency));
                    this.plugin.getCommandManager().registerCommand(new SendCommand(plugin, currency));
                    this.plugin.getCommandManager().registerCommand(new TopCommand(plugin, currency, "baltop"));
                }
            }
            else {
                this.plugin.error("Found Vault Economy currency, but Vault is not installed!");
            }
        });

        this.addTask(this.plugin.createAsyncTask(this::updateBalances)
            .setSecondsInterval(Config.TOP_UPDATE_INTERVAL.get())
        );
    }

    @Override
    protected void onShutdown() {
        if (Plugins.hasVault()) {
            VaultEconomyHook.shutdown();
        }
        this.getCurrencyMap().clear();
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
    public Map<Currency, List<Pair<String, Double>>> getBalanceMap() {
        return balanceMap;
    }

    @NotNull
    public List<Pair<String, Double>> getBalanceList(@NotNull Currency currency) {
        return this.getBalanceMap().getOrDefault(currency, Collections.emptyList());
    }

    public double getTotalBalance(@NotNull Currency currency) {
        return this.getBalanceMap().getOrDefault(currency, Collections.emptyList())
            .stream().mapToDouble(Pair::getSecond).sum();
    }

    public void updateBalances() {
        Map<Currency, List<Pair<String, Double>>> balanceMap = this.getBalanceMap();
        Map<Currency, Map<String, Double>> dataMap = this.plugin.getData().getBalances();

        balanceMap.clear();
        dataMap.forEach((currency, users) -> {
            Lists.sortDescent(users).forEach((name, balance) -> {
                balanceMap.computeIfAbsent(currency, k -> new ArrayList<>()).add(Pair.of(name, balance));
            });
        });
        this.plugin.info("Balance top updated!");
    }

    public void registerCurrency(@NotNull Currency currency) {
        this.unregisterCurrency(currency);

        this.plugin.getCommandManager().registerCommand(new CurrencyMainCommand(plugin, currency));
        this.getCurrencyMap().put(currency.getId(), currency);
        this.plugin.info("Currency registered: '" + currency.getId() + "'!");
    }

    public boolean unregisterCurrency(@NotNull Currency currency) {
        return this.unregisterCurrency(currency.getId());
    }

    public boolean unregisterCurrency(@NotNull String id) {
        Currency currency = this.getCurrencyMap().remove(id);
        if (currency == null) return false;

        this.plugin.getCommandManager().unregisterCommand(currency.getCommandAliases()[0]);
        this.plugin.info("Currency unregistered: '" + currency.getId() + "'!");
        return true;
    }

    @Nullable
    public Currency getCurrency(@NotNull String id) {
        return this.getCurrencyMap().get(id.toLowerCase());
    }

    @NotNull
    public Map<String, Currency> getCurrencyMap() {
        return currencyMap;
    }

    @NotNull
    public Optional<Currency> getVaultCurrency() {
        return this.getCurrencies().stream().filter(Currency::isVaultEconomy).findFirst();
    }

    @NotNull
    public Collection<Currency> getCurrencies() {
        return this.getCurrencyMap().values();
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
        if (user.getCurrencyData(from).getBalance() < amount) {
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

        user.getCurrencyData(from).removeBalance(amount);
        user.getCurrencyData(to).addBalance(result);
        this.plugin.getUserManager().saveAsync(user);

        Lang.CURRENCY_EXCHANGE_SUCCESS.getMessage()
            .replace(from.replacePlaceholders())
            .replace(Placeholders.GENERIC_BALANCE, from.format(amount))
            .replace(Placeholders.GENERIC_AMOUNT, to.format(result))
            .send(player);

        Logger.logExchange(user, from, to, amount, result);
        return true;
    }
}
