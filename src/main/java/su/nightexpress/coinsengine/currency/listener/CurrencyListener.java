package su.nightexpress.coinsengine.currency.listener;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.currency.CurrencyManager;

public class CurrencyListener extends AbstractListener<CoinsEngine> {

    //private final CurrencyManager manager;

    public CurrencyListener(@NotNull CurrencyManager manager) {
        super(manager.plugin());
        //this.manager = manager;
    }

}
