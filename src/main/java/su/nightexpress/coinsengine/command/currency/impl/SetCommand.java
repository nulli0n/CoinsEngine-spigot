package su.nightexpress.coinsengine.command.currency.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandFlags;
import su.nightexpress.coinsengine.command.currency.CurrencySubCommand;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.data.impl.CurrencyData;
import su.nightexpress.coinsengine.util.CoinsLogger;

import java.util.Arrays;
import java.util.List;

public class SetCommand extends CurrencySubCommand {

    public SetCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"set"}, Perms.COMMAND_CURRENCY_SET);
        this.setDescription(plugin.getMessage(Lang.COMMAND_CURRENCY_SET_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_CURRENCY_SET_USAGE));
        this.addFlag(CommandFlags.SILENT);
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

        double amount = result.getDouble(2, -1);
        if (amount < 0D) return;

        this.plugin.getUserManager().getUserDataAsync(result.getArg(1)).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            CurrencyData data = user.getCurrencyData(this.currency);
            data.setBalance(amount);
            user.saveData(this.plugin);

            CoinsLogger.logSet(user, currency, amount, sender);

            plugin.getMessage(Lang.COMMAND_CURRENCY_SET_DONE)
                .replace(currency.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                .replace(Placeholders.GENERIC_BALANCE, currency.format(data.getBalance()))
                .send(sender);

            Player target = user.getPlayer();
            if (!result.hasFlag(CommandFlags.SILENT) && target != null) {
                plugin.getMessage(Lang.COMMAND_CURRENCY_SET_NOTIFY)
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, currency.format(data.getBalance()))
                    .send(sender);
            }
        });
    }
}
