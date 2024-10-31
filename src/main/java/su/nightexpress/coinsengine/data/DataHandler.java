package su.nightexpress.coinsengine.data;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencySettings;
import su.nightexpress.coinsengine.data.serialize.CurrencySettingsSerializer;
import su.nightexpress.nightcore.db.AbstractUserDataManager;
import su.nightexpress.nightcore.db.sql.column.Column;
import su.nightexpress.nightcore.db.sql.column.ColumnType;
import su.nightexpress.nightcore.db.sql.query.impl.SelectQuery;
import su.nightexpress.nightcore.db.sql.query.impl.UpdateQuery;
import su.nightexpress.nightcore.db.sql.query.type.ValuedQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataManager<CoinsEnginePlugin, CoinsUser> {

    private static final Column COLUMN_SETTINGS = Column.of("settings", ColumnType.STRING);

    public DataHandler(@NotNull CoinsEnginePlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    protected Function<ResultSet, CoinsUser> createUserFunction() {
        return resultSet -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                Map<String, CurrencySettings> settingsMap = gson.fromJson(resultSet.getString(COLUMN_SETTINGS.getName()), new TypeToken<Map<String, CurrencySettings>>(){}.getType());
                if (settingsMap == null) settingsMap = new HashMap<>();

                Map<String, Double> balanceMap = new HashMap<>();

                for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
                    double balance = resultSet.getDouble(currency.getColumnName());

                    balanceMap.put(currency.getId(), balance);
                }

                return new CoinsUser(plugin, uuid, name, dateCreated, lastOnline, balanceMap, settingsMap);
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
        return builder
            .registerTypeAdapter(CurrencySettings.class, new CurrencySettingsSerializer());
    }

    @Override
    protected void createUserTable() {
        super.createUserTable();

        this.dropColumn(this.tableUsers, "balances", "currencyData");
        this.addColumn(this.tableUsers, COLUMN_SETTINGS, "{}");
    }

    @Override
    protected void addUpsertQueryData(@NotNull ValuedQuery<?, CoinsUser> query) {
        query.setValue(COLUMN_SETTINGS, user -> this.gson.toJson(user.getSettingsMap()));

        for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
            query.setValue(currency.getColumn(), user -> String.valueOf(user.getBalance(currency)));
        }
    }

    @Override
    protected void addSelectQueryData(@NotNull SelectQuery<CoinsUser> query) {
        query.column(COLUMN_SETTINGS);

        for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
            query.column(currency.getColumn());
        }
    }

    @Override
    protected void addTableColumns(@NotNull List<Column> columns) {
        columns.add(COLUMN_SETTINGS);
    }

    public void createCurrencyColumn(@NotNull Currency currency) {
        this.addColumn(this.tableUsers, currency.getColumn(), String.valueOf(currency.getStartValue()));
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getLoaded().forEach(this::updateUserBalance);
    }

    private void updateUserBalance(@NotNull CoinsUser user) {
        if (user.isAutoSavePlanned()) return;

        CoinsUser fresh = this.getUser(user.getId());
        if (fresh == null) return;

        if (!user.isAutoSyncReady()) return;

        for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
            if (!currency.isSynchronizable()) continue;

            double balance = fresh.getBalance(currency);
            user.setBalance(currency, balance);
        }
    }

    public void resetBalances(@NotNull Currency currency) {
        UpdateQuery<Currency> query = new UpdateQuery<>();

        query.setValue(currency.getColumn(), c -> String.valueOf(c.getStartValue()));

        this.update(this.tableUsers, query, currency);
    }

    public void resetBalances() {
        UpdateQuery<Currency> query = new UpdateQuery<>();

        for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
            query.setValue(currency.getColumn(), c -> String.valueOf(c.getStartValue()));
        }

        this.update(this.tableUsers, query, plugin.getCurrencyManager().getCurrencies());
    }

    @NotNull
    public Map<Currency, Map<String, Double>> getBalances() {
        Map<Currency, Map<String, Double>> map = new HashMap<>();

        SelectQuery<Void> query = new SelectQuery<>(resultSet -> {
            try {
                String name = resultSet.getString(COLUMN_USER_NAME.getName());

                for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
                    double balance = resultSet.getDouble(currency.getColumnName());
                    map.computeIfAbsent(currency, k -> new HashMap<>()).put(name, balance);
                }
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
            return null;
        });

        query.column(COLUMN_USER_NAME);
        for (Currency currency : plugin.getCurrencyManager().getCurrencies()) {
            query.column(currency.getColumn());
        }

        this.select(this.tableUsers, query);

//        for (int i = 0; i < 30; i++) {
//            String name = "DummyPlayer_" + i;
//            map.values().forEach(data -> data.put(name, Rnd.getDouble(1000)));
//        }

        return map;
    }
}
