package su.nightexpress.coinsengine.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.nightcore.database.AbstractUserManager;

import java.util.UUID;

public class UserManager extends AbstractUserManager<CoinsEngine, CoinsUser> {

    public UserManager(@NotNull CoinsEngine plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public CoinsUser createUserData(@NotNull UUID uuid, @NotNull String name) {
        return new CoinsUser(plugin, uuid, name);
    }
}
