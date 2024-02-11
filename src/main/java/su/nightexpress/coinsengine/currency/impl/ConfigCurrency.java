package su.nightexpress.coinsengine.currency.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigCurrency extends AbstractFileData<CoinsEngine> implements Currency {

    private String name;
    private String symbol;
    private String format;
    private String[] commandAliases;
    private ItemStack icon;
    private boolean decimal;
    private boolean permissionRequired;
    private boolean transferAllowed;
    private double minTransferAmount;
    private double startValue;
    private double maxValue;
    private boolean vaultEconomy;
    private boolean exchangeAllowed;
    private Map<String, Double> exchangeRates;

    private final PlaceholderMap placeholderMap;

    public ConfigCurrency(@NotNull CoinsEngine plugin, @NotNull File file) {
        super(plugin, file);
        this.exchangeRates = new HashMap<>();
        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.CURRENCY_ID, this::getId)
            .add(Placeholders.CURRENCY_NAME, this::getName)
            .add(Placeholders.CURRENCY_SYMBOL, this::getSymbol);
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig cfg) {
        this.setName(ConfigValue.create("Name", StringUtil.capitalizeUnderscored(this.getId()),
            "Localized currency name."
        ).read(cfg));

        this.setSymbol(ConfigValue.create("Symbol", this.getName(),
            "Currency symbol, like '$'."
        ).read(cfg));

        this.setFormat(ConfigValue.create("Format",
            Placeholders.GENERIC_AMOUNT + Placeholders.CURRENCY_SYMBOL,
            "Currency display format.",
            "Use '" + Placeholders.GENERIC_AMOUNT + "' placeholder for amount value.",
            "You can use 'Currency' placeholders: " + Placeholders.WIKI_PLACEHOLDERS
        ).read(cfg));

        this.setCommandAliases(ConfigValue.create("Command_Aliases",
            new String[]{this.getId()},
            "Custom currency commands. Split with commas."
        ).read(cfg));

        this.setIcon(ConfigValue.create("Icon", new ItemStack(Material.GOLD_NUGGET),
            "Currency icon."
        ).read(cfg));

        this.setDecimal(ConfigValue.create("Decimal", false,
            "Sets whether or not currency value can have decimals."
        ).read(cfg));

        this.setPermissionRequired(ConfigValue.create("Permission_Required", false,
            "Sets whether or not players must have '" + this.getPermission() + "' permission to use this currency."
        ).read(cfg));

        this.setTransferAllowed(ConfigValue.create("Transfer_Allowed", true,
            "Sets whether or not players can send this currency to other players."
        ).read(cfg));

        this.setMinTransferAmount(ConfigValue.create("Transfer_Min_Amount", 1D,
            "Sets minimal amount for sending this currency to other players.",
            "Set this to '-1' for no limit."
        ).read(cfg));

        this.setStartValue(ConfigValue.create("Start_Value", 0D,
            "How much of this currency new players will have on their balance?"
        ).read(cfg));

        this.setMaxValue(ConfigValue.create("Max_Value", -1D,
            "Max. possible value that players can have on their balance.",
            "Set this to '-1' to disable."
        ).read(cfg));

        this.setVaultEconomy(ConfigValue.create("Economy.Vault", false,
            "When enabled, uses the Vault API to register the currency as primary server Economy."
        ).read(cfg));

        this.setExchangeAllowed(ConfigValue.create("Exchange.Allowed", true,
            "Sets whether or not this currency can be exchanged for other ones."
        ).read(cfg));

        this.exchangeRates = ConfigValue.forMap("Exchange.Rates",
            (cfg2, path, id) -> cfg2.getDouble(path + "." + id),
            (cfg2, path, map) -> map.forEach((id, amount) -> cfg2.set(path + "." + id, amount)),
            () -> Map.of("other", 5D),
            "Sets exchange rates for this currency for other ones.",
            "1 of this currency = X of other currency.",
            "Exchange.Rates:",
            "  other: 5",
            "  another: 10"
        ).read(cfg);

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig cfg) {
        cfg.set("Name", this.getName());
        cfg.set("Symbol", this.getSymbol());
        cfg.set("Format", this.getFormat());
        cfg.set("Command_Aliases", String.join(",", Arrays.asList(this.getCommandAliases())));
        cfg.setItem("Icon", this.getIcon());
        cfg.set("Decimal", this.isDecimal());
        cfg.set("Permission_Required", this.isPermissionRequired());
        cfg.set("Transfer_Allowed", this.isTransferAllowed());
        cfg.set("Transfer_Min_Amount", this.getMinTransferAmount());
        cfg.set("Start_Value", this.getStartValue());
        cfg.set("Max_Value", this.getMaxValue());
        cfg.set("Economy.Vault", this.isVaultEconomy());
        cfg.set("Exchange.Allowed", this.isExchangeAllowed());
        cfg.remove("Exchange.Rates");
        this.getExchangeRates().forEach((id, rate) -> {
            cfg.set("Exchange.Rates." + id, rate);
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
        this.name = Colorizer.apply(name);
    }

    @NotNull
    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public void setSymbol(@NotNull String symbol) {
        this.symbol = Colorizer.apply(symbol);
    }

    @NotNull
    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(@NotNull String format) {
        this.format = Colorizer.apply(format);
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
