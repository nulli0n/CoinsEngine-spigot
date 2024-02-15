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
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.util.Players;

import java.util.List;

public class PaymentsCommand extends CurrencySubCommand {

    public PaymentsCommand(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"payments"}, Perms.COMMAND_CURRENCY_PAYMENTS);
        this.setDescription(Lang.COMMAND_CURRENCY_PAYMENTS_DESC);
        this.setUsage(Lang.COMMAND_CURRENCY_PAYMENTS_USAGE);
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1 && player.hasPermission(Perms.COMMAND_CURRENCY_PAYMENTS_OTHERS)) {
            return Players.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() >= 2 && !sender.hasPermission(Perms.COMMAND_CURRENCY_PAYMENTS_OTHERS)) {
            this.errorPermission(sender);
            return;
        }
        if (result.length() <= 1 && !(sender instanceof Player)) {
            this.errorUsage(sender);
            return;
        }

        String name = result.getArg(1, sender.getName());
        this.plugin.getUserManager().getUserDataAndPerformAsync(name, user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            CurrencyData data = user.getCurrencyData(this.currency);
            data.setPaymentsEnabled(!data.isPaymentsEnabled());
            this.plugin.getUserManager().save(user);

            if (!user.getName().equalsIgnoreCase(sender.getName())) {
                Lang.COMMAND_CURRENCY_PAYMENTS_TARGET.getMessage()
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .replace(Placeholders.GENERIC_STATE, CoreLang.getEnabledOrDisabled(data.isPaymentsEnabled()))
                    .send(sender);
            }

            Player target = user.getPlayer();
            if (!result.hasFlag(CommandFlags.SILENT) && target != null) {
                Lang.COMMAND_CURRENCY_PAYMENTS_TOGGLE.getMessage()
                    .replace(currency.replacePlaceholders())
                    .replace(Placeholders.GENERIC_STATE, CoreLang.getEnabledOrDisabled(data.isPaymentsEnabled()))
                    .send(target);
            }
        });
    }
}
