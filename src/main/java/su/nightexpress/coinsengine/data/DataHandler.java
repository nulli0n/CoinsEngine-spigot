package su.nightexpress.coinsengine.data;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserDataHandler;
import su.nexmedia.engine.api.data.sql.SQLColumn;
import su.nexmedia.engine.api.data.sql.SQLValue;
import su.nexmedia.engine.api.data.sql.column.ColumnType;
import su.nexmedia.engine.api.data.sql.executor.SelectQueryExecutor;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencyData;
import su.nightexpress.coinsengine.data.serialize.CurrencyDataSerializer;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataHandler<CoinsEngine, CoinsUser> implements PluginMessageListener {

    private static final SQLColumn COLUMN_BALANCES = SQLColumn.of("balances", ColumnType.STRING);
    private static final SQLColumn COLUMN_CURRENCY_DATA = SQLColumn.of("currencyData", ColumnType.STRING);

    private static DataHandler instance;

    private final Function<ResultSet, CoinsUser> userFunction;

    @Override
    public void saveUser(@NotNull CoinsUser user) {
        super.saveUser(user);

        // For some reason, NexEngine doesn't return SQL execution result.
        // I can't detect if it actually updates the player data. Just add a slight delay here.
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            Map<String, String> map = new HashMap<>();
            map.put("name", user.getName());
            map.put("uuid", user.getId().toString());

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ALL");
            out.writeUTF("CoinsEngine:RefreshUserBalance");
            out.writeUTF(new Gson().toJson(map));

            Bukkit.getServer().sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray());
        }, 1L);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, Player player, byte[] message) {
        if (!"BungeeCord".equals(channel)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.startsWith("CoinsEngine:")) {
            if (subChannel.endsWith("RefreshUserBalance")) {
                String content = in.readUTF();
                Type typeToken = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> map = new Gson().fromJson(content, typeToken);

                CompletableFuture<CoinsUser> coinsUserCompletableFuture;
                try {
                    UUID uuid = UUID.fromString(map.get("uuid"));
                    coinsUserCompletableFuture = this.plugin.getUserManager().getUserDataAsync(uuid);
                } catch (IllegalArgumentException e) {
                    String name = map.get("name");
                    coinsUserCompletableFuture = this.plugin.getUserManager().getUserDataAsync(name);
                }
                // Refresh player balance due to other server notifications
                coinsUserCompletableFuture.thenAcceptAsync(this::updateUserBalance);
            }
        }
    }

    DataHandler(@NotNull CoinsEngine plugin) {
        super(plugin, plugin);

        this.userFunction = (resultSet) -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                Set<CurrencyData> curDatas = gson.fromJson(resultSet.getString(COLUMN_CURRENCY_DATA.getName()), new TypeToken<Set<CurrencyData>>(){}.getType());
                if (curDatas == null) curDatas = new HashSet<>();
                curDatas.removeIf(Objects::isNull);

                /*Map<String, Double> balanceMap = gson.fromJson(resultSet.getString(COLUMN_BALANCES.getName()), new TypeToken<Map<String, Double>>(){}.getType());
                if (!balanceMap.isEmpty()) {
                    Set<CurrencyData> finalCurDatas = curDatas;
                    balanceMap.forEach((id, balance) -> {
                        Currency currency = plugin.getCurrencyManager().getCurrency(id);
                        if (currency == null) return;

                        CurrencyData data = CurrencyData.create(currency);
                        data.setBalance(balance);
                        finalCurDatas.add(data);
                    });
                    curDatas.addAll(finalCurDatas);
                }*/

                return new CoinsUser(plugin, uuid, name, dateCreated, lastOnline, curDatas);
            }
            catch (SQLException exception) {
                exception.printStackTrace();
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
    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return super.registerAdapters(builder)
            .registerTypeAdapter(CurrencyData.class, new CurrencyDataSerializer());
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
        this.dropColumn(this.tableUsers, COLUMN_BALANCES);
    }

    /*@Override
    protected void createUserTable() {
        super.createUserTable();
        this.addColumn(this.tableUsers, COLUMN_BALANCES.toValue("{}"));
        this.addColumn(this.tableUsers, COLUMN_CURRENCY_DATA.toValue("[]"));
    }*/

    public void updateUserBalance(@NotNull CoinsUser user) {
        CoinsUser fromDb = this.getUser(user.getId());
        if (fromDb == null) return;

        user.getCurrencyDataMap().clear();
        user.getCurrencyDataMap().putAll(fromDb.getCurrencyDataMap());
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

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Arrays.asList(/*COLUMN_BALANCES, */COLUMN_CURRENCY_DATA);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull CoinsUser user) {
        /*if (!user.isRecentlyCreated() && user.getCurrencyDataMap().isEmpty()) {
            this.plugin.warn("Empty currency data save for '" + user.getName() + "'. UUID: " + user.getId());
            new Throwable().printStackTrace();
        }*/

        return Arrays.asList(
            //COLUMN_BALANCES.toValue("{}"),
            COLUMN_CURRENCY_DATA.toValue(this.gson.toJson(new HashSet<>(user.getCurrencyDataMap().values())))
        );
    }

    @Override
    @NotNull
    protected Function<ResultSet, CoinsUser> getFunctionToUser() {
        return this.userFunction;
    }
}
