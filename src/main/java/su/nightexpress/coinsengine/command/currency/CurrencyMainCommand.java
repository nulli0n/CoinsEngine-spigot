package su.nightexpress.coinsengine.command.currency;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.impl.*;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.base.HelpSubCommand;
import su.nightexpress.nightcore.command.impl.PluginCommand;

public class CurrencyMainCommand extends PluginCommand<CoinsEnginePlugin> {

    public CurrencyMainCommand(@NotNull CoinsEnginePlugin plugin, @NotNull Currency currency) {
        super(plugin, currency.getCommandAliases(), currency.isPermissionRequired() ? currency.getPermission() : null);

        this.addChildren(new HelpSubCommand(plugin));
        this.addDefaultCommand(new BalanceCommand(plugin, currency));
        this.addChildren(new TopCommand(plugin, currency, "top"));
        this.addChildren(new GiveCommand(plugin, currency));
        this.addChildren(new SetCommand(plugin, currency));
        this.addChildren(new TakeCommand(plugin, currency));
        if (currency.isTransferAllowed()) {
            this.addChildren(new SendCommand(plugin, currency));
            this.addChildren(new PaymentsCommand(plugin, currency));
        }
        if (currency.isExchangeAllowed()) {
            this.addChildren(new ExchangeCommand(plugin, currency));
        }
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
