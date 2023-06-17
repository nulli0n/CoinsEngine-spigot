package su.nightexpress.coinsengine.currency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.Pair;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.CurrencyMainCommand;
import su.nightexpress.coinsengine.command.currency.impl.BalanceCommand;
import su.nightexpress.coinsengine.command.currency.impl.TopCommand;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.currency.impl.ConfigCurrency;
import su.nightexpress.coinsengine.currency.listener.CurrencyListener;
import su.nightexpress.coinsengine.hook.VaultEconomyHook;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyManager extends AbstractManager<CoinsEngine> {

    private final Map<String, Currency>                     currencyMap;
    private final Map<Currency, List<Pair<String, Double>>> balanceMap;

    public CurrencyManager(@NotNull CoinsEngine plugin) {
        super(plugin);
        this.currencyMap = new HashMap<>();
        this.balanceMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.getConfigManager().extractResources(Config.DIR_CURRENCIES);

        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.DIR_CURRENCIES)) {
            ConfigCurrency currency = new ConfigCurrency(plugin, cfg);
            if (currency.load()) {
                this.registerCurrency(currency);
            }
        }

        this.plugin.getData().migrateOldPoints();

        this.getVaultCurrency().ifPresent(currency -> {
            if (Hooks.hasVault()) {
                VaultEconomyHook.setup(this.plugin, currency);
                this.plugin.getCommandManager().registerCommand(new BalanceCommand(plugin, currency));
                this.plugin.getCommandManager().registerCommand(new TopCommand(plugin, currency));
            }
            else {
                this.plugin.error("Found Vault Economy currency, but Vault is not installed!");
            }
        });

        this.addListener(new CurrencyListener(this));
    }

    @Override
    protected void onShutdown() {
        if (Hooks.hasVault()) {
            VaultEconomyHook.shutdown();
        }

        this.currencyMap.clear();
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
        this.currencyMap.put(currency.getId(), currency);
        this.plugin.info("Currency registered: '" + currency.getId() + "'!");
    }

    public void unregisterCurrency(@NotNull Currency currency) {
        Currency del = this.currencyMap.remove(currency.getId());
        if (del == null) return;

        this.plugin.getCommandManager().unregisterCommand(currency.getCommandAliases()[0]);
        this.plugin.info("Currency unregistered: '" + del.getId() + "'!");
    }

    @Nullable
    public Currency getCurrency(@NotNull String id) {
        return this.currencyMap.get(id.toLowerCase());
    }

    @NotNull
    public Optional<Currency> getVaultCurrency() {
        return this.getCurrencies().stream().filter(Currency::isVaultEconomy).findFirst();
    }

    @NotNull
    public Collection<Currency> getCurrencies() {
        return this.currencyMap.values();
    }
}
