package su.nightexpress.coinsengine.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.currency.impl.AbstractCurrency;
import su.nightexpress.coinsengine.currency.impl.EconomyCurrency;
import su.nightexpress.coinsengine.currency.impl.NormalCurrency;
import su.nightexpress.coinsengine.data.DataHandler;
import su.nightexpress.coinsengine.user.UserManager;

import java.nio.file.Path;

public class CurrencyFactory {

    private CurrencyFactory() {}

    @NotNull
    public static AbstractCurrency createEconomy(@NotNull Path path,
                                                 @NotNull String id,
                                                 @NotNull CoinsEnginePlugin plugin,
                                                 @NotNull CurrencyManager currencyManager,
                                                 @NotNull DataHandler dataHandler,
                                                 @NotNull UserManager userManager) {
        return new EconomyCurrency(path, id, plugin, currencyManager, dataHandler, userManager);
    }

    @NotNull
    public static AbstractCurrency createNormal(@NotNull Path path, @NotNull String id) {
        return new NormalCurrency(path, id);
    }
}
