package su.nightexpress.coinsengine.currency.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.column.ColumnType;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigCurrency extends AbstractFileData<CoinsEnginePlugin> implements Currency {

    private String              name;
    private String              symbol;
    private String              format;
    private String              formatShort;
    private String              columnName;
    private String[]            commandAliases;
    private ItemStack           icon;
    private SQLColumn           column;
    private boolean             decimal;
    private boolean             synchronizable;
    private boolean             permissionRequired;
    private boolean             transferAllowed;
    private double              minTransferAmount;
    private double              startValue;
    private double              maxValue;
    private boolean             vaultEconomy;
    private boolean             exchangeAllowed;
    private Map<String, Double> exchangeRates;

    private final PlaceholderMap placeholderMap;

    public ConfigCurrency(@NotNull CoinsEnginePlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.exchangeRates = new HashMap<>();
        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.CURRENCY_ID, this::getId)
            .add(Placeholders.CURRENCY_NAME, this::getName)
            .add(Placeholders.CURRENCY_SYMBOL, this::getSymbol);
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setName(ConfigValue.create("Name", StringUtil.capitalizeUnderscored(this.getId()),
            "Localized currency name.",
            "Text formation allowed: " + Placeholders.WIKI_TEXT_URL
        ).read(config));

        this.setSymbol(ConfigValue.create("Symbol", this.getName(),
            "Currency symbol, like '$'.",
            "Text formation allowed: " + Placeholders.WIKI_TEXT_URL
        ).read(config));

        this.setFormat(ConfigValue.create("Format",
            Placeholders.GENERIC_AMOUNT + Placeholders.CURRENCY_SYMBOL,
            "Currency display format.",
            "Use '" + Placeholders.GENERIC_AMOUNT + "' placeholder for amount value.",
            "You can use 'Currency' placeholders: " + Placeholders.WIKI_PLACEHOLDERS,
            "Text formation allowed: " + Placeholders.WIKI_TEXT_URL
        ).read(config));

        this.setFormatShort(ConfigValue.create("Format_Short",
            Placeholders.CURRENCY_SYMBOL + Placeholders.GENERIC_AMOUNT + Placeholders.CURRENCY_SHORT_SYMBOL,
            "Currency short display format.",
            "Use '" + Placeholders.GENERIC_AMOUNT + "' placeholder for amount value.",
            "Use '" + Placeholders.CURRENCY_SHORT_SYMBOL + "' placeholder for short symbol (k, m, b, t, q).",
            "You can use 'Currency' placeholders: " + Placeholders.WIKI_PLACEHOLDERS
        ).read(config));

        this.setColumnName(ConfigValue.create("Column_Name",
            this.getId(),
            "Sets database column name for this currency.",
            "This might be useful for MySQL if you want to use the same currency for multiple servers, but not share their balance.",
            "=".repeat(15) + " WARNING " + "=".repeat(15),
            "When chaning this setting, all balances will retain in PREVIOUS column!"
        ).read(config));

        this.setCommandAliases(ConfigValue.create("Command_Aliases",
            new String[]{this.getId()},
            "Custom currency commands. Split with commas."
        ).read(config));

        this.setIcon(ConfigValue.create("Icon",
            new ItemStack(Material.GOLD_NUGGET),
            "Currency icon."
        ).read(config));

        this.setDecimal(ConfigValue.create("Decimal",
            false,
            "Sets whether or not currency value can have decimals."
        ).read(config));

        this.setSynchronizable(ConfigValue.create("Synchronized",
            true,
            "Sets whether or not the currency will synchronize balance of online players from the database.",
            "This setting is useless for SQLite.",
            "You may want to disable this if you're using this currency on a single server only."
        ).read(config));

        this.setPermissionRequired(ConfigValue.create("Permission_Required",
            false,
            "Sets whether or not players must have '" + this.getPermission() + "' permission to use this currency."
        ).read(config));

        this.setTransferAllowed(ConfigValue.create("Transfer_Allowed",
            true,
            "Sets whether or not players can send this currency to other players."
        ).read(config));

        this.setMinTransferAmount(ConfigValue.create("Transfer_Min_Amount",
            1D,
            "Sets minimal amount for sending this currency to other players.",
            "Set this to '-1' for no limit."
        ).read(config));

        this.setStartValue(ConfigValue.create("Start_Value",
            0D,
            "How much of this currency new players will have on their balance?"
        ).read(config));

        this.setMaxValue(ConfigValue.create("Max_Value",
            -1D,
            "Max. possible value that players can have on their balance.",
            "Set this to '-1' to disable."
        ).read(config));

        this.setVaultEconomy(ConfigValue.create("Economy.Vault",
            false,
            "When enabled, uses the Vault API to register the currency as primary server Economy."
        ).read(config));

        this.setExchangeAllowed(ConfigValue.create("Exchange.Allowed",
            true,
            "Sets whether or not this currency can be exchanged for other ones."
        ).read(config));

        this.exchangeRates = ConfigValue.forMap("Exchange.Rates",
            (cfg2, path, id) -> cfg2.getDouble(path + "." + id),
            (cfg2, path, map) -> map.forEach((id, amount) -> cfg2.set(path + "." + id, amount)),
            () -> Map.of("other", 5D),
            "Sets exchange rates for this currency for other ones.",
            "1 of this currency = X of other currency.",
            "Exchange.Rates:",
            "  other: 5",
            "  another: 10"
        ).read(config);

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Name", this.getName());
        config.set("Symbol", this.getSymbol());
        config.set("Format", this.getFormat());
        config.set("Format_Short", this.getFormatShort());
        config.set("Column_Name", this.getColumnName());
        config.set("Command_Aliases", String.join(",", Arrays.asList(this.getCommandAliases())));
        config.setItem("Icon", this.getIcon());
        config.set("Decimal", this.isDecimal());
        config.set("Synchronized", this.isSynchronizable());
        config.set("Permission_Required", this.isPermissionRequired());
        config.set("Transfer_Allowed", this.isTransferAllowed());
        config.set("Transfer_Min_Amount", this.getMinTransferAmount());
        config.set("Start_Value", this.getStartValue());
        config.set("Max_Value", this.getMaxValue());
        config.set("Economy.Vault", this.isVaultEconomy());
        config.set("Exchange.Allowed", this.isExchangeAllowed());
        config.remove("Exchange.Rates");
        this.getExchangeRates().forEach((id, rate) -> {
            config.set("Exchange.Rates." + id, rate);
        });
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public void setSymbol(@NotNull String symbol) {
        this.symbol = symbol;
    }

    @NotNull
    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(@NotNull String format) {
        this.format = format;
    }

    @NotNull
    @Override
    public String getFormatShort() {
        return this.formatShort;
    }

    @Override
    public void setFormatShort(@NotNull String formatShort) {
        this.formatShort = formatShort;
    }

    @NotNull
    @Override
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(@NotNull String columnName) {
        this.columnName = columnName;
        this.column = SQLColumn.of(this.columnName, ColumnType.DOUBLE);
    }

    @NotNull
    @Override
    public String[] getCommandAliases() {
        return commandAliases;
    }

    @Override
    public void setCommandAliases(String[] commandAliases) {
        this.commandAliases = commandAliases;
    }

    @NotNull
    @Override
    public SQLColumn getColumn() {
        return column;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(icon);
    }

    @Override
    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
    }

    @Override
    public boolean isDecimal() {
        return decimal;
    }

    @Override
    public void setDecimal(boolean decimal) {
        this.decimal = decimal;
    }

    @Override
    public boolean isSynchronizable() {
        return synchronizable;
    }

    @Override
    public void setSynchronizable(boolean synchronizable) {
        this.synchronizable = synchronizable;
    }

    @Override
    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    @Override
    public void setPermissionRequired(boolean permissionRequired) {
        this.permissionRequired = permissionRequired;
    }

    @Override
    public boolean isTransferAllowed() {
        return transferAllowed;
    }

    @Override
    public void setTransferAllowed(boolean transferAllowed) {
        this.transferAllowed = transferAllowed;
    }

    public double getMinTransferAmount() {
        return minTransferAmount;
    }

    @Override
    public void setMinTransferAmount(double minTransferAmount) {
        this.minTransferAmount = minTransferAmount;
    }

    @Override
    public double getStartValue() {
        return startValue;
    }

    @Override
    public void setStartValue(double startValue) {
        this.startValue = startValue;
    }

    @Override
    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public boolean isVaultEconomy() {
        return vaultEconomy;
    }

    @Override
    public void setVaultEconomy(boolean vaultEconomy) {
        this.vaultEconomy = vaultEconomy;
    }

    public boolean isExchangeAllowed() {
        return exchangeAllowed;
    }

    @Override
    public void setExchangeAllowed(boolean exchangeAllowed) {
        this.exchangeAllowed = exchangeAllowed;
    }

    @NotNull
    public Map<String, Double> getExchangeRates() {
        return exchangeRates;
    }
}
