package su.nightexpress.coinsengine.data;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencyData;
import su.nightexpress.coinsengine.data.serialize.CurrencyDataSerializer;
import su.nightexpress.nightcore.database.AbstractUserDataHandler;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLCondition;
import su.nightexpress.nightcore.database.sql.SQLValue;
import su.nightexpress.nightcore.database.sql.column.ColumnType;
import su.nightexpress.nightcore.database.sql.executor.SelectQueryExecutor;
import su.nightexpress.nightcore.util.Lists;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataHandler<CoinsEnginePlugin, CoinsUser> {

    private static final SQLColumn COLUMN_CURRENCY_DATA = SQLColumn.of("currencyData", ColumnType.STRING);

    private final Function<ResultSet, CoinsUser> userFunction;

    public DataHandler(@NotNull CoinsEnginePlugin plugin) {
        super(plugin);

        this.userFunction = resultSet -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                Set<CurrencyData> data = gson.fromJson(resultSet.getString(COLUMN_CURRENCY_DATA.getName()), new TypeToken<Set<CurrencyData>>(){}.getType());
                if (data == null) data = new HashSet<>();
                data.removeIf(Objects::isNull);

                return new CoinsUser(plugin, uuid, name, dateCreated, lastOnline, data);
            }
            catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        };
    }

    @Override
    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return super.registerAdapters(builder)
            .registerTypeAdapter(CurrencyData.class, new CurrencyDataSerializer());
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getLoaded().forEach(this::updateUserBalance);
    }

    public void updateUserBalance(@NotNull CoinsUser user) {
        Set<CurrencyData> data = this.getBalances(user.getId());
        if (data == null || data.isEmpty()) return;

        user.getCurrencyDataMap().clear();

        data.forEach(currencyData -> {
            user.getCurrencyDataMap().put(currencyData.getCurrency().getId(), currencyData);
        });
    }

    @NotNull
    public Map<Currency, Map<String, Double>> getBalances() {
        Map<Currency, Map<String, Double>> map = new HashMap<>();

        Function<ResultSet, Void> function = resultSet -> {
            try {
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                Set<CurrencyData> currencyData = gson.fromJson(resultSet.getString(COLUMN_CURRENCY_DATA.getName()), new TypeToken<Set<CurrencyData>>(){}.getType());
                if (currencyData == null) currencyData = new HashSet<>();
                currencyData.removeIf(Objects::isNull);

                currencyData.forEach(data -> {
                    map.computeIfAbsent(data.getCurrency(), k -> new HashMap<>()).put(name, data.getBalance());
                });
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        SelectQueryExecutor.builder(this.tableUsers, function)
            .columns(COLUMN_USER_NAME, COLUMN_CURRENCY_DATA)
            .execute(this.getConnector());

        /*map.values().forEach(data -> {
            data.put("MoonBunny", Rnd.getDouble(500));
            data.put("7teen", Rnd.getDouble(1200));
            data.put("har1us", Rnd.getDouble(2000));
            data.put("lPariahl", Rnd.getDouble(800));
            data.put("AquaticFlamesIV", Rnd.getDouble(600));
            data.put("YaZanoZa", Rnd.getDouble(200));
            data.put("S_T_I_N_O_L", Rnd.getDouble(400));
            data.put("konoos", Rnd.getDouble(100));
            data.put("Noob_Perforator", Rnd.getDouble(80));
        });*/

        return map;
    }

    @Nullable
    public Set<CurrencyData> getBalances(@NotNull UUID uuid) {
        Function<ResultSet, Set<CurrencyData>> function = resultSet -> {
            try {
                return gson.fromJson(resultSet.getString(COLUMN_CURRENCY_DATA.getName()), new TypeToken<Set<CurrencyData>>(){}.getType());
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
            return null;
        };

        return this.load(this.tableUsers, function,
            Lists.newList(COLUMN_CURRENCY_DATA),
            Lists.newList(SQLCondition.equal(COLUMN_USER_ID.toValue(uuid)))
        ).orElse(null);
    }

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Lists.newList(COLUMN_CURRENCY_DATA);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull CoinsUser user) {
        return Lists.newList(
            COLUMN_CURRENCY_DATA.toValue(this.gson.toJson(new HashSet<>(user.getCurrencyDataMap().values())))
        );
    }

    @Override
    @NotNull
    protected Function<ResultSet, CoinsUser> getUserFunction() {
        return this.userFunction;
    }
}
