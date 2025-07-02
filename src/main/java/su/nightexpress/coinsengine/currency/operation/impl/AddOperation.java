package su.nightexpress.coinsengine.currency.operation.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.operation.ConsoleOperation;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public class AddOperation extends ConsoleOperation<CommandSender> {

    private static final String LOG = "%s gave %s to %s. New balance: %s";

    public AddOperation(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull CommandSender sender) {
        super(currency, amount, user, sender);
    }

    @Override
    protected void operate() {
        this.user.addBalance(this.currency, this.amount);
    }

    @Override
    @NotNull
    protected String createLog() {
        return LOG.formatted(this.sender.getName(), this.currency.format(this.amount), this.user.getName(), this.currency.format(this.user.getBalance(this.currency)));
    }

    @Override
    protected void sendFeedback() {
        this.currency.sendPrefixed(Lang.COMMAND_CURRENCY_GIVE_DONE, this.sender, replacer -> replacer
            .replace(currency.replacePlaceholders())
            .replace(Placeholders.PLAYER_NAME, user.getName())
            .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
            .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
        );
    }

    @Override
    protected void notifyUser() {
        Player target = this.user.getPlayer();
        if (target == null) return;

        this.currency.sendPrefixed(Lang.COMMAND_CURRENCY_GIVE_NOTIFY, target, replacer -> replacer
            .replace(currency.replacePlaceholders())
            .replace(Placeholders.GENERIC_AMOUNT, currency.format(amount))
            .replace(Placeholders.GENERIC_BALANCE, currency.format(user.getBalance(currency)))
        );
    }
}
