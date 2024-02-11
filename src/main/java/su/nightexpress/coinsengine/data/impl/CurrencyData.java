package su.nightexpress.coinsengine.data.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;

public class CurrencyData {

    private final Currency currency;

    private double balance;
    private boolean paymentsEnabled;

    public CurrencyData(@NotNull Currency currency, double balance, boolean paymentsEnabled) {
        this.currency = currency;
        this.setBalance(balance);
        this.setPaymentsEnabled(paymentsEnabled);
    }

    @NotNull
    public static CurrencyData create(@NotNull Currency currency) {
        return new CurrencyData(currency, currency.getStartValue(), true);
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double amount) {
        this.balance = this.getCurrency().fineAndLimit(amount);
    }

    public void addBalance(double amount) {
        this.setBalance(this.getBalance() + Math.abs(this.currency.fine(amount)));
    }

    public void removeBalance(double amount) {
        this.setBalance(this.getBalance() - Math.abs(this.currency.fine(amount)));
    }

    public boolean isPaymentsEnabled() {
        return paymentsEnabled;
    }

    public void setPaymentsEnabled(boolean paymentsEnabled) {
        this.paymentsEnabled = paymentsEnabled;
    }

    @Override
    public String toString() {
        return "CurrencyData{" +
            "currency=" + currency.getId() +
            ", balance=" + balance +
            ", paymentsEnabled=" + paymentsEnabled +
            '}';
    }
}
