package su.nightexpress.coinsengine.currency.task;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Pair;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BalanceUpdateTask extends AbstractTask<CoinsEngine> {

    public BalanceUpdateTask(@NotNull CoinsEngine plugin) {
        super(plugin, Config.TOP_UPDATE_INTERVAL.get(), true);
    }

    @Override
    public void action() {
        Map<Currency, List<Pair<String, Double>>> balanceMap = this.plugin.getCurrencyManager().getBalanceMap();
        Map<Currency, Map<String, Double>> dataMap = this.plugin.getData().getBalances();

        balanceMap.clear();
        dataMap.forEach((currency, users) -> {
            CollectionsUtil.sortDescent(users).forEach((name, balance) -> {
                balanceMap.computeIfAbsent(currency, k -> new ArrayList<>()).add(Pair.of(name, balance));
            });
        });
        plugin.info("Balance top updated!");
    }
}
