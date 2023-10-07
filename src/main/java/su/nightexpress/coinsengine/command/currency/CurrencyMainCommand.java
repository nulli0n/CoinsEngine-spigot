package su.nightexpress.coinsengine.command.currency;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.impl.*;

public class CurrencyMainCommand extends GeneralCommand<CoinsEngine> {

    public CurrencyMainCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency) {
        super(plugin, currency.getCommandAliases(), currency.isPermissionRequired() ? currency.getPermission() : null);

        this.addChildren(new HelpSubCommand<>(plugin));
        this.addDefaultCommand(new BalanceCommand(plugin, currency));
        this.addChildren(new TopCommand(plugin, currency, "top"));
        this.addChildren(new GiveCommand(plugin, currency));
        this.addChildren(new SetCommand(plugin, currency));
        this.addChildren(new TakeCommand(plugin, currency));
        if (currency.isTransferAllowed()) {
            this.addChildren(new SendCommand(plugin, currency));
            this.addChildren(new PaymentsCommand(plugin, currency));
        }
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
