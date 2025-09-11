package su.nightexpress.coinsengine.currency.operation.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.operation.ConsoleOperation;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public class SendOperation extends ConsoleOperation<Player> {

    private static final String LOG = "%s paid %s to %s. New balances: %s and %s.";

    private final CoinsUser fromUser;

    public SendOperation(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull Player sender, @NotNull CoinsUser fromUser) {
        super(currency, amount, user, sender);
        this.fromUser = fromUser;
    }

    @Override
    protected void operate() {
        this.user.addBalance(this.currency, this.amount);
        this.fromUser.removeBalance(this.currency, this.amount);
    }

    @Override
    protected void notifyUser() {
        Player player = this.user.getPlayer();
        if (player == null) return;

        this.currency.sendPrefixed(Lang.CURRENCY_SEND_DONE_NOTIFY, player, replacer -> replacer
            .replace(this.currency.replacePlaceholders())
            .replace(Placeholders.GENERIC_AMOUNT, this.currency.format(this.amount))
            .replace(Placeholders.GENERIC_BALANCE, this.user.getBalance(this.currency))
            .replace(Placeholders.PLAYER_NAME, this.sender.getName())
        );
    }

    @Override
    protected void sendFeedback() {
        this.currency.sendPrefixed(Lang.CURRENCY_SEND_DONE_SENDER, this.sender, replacer -> replacer
            .replace(this.currency.replacePlaceholders())
            .replace(Placeholders.GENERIC_AMOUNT, this.currency.format(this.amount))
            .replace(Placeholders.GENERIC_BALANCE, this.fromUser.getBalance(this.currency))
            .replace(Placeholders.PLAYER_NAME, this.user.getName())
        );
    }

    @Override
    @NotNull
    protected String createLog() {
        return LOG.formatted(
            this.sender.getName(),
            this.currency.format(this.amount),
            this.user.getName(),
            this.currency.format(this.fromUser.getBalance(this.currency)),
            this.currency.format(this.user.getBalance(this.currency))
        );
    }
}
