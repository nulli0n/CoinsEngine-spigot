package su.nightexpress.coinsengine.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;

import java.util.ArrayList;
import java.util.List;

public class WipeCommand extends AbstractCommand<CoinsEngine> {

    public WipeCommand(@NotNull CoinsEngine plugin) {
        super(plugin, new String[]{"wipe"}, Perms.COMMAND_WIPE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_WIPE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_WIPE_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return new ArrayList<>(this.plugin.getCurrencyManager().getCurrencyMap().keySet());
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Currency currency = this.plugin.getCurrencyManager().getCurrency(result.getArg(1));
        if (currency == null) {
            this.plugin.getMessage(Lang.CURRENCY_ERROR_INVALID).send(sender);
            return;
        }

        this.plugin.getMessage(Lang.COMMAND_WIPE_START).replace(currency.replacePlaceholders()).send(sender);
        this.plugin.runTaskAsync(task -> {
            this.plugin.getUserManager().getAllUsers().forEach(user -> {
                user.getCurrencyDataMap().remove(currency.getId());
                this.plugin.getData().saveUser(user);
            });
            this.plugin.getMessage(Lang.COMMAND_WIPE_FINISH).replace(currency.replacePlaceholders()).send(sender);
        });
    }
}
