package su.nightexpress.coinsengine.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;
import su.nightexpress.nightcore.util.Players;

import java.util.List;

public class ResetCommand extends AbstractCommand<CoinsEngine> {

    public ResetCommand(@NotNull CoinsEngine plugin) {
        super(plugin, new String[]{"reset"}, Perms.COMMAND_RESET);
        this.setDescription(Lang.COMMAND_RESET_DESC);
        this.setUsage(Lang.COMMAND_RESET_USAGE);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Players.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.errorUsage(sender);
            return;
        }

        String name = result.getArg(1, sender.getName());
        this.plugin.getUserManager().getUserDataAndPerformAsync(name, user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            user.getCurrencyDataMap().clear();
            this.plugin.getUserManager().save(user);

            Lang.COMMAND_RESET_DONE.getMessage()
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .send(sender);
        });
    }
}
