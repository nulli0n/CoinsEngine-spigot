package su.nightexpress.coinsengine.migration.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.migration.MigrationPlugin;
import su.nightexpress.gamepoints.api.GamePointsAPI;

import java.util.UUID;

public class GamePointsConverter extends AbstractDataConverter {

    public GamePointsConverter(@NotNull CoinsEngine plugin) {
        super(plugin, MigrationPlugin.GAME_POINTS.getPluginName());
    }

    @Override
    public void migrate(@NotNull Currency currency) {
        GamePointsAPI.PLUGIN.getData().getUsers().forEach(pointUser -> {
            UUID uuid = pointUser.getId();

            if (this.plugin.getData().isUserExists(uuid)) return;

            CoinsUser user = new CoinsUser(plugin, uuid, pointUser.getName());
            user.setBalance(currency, pointUser.getBalance());

            plugin.getData().addUser(user);
        });
    }
}
