package su.nightexpress.coinsengine.data.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;

public class CurrencyData {

    private final Currency currency;

    private final double balance;
    private final boolean paymentsEnabled;

    public CurrencyData(@NotNull Currency currency, double balance, boolean paymentsEnabled) {
        this.currency = currency;
        this.balance = balance;
        this.paymentsEnabled = paymentsEnabled;
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
    }

    public double getBalance() {
        return this.balance;
    }

    public boolean isPaymentsEnabled() {
        return paymentsEnabled;
    }
}
