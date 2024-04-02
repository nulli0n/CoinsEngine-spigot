package su.nightexpress.coinsengine.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.nightcore.database.AbstractUserManager;

import java.util.UUID;

public class UserManager extends AbstractUserManager<CoinsEnginePlugin, CoinsUser> {

    public UserManager(@NotNull CoinsEnginePlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public CoinsUser createUserData(@NotNull UUID uuid, @NotNull String name) {
        return CoinsUser.create(plugin, uuid, name);
    }
}
