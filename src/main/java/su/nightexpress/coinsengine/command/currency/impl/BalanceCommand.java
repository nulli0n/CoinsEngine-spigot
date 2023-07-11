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

import java.util.List;

public class BalanceCommand extends CurrencySubCommand {

    public BalanceCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"balance", "bal"}, Perms.COMMAND_CURRENCY_BALANCE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_CURRENCY_BALANCE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_CURRENCY_BALANCE_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1 && player.hasPermission(Perms.COMMAND_CURRENCY_BALANCE_OTHERS)) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        int indexOff = this.getParent() == null ? 1 : 0;

        if ((result.length() < 2 - indexOff && !(sender instanceof Player))) {
            this.printUsage(sender);
            return;
        }

        if (result.length() >= 2 - indexOff && !sender.hasPermission(Perms.COMMAND_CURRENCY_BALANCE_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        this.plugin.getUserManager().getUserDataAsync(result.getArg(1 - indexOff, sender.getName())).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            plugin.getMessage(Lang.COMMAND_CURRENCY_BALANCE_DONE)
                .replace(currency.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
                .send(sender);
        });
    }
}
