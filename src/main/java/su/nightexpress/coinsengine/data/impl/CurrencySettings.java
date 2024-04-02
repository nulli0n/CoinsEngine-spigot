package su.nightexpress.coinsengine.data.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;

public class CurrencySettings {

    private boolean paymentsEnabled;

    public CurrencySettings(boolean paymentsEnabled) {
        this.setPaymentsEnabled(paymentsEnabled);
    }

    @NotNull
    public static CurrencySettings create(@NotNull Currency currency) {
        return new CurrencySettings(true);
    }

    public boolean isPaymentsEnabled() {
        return paymentsEnabled;
    }

    public void setPaymentsEnabled(boolean paymentsEnabled) {
        this.paymentsEnabled = paymentsEnabled;
    }
}
