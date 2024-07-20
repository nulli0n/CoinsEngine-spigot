package su.nightexpress.coinsengine.data;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencyData;
import su.nightexpress.coinsengine.data.impl.CurrencySettings;
import su.nightexpress.coinsengine.data.serialize.CurrencyDataSerializer;
import su.nightexpress.coinsengine.data.serialize.CurrencySettingsSerializer;
import su.nightexpress.nightcore.database.AbstractUserDataHandler;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLValue;
import su.nightexpress.nightcore.database.sql.column.ColumnType;
import su.nightexpress.nightcore.database.sql.executor.SelectQueryExecutor;
import su.nightexpress.nightcore.util.Lists;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataHandler<CoinsEnginePlugin, CoinsUser> {

    private static final SQLColumn COLUMN_CURRENCY_LEGACY_DATA = SQLColumn.of("currencyData", ColumnType.STRING);

    private static final SQLColumn COLUMN_SETTINGS = SQLColumn.of("settings", ColumnType.STRING);

    private final Function<ResultSet, CoinsUser> userFunction;

    public DataHandler(@NotNull CoinsEnginePlugin plugin) {
        super(plugin);

        this.userFunction = resultSet -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());


                Set<CurrencyData> legacyDatas = gson.fromJson(resultSet.getString(COLUMN_CURRENCY_LEGACY_DATA.getName()), new TypeToken<Set<CurrencyData>>(){}.getType());
                Map<String, CurrencyData> legacyDataMap = new HashMap<>();
                if (legacyDatas != null && !legacyDatas.isEmpty()) {
                    legacyDatas.forEach(currencyData -> {
                        if (currencyData == null) return;

                        legacyDataMap.put(currencyData.getCurrency().getId(), currencyData);
                    });
                }

                Map<String, CurrencySettings> settingsMap = gson.fromJson(resultSet.getString(COLUMN_SETTINGS.getName()), new TypeToken<Map<String, CurrencySettings>>(){}.getType());
                if (settingsMap == null) settingsMap = new HashMap<>();

                Map<String, Double> balanceMap = new HashMap<>();

                for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
                    double balance = resultSet.getDouble(currency.getColumnName());

                    CurrencyData legacyData = legacyDataMap.get(currency.getId());
                    if (legacyData != null) {
                        balance = legacyData.getBalance();

                        CurrencySettings settings = settingsMap.computeIfAbsent(currency.getId(), k -> CurrencySettings.create(currency));
                        settings.setPaymentsEnabled(legacyData.isPaymentsEnabled());
                    }

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
        return super.registerAdapters(builder)
            .registerTypeAdapter(CurrencyData.class, new CurrencyDataSerializer())
            .registerTypeAdapter(CurrencySettings.class, new CurrencySettingsSerializer());
    }

    @Override
    protected void createUserTable() {
        super.createUserTable();
        this.dropColumn(this.tableUsers, SQLColumn.of("balances", ColumnType.STRING));
        this.addColumn(this.tableUsers, SQLValue.of(COLUMN_SETTINGS, "{}"));
    }

    public void createCurrencyColumn(@NotNull Currency currency) {
        if (this.hasColumn(this.tableUsers, currency.getColumn())) return;

        this.addColumn(this.tableUsers, SQLValue.of(currency.getColumn(), String.valueOf(currency.getStartValue())));
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getLoaded().forEach(this::updateUserBalance);
    }

    private void updateUserBalance(@NotNull CoinsUser user) {
        if (this.plugin.getUserManager().isScheduledToSave(user)) return;

        CoinsUser fresh = this.getUser(user.getId());
        if (fresh == null) return;

        for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
            if (!currency.isSynchronizable()) continue;

            double balance = fresh.getBalance(currency);
            user.setBalance(currency, balance);
        }
    }

    public void resetBalances(@NotNull Currency currency) {
        this.executeUpdate(this.tableUsers, Lists.newList(SQLValue.of(currency.getColumn(), String.valueOf(currency.getStartValue()))), Lists.newList());
    }

    public void resetBalances() {
        List<SQLValue> values = new ArrayList<>();

        for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
            values.add(SQLValue.of(currency.getColumn(), String.valueOf(currency.getStartValue())));
        }

        this.executeUpdate(this.tableUsers, values, Lists.newList());
    }

    @NotNull
    public Map<Currency, Map<String, Double>> getBalances() {
        Map<Currency, Map<String, Double>> map = new HashMap<>();

        Function<ResultSet, Void> function = resultSet -> {
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
        };

        List<SQLColumn> columns = Lists.newList(COLUMN_USER_NAME);
        columns.addAll(this.plugin.getCurrencyManager().getCurrencies().stream().map(Currency::getColumn).toList());

        SelectQueryExecutor.builder(this.tableUsers, function)
            .columns(columns)
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

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Lists.newList(COLUMN_CURRENCY_LEGACY_DATA, COLUMN_SETTINGS);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull CoinsUser user) {
        List<SQLValue> values = new ArrayList<>();

        values.add(COLUMN_CURRENCY_LEGACY_DATA.toValue(this.gson.toJson(new HashSet<>())));
        values.add(COLUMN_SETTINGS.toValue(this.gson.toJson(user.getSettingsMap())));

        for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
            values.add(SQLValue.of(currency.getColumn(), String.valueOf(user.getBalance(currency))));
        }

        return values;
    }

    @Override
    @NotNull
    protected Function<ResultSet, CoinsUser> getUserFunction() {
        return this.userFunction;
    }
}
