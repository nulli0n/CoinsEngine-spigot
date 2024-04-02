package su.nightexpress.coinsengine.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

public class WipeCommand extends AbstractCommand<CoinsEnginePlugin> {

    public WipeCommand(@NotNull CoinsEnginePlugin plugin) {
        super(plugin, new String[]{"wipe"}, Perms.COMMAND_WIPE);
        this.setDescription(Lang.COMMAND_WIPE_DESC);
        this.setUsage(Lang.COMMAND_WIPE_USAGE);
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
            this.errorUsage(sender);
            return;
        }

        Currency currency = this.plugin.getCurrencyManager().getCurrency(result.getArg(1));
        if (currency == null) {
            Lang.CURRENCY_ERROR_INVALID.getMessage().send(sender);
            return;
        }

        Lang.COMMAND_WIPE_START.getMessage().replace(currency.replacePlaceholders()).send(sender);
        this.plugin.runTaskAsync(task -> {
            this.plugin.getData().resetBalances();
            this.plugin.getUserManager().getLoaded().forEach(CoinsUser::resetBalance);
            Lang.COMMAND_WIPE_FINISH.getMessage().replace(currency.replacePlaceholders()).send(sender);
        });
    }
}
