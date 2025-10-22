package su.nightexpress.coinsengine.migration.impl;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.hook.HookPlugin;
import su.nightexpress.coinsengine.migration.Migrator;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerPointsMigrator extends Migrator {

    public PlayerPointsMigrator(@NotNull CoinsEnginePlugin plugin) {
        super(plugin, HookPlugin.PLAYER_POINTS);
    }

    @Override
    public boolean canMigrate(@NotNull Currency currency) {
        return true;
    }

    @Override
    @NotNull
    public Map<OfflinePlayer, Double> getBalances(@NotNull Currency currency) {
        Map<OfflinePlayer, Double> balances = new HashMap<>();

        PlayerPoints playerPoints = (PlayerPoints) this.getBackend();
        if (playerPoints == null) return balances;

        Map<UUID, Integer> pointsMap = new HashMap<>();

        DataManager dataManager = playerPoints.getManager(DataManager.class);
        try {
            dataManager.getDatabaseConnector().connect(connection -> {
                String query = "SELECT * FROM " + dataManager.getTablePrefix() + "points";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    int points = resultSet.getInt("points");

                    pointsMap.put(uuid, points);
                }
                statement.close();
            });
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        pointsMap.forEach((id, balance) -> {
            OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(id);
            balances.put(offlinePlayer, (double) balance);
        });

        return balances;
    }
}
