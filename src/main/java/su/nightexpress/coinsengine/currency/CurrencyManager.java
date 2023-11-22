package su.nightexpress.coinsengine.currency;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.Pair;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.CurrencyMainCommand;
import su.nightexpress.coinsengine.command.currency.impl.BalanceCommand;
import su.nightexpress.coinsengine.command.currency.impl.SendCommand;
import su.nightexpress.coinsengine.command.currency.impl.TopCommand;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.impl.ConfigCurrency;
import su.nightexpress.coinsengine.currency.listener.CurrencyListener;
import su.nightexpress.coinsengine.currency.task.BalanceUpdateTask;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.hook.VaultEconomyHook;
import su.nightexpress.coinsengine.util.Logger;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CurrencyManager extends AbstractManager<CoinsEngine> {

    private final Map<String, Currency>                     currencyMap;
    private final Map<Currency, List<Pair<String, Double>>> balanceMap;

    private BalanceUpdateTask balanceUpdateTask;

    public CurrencyManager(@NotNull CoinsEngine plugin) {
        super(plugin);
        this.currencyMap = new HashMap<>();
        this.balanceMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        this.createDefaults();

        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.DIR_CURRENCIES)) {
            ConfigCurrency currency = new ConfigCurrency(plugin, cfg);
            if (currency.load()) {
                this.registerCurrency(currency);
            }
        }

        this.getVaultCurrency().ifPresent(currency -> {
            if (EngineUtils.hasVault()) {
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

        this.addListener(new CurrencyListener(this));

        this.balanceUpdateTask = new BalanceUpdateTask(this.plugin);
        this.balanceUpdateTask.start();
    }

    @Override
    protected void onShutdown() {
        if (this.balanceUpdateTask != null) this.balanceUpdateTask.stop();
        if (EngineUtils.hasVault()) {
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

    public void registerCurrency(@NotNull Currency currency) {
        this.plugin.getCommandManager().registerCommand(new CurrencyMainCommand(plugin, currency));
        this.getCurrencyMap().put(currency.getId(), currency);
        this.plugin.info("Currency registered: '" + currency.getId() + "'!");
    }

    public void unregisterCurrency(@NotNull Currency currency) {
        Currency del = this.getCurrencyMap().remove(currency.getId());
        if (del == null) return;

        this.plugin.getCommandManager().unregisterCommand(currency.getCommandAliases()[0]);
        this.plugin.info("Currency unregistered: '" + del.getId() + "'!");
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

        JYML cfg = new JYML(plugin.getDataFolder() + Config.DIR_CURRENCIES, id + ".yml");
        ConfigCurrency currency = new ConfigCurrency(this.plugin, cfg);
        currency.setName(StringUtil.capitalizeUnderscored(id));
        currency.setFormat(Placeholders.GENERIC_AMOUNT + Placeholders.CURRENCY_SYMBOL);
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
            this.plugin.getMessage(Lang.CURRENCY_EXCHANGE_ERROR_DISABLED).replace(from.replacePlaceholders()).send(player);
            return false;
        }

        CoinsUser user = this.plugin.getUserManager().getUserData(player);
        if (user.getCurrencyData(from).getBalance() < amount) {
            this.plugin.getMessage(Lang.CURRENCY_EXCHANGE_ERROR_LOW_BALANCE)
                .replace(from.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, from.format(amount))
                .send(player);
            return false;
        }

        double rate = from.getExchangeRate(to);
        if (rate <= 0D) {
            this.plugin.getMessage(Lang.CURRENCY_EXCHANGE_ERROR_NO_RATE)
                .replace(from.replacePlaceholders())
                .replace(Placeholders.GENERIC_NAME, to.getName())
                .send(player);
            return false;
        }

        if (from.isInteger()) {
            amount = Math.floor(amount);
        }
        if (amount <= 0D) {
            this.plugin.getMessage(Lang.CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT).replace(from.replacePlaceholders()).send(player);
            return false;
        }

        double result = to.fine(amount * rate);
        if (result <= 0D) {
            this.plugin.getMessage(Lang.CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT).replace(from.replacePlaceholders()).send(player);
            return false;
        }

        user.getCurrencyData(from).removeBalance(amount);
        user.getCurrencyData(to).addBalance(result);
        this.plugin.getUserManager().saveUser(user);

        this.plugin.getMessage(Lang.CURRENCY_EXCHANGE_SUCCESS)
            .replace(from.replacePlaceholders())
            .replace(Placeholders.GENERIC_BALANCE, from.format(amount))
            .replace(Placeholders.GENERIC_AMOUNT, to.format(result))
            .send(player);

        Logger.logExchange(user, from, to, amount, result);
        return true;
    }
}
