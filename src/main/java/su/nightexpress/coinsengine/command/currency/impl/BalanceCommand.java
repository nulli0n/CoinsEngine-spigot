package su.nightexpress.coinsengine.command.currency.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.CurrencySubCommand;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.data.impl.CurrencyData;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.util.Players;

import java.util.List;

public class BalanceCommand extends CurrencySubCommand {

    public BalanceCommand(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"balance", "bal"}, Perms.COMMAND_CURRENCY_BALANCE);
        this.setDescription(Lang.COMMAND_CURRENCY_BALANCE_DESC);
        this.setUsage(Lang.COMMAND_CURRENCY_BALANCE_USAGE);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1 && player.hasPermission(Perms.COMMAND_CURRENCY_BALANCE_OTHERS)) {
            return Players.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        int indexOff = this.getParent() == null ? 1 : 0;

        if ((result.length() < 2 - indexOff && !(sender instanceof Player))) {
            this.errorUsage(sender);
            return;
        }

        if (result.length() >= 2 - indexOff && !sender.hasPermission(Perms.COMMAND_CURRENCY_BALANCE_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String name = result.getArg(1 - indexOff, sender.getName());
        this.plugin.getUserManager().getUserDataAndPerformAsync(name, user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            boolean isOwn = user.getName().equalsIgnoreCase(sender.getName());
            CurrencyData data = user.getCurrencyData(this.currency);
            (isOwn ? Lang.CURRENCY_BALANCE_DISPLAY_OWN : Lang.CURRENCY_BALANCE_DISPLAY_OTHERS).getMessage()
                .replace(this.currency.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_BALANCE, currency.format(data.getBalance()))
                .send(sender);
        });
    }
}
