package su.nightexpress.coinsengine.data;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencySettings;
import su.nightexpress.coinsengine.user.UserBalance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class DataQueries {

    @NotNull
    public static UserBalance readBalance(@NotNull ResultSet resultSet) {
        UserBalance balance = new UserBalance();

        DataHandler.CURRENCY_COLUMNS.forEach((id, column) -> {
            try {
                double amount = resultSet.getDouble(column.getName());
                balance.add(id, amount);
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });

        return balance;
    }

    public static final Function<ResultSet, CoinsUser> USER_LOADER = resultSet -> {
        try {
            UUID uuid = UUID.fromString(resultSet.getString(DataHandler.COLUMN_USER_ID.getName()));
            String name = resultSet.getString(DataHandler.COLUMN_USER_NAME.getName());
            long dateCreated = resultSet.getLong(DataHandler.COLUMN_USER_DATE_CREATED.getName());
            long lastOnline = resultSet.getLong(DataHandler.COLUMN_USER_LAST_ONLINE.getName());

            Map<String, CurrencySettings> settingsMap = DataHandler.GSON.fromJson(resultSet.getString(DataHandler.COLUMN_SETTINGS.getName()), new TypeToken<Map<String, CurrencySettings>>(){}.getType());
            if (settingsMap == null) settingsMap = new HashMap<>();

            UserBalance balance = readBalance(resultSet);

            boolean hiddenFromTops = resultSet.getBoolean(DataHandler.COLUMN_HIDE_FROM_TOPS.getName());

            return new CoinsUser(uuid, name, dateCreated, lastOnline, balance, settingsMap, hiddenFromTops);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };
}
