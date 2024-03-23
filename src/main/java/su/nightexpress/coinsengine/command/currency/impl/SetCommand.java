package su.nightexpress.coinsengine.command.currency.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandFlags;
import su.nightexpress.coinsengine.command.currency.CurrencySubCommand;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.data.impl.CurrencyData;
import su.nightexpress.coinsengine.util.CoinsUtils;
import su.nightexpress.coinsengine.util.Logger;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.util.Players;

import java.util.Arrays;
import java.util.List;

public class SetCommand extends CurrencySubCommand {

    public SetCommand(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"set"}, Perms.COMMAND_CURRENCY_SET);
        this.setDescription(Lang.COMMAND_CURRENCY_SET_DESC);
        this.setUsage(Lang.COMMAND_CURRENCY_SET_USAGE);
        this.addFlag(CommandFlags.SILENT, CommandFlags.NO_SAVE);
    }

    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Players.playerNames(player);
        }
        if (arg == 2) {
            return Arrays.asList("1", "10", "50", "100");
        }
        return super.getTab(player, arg, args);
    }

    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.errorUsage(sender);
            return;
        }

        double amount = CoinsUtils.getAmountFromInput(result.getArg(2));
        if (amount < 0D) return;

        this.plugin.getUserManager().getUserDataAsync(result.getArg(1)).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            this.plugin.runTask(task -> {
                CurrencyData data = user.getCurrencyData(this.currency);
                data.setBalance(amount);

                if (!result.hasFlag(CommandFlags.NO_SAVE)) {
                    this.plugin.getUserManager().saveAsync(user);
                }

                Logger.logSet(user, this.currency, amount, sender);

                Lang.COMMAND_CURRENCY_SET_DONE.getMessage()
                    .replace(this.currency.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, this.currency.format(amount))
                    .replace(Placeholders.GENERIC_BALANCE, this.currency.format(data.getBalance()))
                    .send(sender);

                Player target = user.getPlayer();

                if (!result.hasFlag(CommandFlags.SILENT) && target != null) {
                    Lang.COMMAND_CURRENCY_SET_NOTIFY.getMessage()
                        .replace(this.currency.replacePlaceholders())
                        .replace(Placeholders.GENERIC_AMOUNT, this.currency.format(amount))
                        .replace(Placeholders.GENERIC_BALANCE, this.currency.format(data.getBalance()))
                        .send(target);
                }
            });
        });
    }
}

