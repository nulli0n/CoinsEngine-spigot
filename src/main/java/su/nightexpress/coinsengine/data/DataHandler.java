package su.nightexpress.coinsengine.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import su.nightexpress.nightcore.util.Lists;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataManager<CoinsEnginePlugin, CoinsUser> {

    static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(CurrencySettings.class, new CurrencySettingsSerializer())
        .create();

    static final Column COLUMN_SETTINGS       = Column.of("settings", ColumnType.STRING);
    static final Column COLUMN_HIDE_FROM_TOPS = Column.of("hiddenFromTops", ColumnType.BOOLEAN);

    static final Map<String, Column> CURRENCY_COLUMNS = new HashMap<>();

    private boolean synchronizationActive; // A little helper to pause synchronization during operations disable

    public DataHandler(@NotNull CoinsEnginePlugin plugin) {
        super(plugin);
        this.setSynchronizationActive(true);
    }

    public void setSynchronizationActive(boolean synchronizationActive) {
        this.synchronizationActive = synchronizationActive;
    }

    @NotNull
    public String getUsersTable() {
        return this.tableUsers;
    }

    @Override
    protected void onClose() {
        super.onClose();
        CURRENCY_COLUMNS.clear();
    }

    @Override
    @NotNull
    protected Function<ResultSet, CoinsUser> createUserFunction() {
        return DataQueries.USER_LOADER;
    }

    @Override
    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return builder;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.dropColumn(this.tableUsers, "balances", "currencyData");
        this.addColumn(this.tableUsers, COLUMN_SETTINGS, "{}");
        this.addColumn(this.tableUsers, COLUMN_HIDE_FROM_TOPS, String.valueOf(0));
    }

    @NotNull
    public static Column getCurrencyColumn(@NotNull Currency currency) {
        return getCurrencyColumn(currency.getId());
    }

    @NotNull
    public static Column getCurrencyColumn(@NotNull String currencyId) {
        return CURRENCY_COLUMNS.get(currencyId);
    }

    public static boolean isCurrencyColumnCached(@NotNull Currency currency) {
        return CURRENCY_COLUMNS.containsKey(currency.getId());
    }

    public void onCurrencyRegister(@NotNull Currency currency) {
        this.addCurrencyColumn(currency);
    }

    public void onCurrencyUnload(@NotNull Currency currency) {
        CURRENCY_COLUMNS.remove(currency.getId());
    }

    public void addCurrencyColumn(@NotNull Currency currency) {
        Column column = Column.of(currency.getColumnName(), ColumnType.DOUBLE);
        this.addColumn(this.tableUsers, column, String.valueOf(currency.getStartValue()));
        CURRENCY_COLUMNS.put(currency.getId(), column);
    }

    @Override
    protected void addUpsertQueryData(@NotNull ValuedQuery<?, CoinsUser> query) {
        query.setValue(COLUMN_SETTINGS, user -> GSON.toJson(user.getSettingsMap()));
        query.setValue(COLUMN_HIDE_FROM_TOPS, user -> String.valueOf(user.isHiddenFromTops() ? 1 : 0));

        CURRENCY_COLUMNS.forEach((id, column) -> {
            query.setValue(column, user -> String.valueOf(user.getBalance().get(id)));
        });
    }

    @Override
    protected void addSelectQueryData(@NotNull SelectQuery<CoinsUser> query) {
        query.column(COLUMN_SETTINGS);
        query.column(COLUMN_HIDE_FROM_TOPS);
        CURRENCY_COLUMNS.values().forEach(query::column);
    }

    @Override
    protected void addTableColumns(@NotNull List<Column> columns) {
        columns.add(COLUMN_SETTINGS);
        columns.add(COLUMN_HIDE_FROM_TOPS);
    }

    @Override
    public void onSynchronize() {
        // Do not synchronize data if operations are disabled to prevent data loss/clash.
        if (!this.synchronizationActive) return;

        this.synchronizer.syncAll();
    }

    public void resetBalances(@NotNull Currency currency) {
        this.resetBalances(Lists.newSet(currency));
    }

    public void resetBalances(@NotNull Collection<Currency> currencies) {
        UpdateQuery<Object> query = new UpdateQuery<>();

        for (Currency currency : currencies) {
            query.setValue(getCurrencyColumn(currency), o -> String.valueOf(currency.getStartValue()));
        }

        this.update(this.tableUsers, query, new Object()); // Little hack to bypass query params.
    }
}
