package su.nightexpress.coinsengine.currency.operation.impl;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.operation.ConsoleOperation;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public class ResetOperation extends ConsoleOperation<CommandSender> {

    private static final String LOG = "%s reset %s's balance of %s to %s.";

    public ResetOperation(@NotNull Currency currency, @NotNull CoinsUser user, @NotNull CommandSender sender) {
        super(currency, currency.getStartValue(), user, sender);
    }

    @Override
    protected void notifyUser() {
        // TODO
    }

    @Override
    protected void sendFeedback() {
        Lang.COMMAND_RESET_DONE.getMessage().send(this.sender, replacer -> replacer
            .replace(Placeholders.PLAYER_NAME, this.user.getName())
            .replace(Placeholders.GENERIC_BALANCE, this.user.getBalance(this.currency))
        );
    }

    @Override
    protected void operate() {
        this.user.resetBalance(this.currency);
    }

    @Override
    @NotNull
    protected String createLog() {
        return LOG.formatted(
            this.sender.getName(),
            this.user.getName(),
            this.currency.getName(),
            this.currency.format(this.amount)
        );
    }
}
