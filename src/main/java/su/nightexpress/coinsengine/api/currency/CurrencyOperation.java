package su.nightexpress.coinsengine.api.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public interface CurrencyOperation {

    @NotNull OperationResult perform();

    @NotNull CoinsUser getUser();

    @NotNull Currency getCurrency();

    double getAmount();

    boolean isLoggable();

    void setLoggable(boolean loggable);
}
