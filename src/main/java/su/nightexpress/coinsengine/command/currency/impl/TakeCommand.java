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
import su.nightexpress.coinsengine.util.CoinsLogger;

import java.util.Arrays;
import java.util.List;

public class TakeCommand extends CurrencySubCommand {

    public TakeCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"take"}, Perms.COMMAND_CURRENCY_TAKE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_CURRENCY_TAKE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_CURRENCY_TAKE_USAGE));
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

        double amount = result.getDouble(2, 0D);
        if (amount <= 0D) return;

        this.plugin.getUserManager().getUserDataAsync(result.getArg(1)).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            user.removeBalance(currency, amount);
            user.saveData(this.plugin);

            CoinsLogger.logTake(user, currency, amount, sender);

            plugin.getMessage(Lang.COMMAND_CURRENCY_TAKE_DONE)
                .replace(currency.replacePlaceholders())
                .replace(Placeholders.Player.NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, currency.formatValue(amount))
                .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                .send(sender);
        });


    }
}
