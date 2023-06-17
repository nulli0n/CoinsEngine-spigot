package su.nightexpress.coinsengine.data;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserDataHandler;
import su.nexmedia.engine.api.data.sql.SQLColumn;
import su.nexmedia.engine.api.data.sql.SQLValue;
import su.nexmedia.engine.api.data.sql.column.ColumnType;
import su.nexmedia.engine.api.data.sql.executor.SelectQueryExecutor;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataHandler<CoinsEngine, CoinsUser> {

    private static DataHandler instance;

    private final Function<ResultSet, CoinsUser> userFunction;

    private static final SQLColumn COLUMN_BALANCE   = SQLColumn.of("balance", ColumnType.INTEGER);
    private static final SQLColumn COLUMN_BALANCES = SQLColumn.of("balances", ColumnType.STRING);

    DataHandler(@NotNull CoinsEngine plugin) {
        super(plugin, plugin);

        this.userFunction = (resultSet) -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                Map<String, Double> balanceMap = gson.fromJson(resultSet.getString(COLUMN_BALANCES.getName()), new TypeToken<Map<String, Double>>(){}.getType());

                return new CoinsUser(plugin, uuid, name, dateCreated, lastOnline, balanceMap);
            }
            catch (SQLException e) {
                return null;
            }
        };
    }

    @NotNull
    public static DataHandler getInstance(@NotNull CoinsEngine plugin) {
        if (instance == null) {
            instance = new DataHandler(plugin);
        }
        return instance;
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
        instance = null;
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getUsersLoaded().forEach(this::updateUserBalance);
    }

    @Override
    protected void createUserTable() {
        super.createUserTable();
        this.addColumn(this.tableUsers, COLUMN_BALANCES.toValue("{}"));
    }

    public void migrateOldPoints() {
        if (this.hasColumn(this.tableUsers, COLUMN_BALANCE)) {
            this.plugin().getCurrencyManager().getCurrencies().stream().findAny().ifPresent(currency -> {
                this.plugin.info("Started data migration from GamePoints into new one. This may take a while...");

                Map<String, Integer> balanceOld = this.getOldBalances();
                List<CoinsUser> users = this.getUsers();

                users.forEach(user -> {
                    user.setBalance(currency, balanceOld.getOrDefault(user.getName(), 0));
                    this.saveUser(user);
                });
                this.dropColumn(this.tableUsers, COLUMN_BALANCE);
                this.plugin.info("GamePoints migration completed for " + balanceOld.size() + " users!");
            });
        }
    }

    public void updateUserBalance(@NotNull CoinsUser user) {
        CoinsUser fromDb = this.getUser(user.getId());
        if (fromDb == null) return;

        user.getBalanceMap().clear();
        user.getBalanceMap().putAll(fromDb.getBalanceMap());
    }

    @NotNull
    private Map<String, Integer> getOldBalances() {
        Map<String, Integer> map = new HashMap<>();

        Function<ResultSet, Void> function = resultSet -> {
            try {
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                int balance = resultSet.getInt(COLUMN_BALANCE.getName());

                map.put(name, balance);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        SelectQueryExecutor.builder(this.tableUsers, function)
            .columns(COLUMN_USER_NAME, COLUMN_BALANCE)
            .execute(this.getConnector());
        return map;
    }

    @NotNull
    public Map<Currency, Map<String, Double>> getBalances() {
        Map<Currency, Map<String, Double>> map = new HashMap<>();

        Function<ResultSet, Void> function = resultSet -> {
            try {
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                Map<String, Double> balanceMap = gson.fromJson(resultSet.getString(COLUMN_BALANCES.getName()), new TypeToken<Map<String, Double>>(){}.getType());

                this.plugin.getCurrencyManager().getCurrencies().forEach(currency -> {
                    map.computeIfAbsent(currency, k -> new HashMap<>()).put(name, balanceMap.getOrDefault(currency.getId(), 0D));
                });
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        SelectQueryExecutor.builder(this.tableUsers, function)
            .columns(COLUMN_USER_NAME, COLUMN_BALANCES)
            .execute(this.getConnector());

        map.values().forEach(data -> {
            data.put("MoonBunny", Rnd.getDouble(500));
            data.put("7teen", Rnd.getDouble(1200));
            data.put("har1us", Rnd.getDouble(2000));
            data.put("lPariahl", Rnd.getDouble(800));
            data.put("AquaticFlamesIV", Rnd.getDouble(600));
            data.put("YaZanoZa", Rnd.getDouble(200));
            data.put("S_T_I_N_O_L", Rnd.getDouble(400));
            data.put("konoos", Rnd.getDouble(100));
            data.put("Noob_Perforator", Rnd.getDouble(80));
        });

        return map;
    }

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Arrays.asList(COLUMN_BALANCES);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull CoinsUser user) {
        return Arrays.asList(
            COLUMN_BALANCES.toValue(this.gson.toJson(user.getBalanceMap()))
        );
    }

    @Override
    @NotNull
    protected Function<ResultSet, CoinsUser> getFunctionToUser() {
        return this.userFunction;
    }
}
