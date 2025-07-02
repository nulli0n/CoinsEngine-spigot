package su.nightexpress.coinsengine.currency.operation;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.api.currency.CurrencyOperation;
import su.nightexpress.coinsengine.api.currency.OperationResult;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public abstract class AbstractOperation implements CurrencyOperation {

    protected final Currency  currency;
    protected final double    amount;
    protected final CoinsUser user;

    protected boolean loggable;

    public AbstractOperation(@NotNull Currency currency, double amount, @NotNull CoinsUser user) {
        this.currency = currency;
        this.amount = amount;
        this.user = user;

        this.setLoggable(true);
    }

    @Override
    @NotNull
    public OperationResult perform() {
        this.operate();

        long timestamp = System.currentTimeMillis();
        String log = this.createLog();

        return new OperationResult(timestamp, log, true);
    }

    protected abstract void operate();

    @NotNull
    protected abstract String createLog();

    @Override
    @NotNull
    public Currency getCurrency() {
        return this.currency;
    }

    @Override
    public double getAmount() {
        return this.amount;
    }

    @Override
    @NotNull
    public CoinsUser getUser() {
        return this.user;
    }

    @Override
    public boolean isLoggable() {
        return this.loggable;
    }

    @Override
    public void setLoggable(boolean loggable) {
        this.loggable = loggable;
    }
}
