package su.nightexpress.coinsengine.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.nightcore.db.AbstractUserManager;

import java.util.UUID;

public class UserManager extends AbstractUserManager<CoinsEnginePlugin, CoinsUser> {

    public UserManager(@NotNull CoinsEnginePlugin plugin, @NotNull DataHandler dataHandler) {
        super(plugin, dataHandler);
    }

    @Override
    @NotNull
    public CoinsUser create(@NotNull UUID uuid, @NotNull String name) {
        return CoinsUser.create(this.plugin, uuid, name);
    }
}
