package su.nightexpress.coinsengine.migration.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.migration.MigrationPlugin;

public class GamePointsConverter extends AbstractDataConverter {

    public GamePointsConverter(@NotNull CoinsEngine plugin) {
        super(plugin, MigrationPlugin.GAME_POINTS.getPluginName());
    }

    @Override
    public void migrate(@NotNull Currency currency) {
        /*GamePointsAPI.PLUGIN.getData().getUsers().forEach(pointUser -> {
            UUID uuid = pointUser.getId();

            CoinsUser user = this.plugin.getData().getUser(uuid);
            if (user == null) {
                user = new CoinsUser(plugin, uuid, pointUser.getName());
                plugin.getData().addUser(user);
            }

            //CoinsUser user = new CoinsUser(plugin, uuid, pointUser.getName());
            user.setBalance(currency, pointUser.getBalance());
            plugin.getData().saveUser(user);

            /*if (this.plugin.getData().isUserExists(uuid)) {

            }
            else {
                plugin.getData().addUser(user);
            }
        });*/
    }
}
