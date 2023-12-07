package su.nightexpress.coinsengine.command.currency.impl;

import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.Pair;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.CurrencySubCommand;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;

import java.util.Collections;
import java.util.List;

public class TotalCommand extends CurrencySubCommand {

    public TotalCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency, @NotNull String... aliases) {
        super(plugin, currency, aliases, Perms.COMMAND_CURRENCY_TOTAL);
        this.setDescription(plugin.getMessage(Lang.COMMAND_CURRENCY_TOTAL_TOP_DESC));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        List<Pair<String, Double>> full = this.plugin.getCurrencyManager().getBalanceMap().getOrDefault(this.currency, Collections.emptyList());

        AtomicDouble totalCurrency = new AtomicDouble(0);
        full.forEach((pair) -> totalCurrency.addAndGet(pair.getSecond()));
        String totalCurrencyFormat = currency.format(totalCurrency.get());

        this.plugin.getMessage(Lang.COMMAND_CURRENCY_TOTAL_TOP_DONE)
            .replace(currency.replacePlaceholders())
            .replace(Placeholders.GENERIC_TOTAL_BALANCE, totalCurrencyFormat)
            .send(sender);
    }
}
