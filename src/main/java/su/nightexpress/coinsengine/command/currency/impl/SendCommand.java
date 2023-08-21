package su.nightexpress.coinsengine.command.currency.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.CurrencySubCommand;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencyData;
import su.nightexpress.coinsengine.util.CoinsLogger;

import java.util.Arrays;
import java.util.List;

public class SendCommand extends CurrencySubCommand {

    public SendCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"pay", "transfer", "send"}, Perms.COMMAND_CURRENCY_SEND);
        this.setDescription(plugin.getMessage(Lang.COMMAND_CURRENCY_SEND_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_CURRENCY_SEND_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            return Arrays.asList("1", "10", "50", "100");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        String userName = result.getArg(1);
        if (userName.equalsIgnoreCase(sender.getName())) {
            plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(sender);
            return;
        }

        double amount = result.getDouble(2, 0);
        if (amount <= 0) return;

        if (this.currency.getMinTransferAmount() > 0 && amount < this.currency.getMinTransferAmount()) {
            this.plugin.getMessage(Lang.COMMAND_CURRENCY_SEND_ERROR_TOO_LOW)
                .replace(Placeholders.GENERIC_AMOUNT, this.currency.format(this.currency.getMinTransferAmount()))
                .send(sender);
            return;
        }

        CoinsUser userTarget = plugin.getUserManager().getUserData(userName);
        if (userTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        Player from = (Player) sender;
        CoinsUser userFrom = plugin.getUserManager().getUserData(from);
        CurrencyData dataFrom = userFrom.getCurrencyData(this.currency);
        if (amount > dataFrom.getBalance()) {
            plugin.getMessage(Lang.COMMAND_CURRENCY_SEND_ERROR_NOT_ENOUGH)
                .replace(this.currency.replacePlaceholders())
                .send(from);
            return;
        }

        CurrencyData dataTarget = userTarget.getCurrencyData(this.currency);
        if (!dataTarget.isPaymentsEnabled()) {
            plugin.getMessage(Lang.COMMAND_CURRENCY_SEND_ERROR_NO_PAYMENTS)
                .replace(Placeholders.PLAYER_NAME, userTarget.getName())
                .replace(this.currency.replacePlaceholders())
                .send(from);
            return;
        }

        dataTarget.addBalance(amount);
        dataFrom.removeBalance(amount);
        userTarget.saveData(this.plugin);
        userFrom.saveData(this.plugin);

        CoinsLogger.logGive(userTarget, currency, amount, from);

        plugin.getMessage(Lang.COMMAND_CURRENCY_SEND_DONE_SENDER)
            .replace(currency.replacePlaceholders())
            .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
            .replace(Placeholders.GENERIC_BALANCE, dataFrom.getBalance())
            .replace(Placeholders.PLAYER_NAME, userTarget.getName())
            .send(sender);

        Player pTarget = plugin.getServer().getPlayer(userTarget.getName());
        if (pTarget != null) {
            plugin.getMessage(Lang.COMMAND_CURRENCY_SEND_DONE_NOTIFY)
                .replace(currency.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                .replace(Placeholders.GENERIC_BALANCE, dataTarget.getBalance())
                .replace(Placeholders.PLAYER_NAME, userFrom.getName())
                .send(pTarget);
        }
    }
}
