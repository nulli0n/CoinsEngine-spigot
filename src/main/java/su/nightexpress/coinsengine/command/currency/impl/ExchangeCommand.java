package su.nightexpress.coinsengine.command.currency.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.CurrencySubCommand;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.util.CoinsUtils;

import java.util.Arrays;
import java.util.List;

public class ExchangeCommand extends CurrencySubCommand {

    public ExchangeCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"exchange"}, Perms.COMMAND_CURRENCY_EXCHANGE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_CURRENCY_EXCHANGE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_CURRENCY_EXCHANGE_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.plugin.getCurrencyManager().getCurrencies().stream()
                .filter(other -> this.currency.getExchangeRate(other) > 0).map(Currency::getId).toList();
        }
        if (arg == 2) {
            return Arrays.asList("1", "10", "50", "100");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        int indexOff = this.getParent() == null ? 1 : 0;

        if (result.length() < 3 - indexOff) {
            this.printUsage(sender);
            return;
        }

        String curId = result.getArg(1 - indexOff);
        Currency to = this.plugin.getCurrencyManager().getCurrency(curId);
        if (to == null) {
            this.plugin.getMessage(Lang.CURRENCY_ERROR_INVALID).send(sender);
            return;
        }

        double amount = CoinsUtils.getAmountFromInput(result.getArg(2 - indexOff));
        if (amount <= 0) return;

        Player player = (Player) sender;
        this.plugin.getCurrencyManager().exchange(player, this.currency, to, amount);
    }
}
