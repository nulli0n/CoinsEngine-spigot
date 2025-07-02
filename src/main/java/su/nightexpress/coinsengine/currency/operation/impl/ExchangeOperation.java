package su.nightexpress.coinsengine.currency.operation.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.operation.ConsoleOperation;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public class ExchangeOperation extends ConsoleOperation<Player> {

    private static final String LOG = "%s exchanged %s for %s. New balances: %s and %s.";

    private final Currency target;

    private double result;

    public ExchangeOperation(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull Player sender, @NotNull Currency target) {
        super(currency, amount, user, sender);
        this.target = target;
    }

    @Override
    protected void notifyUser() {

    }

    @Override
    protected void sendFeedback() {
        this.currency.sendPrefixed(Lang.CURRENCY_EXCHANGE_SUCCESS, this.sender, replacer -> replacer
            .replace(this.currency.replacePlaceholders())
            .replace(Placeholders.GENERIC_BALANCE, this.currency.format(this.amount))
            .replace(Placeholders.GENERIC_AMOUNT, this.target.format(this.result))
        );
    }

    @Override
    protected void operate() {
        this.result = this.currency.getExchangeResult(this.target, this.amount);

        this.user.removeBalance(this.currency, this.amount);
        this.user.addBalance(this.target, this.result);
    }

    @Override
    @NotNull
    protected String createLog() {
        return LOG.formatted(
            this.user.getName(),
            this.currency.format(this.amount),
            this.target.format(this.result),
            this.currency.format(this.user.getBalance(this.currency)),
            this.target.format(this.user.getBalance(this.target))
        );
    }
}
