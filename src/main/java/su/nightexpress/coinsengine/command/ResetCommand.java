package su.nightexpress.coinsengine.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;

import java.util.List;

public class ResetCommand extends AbstractCommand<CoinsEngine> {

    public ResetCommand(@NotNull CoinsEngine plugin) {
        super(plugin, new String[]{"reset"}, Perms.COMMAND_RESET);
        this.setDescription(plugin.getMessage(Lang.COMMAND_RESET_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_RESET_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        this.plugin.getUserManager().getUserDataAsync(result.getArg(1)).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            user.getBalanceMap().clear();
            user.saveData(this.plugin);

            this.plugin.getMessage(Lang.COMMAND_RESET_DONE)
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .send(sender);
        });
    }
}
