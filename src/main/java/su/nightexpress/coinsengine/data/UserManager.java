package su.nightexpress.coinsengine.data;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserManager;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

import java.util.UUID;

public class UserManager extends AbstractUserManager<CoinsEngine, CoinsUser> {

    public UserManager(@NotNull CoinsEngine plugin) {
        super(plugin, plugin);
    }

    @Override
    @NotNull
    protected CoinsUser createData(@NotNull UUID uuid, @NotNull String name) {
        return new CoinsUser(plugin, uuid, name);
    }
}
