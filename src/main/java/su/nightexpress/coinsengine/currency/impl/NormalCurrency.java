package su.nightexpress.coinsengine.currency.impl;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class NormalCurrency extends AbstractCurrency {

    public NormalCurrency(@NotNull Path path, @NotNull String id) {
        super(path, id);
    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onUnregister() {

    }

    @Override
    public boolean isPrimary() {
        return false;
    }
}
