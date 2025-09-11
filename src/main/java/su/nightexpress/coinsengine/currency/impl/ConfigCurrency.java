package su.nightexpress.coinsengine.currency.impl;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.number.CompactNumber;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ConfigCurrency extends AbstractFileData<CoinsEnginePlugin> implements Currency {

    private String    name;
    private String    symbol;
    private String    prefix;
    private String[]  commandAliases;
    private String    format;
    private String    formatShort;
    private NightItem icon;

    private String  dataColumn;
    private boolean dataSync;

    private boolean permissionRequired;
    private boolean decimal;
    private double  startValue;
    private double  maxValue;

    private boolean transferAllowed;
    private double  minTransferAmount;

    private boolean vaultEconomy;

    private boolean exchangeAllowed;
    private boolean leaderboardEnabled;

    private final Map<String, Double> exchangeRates;

    public ConfigCurrency(@NotNull CoinsEnginePlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.exchangeRates = new HashMap<>();
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setName(ConfigValue.create("Name", StringUtil.capitalizeUnderscored(this.getId()),
            "Currency display name.",
            Placeholders.URL_WIKI_TEXT
        ).read(config));

        this.setSymbol(ConfigValue.create("Symbol", this.name,
            "Currency symbol.",
            Placeholders.URL_WIKI_TEXT
        ).read(config));

        this.setPrefix(ConfigValue.create("Prefix", this.name,
            "Currency prefix.",
            Placeholders.URL_WIKI_TEXT,
            Placeholders.WIKI_PREFIXES
        ).read(config));

        this.setCommandAliases(ConfigValue.create("Command_Aliases",
            new String[]{this.getId()},
            "Currency command aliases. Split with comma.",
            "[*] Server reboot is required for the changes to apply.",
            Placeholders.WIKI_COMMANDS
        ).read(config));

        this.setIcon(ConfigValue.create("Icon",
            NightItem.fromType(Material.GOLD_NUGGET),
            "Currency icon.",
            Placeholders.URL_WIKI_ITEMS
        ).read(config));

        this.setFormat(ConfigValue.create("Format",
            Placeholders.GENERIC_AMOUNT + Placeholders.CURRENCY_SYMBOL,
            "Currency display format.",
            "Placeholders:",
            "- " + Placeholders.GENERIC_AMOUNT + " - Amount value.",
            "- Currency placeholders: " + Placeholders.WIKI_PLACEHOLDERS,
            "- " + Plugins.PLACEHOLDER_API + " placeholders that are NOT bound to a player (e.g. Oraxen or ItemsAdder %img% placeholders)",
            Placeholders.URL_WIKI_TEXT
        ).read(config));

        this.setFormatShort(ConfigValue.create("Format_Short",
            Placeholders.CURRENCY_SYMBOL + Placeholders.GENERIC_AMOUNT,
            "Currency short display format.",
            "- " + Placeholders.GENERIC_AMOUNT + " - Amount value.",
            "- Currency placeholders: " + Placeholders.WIKI_PLACEHOLDERS,
            "- " + Plugins.PLACEHOLDER_API + " placeholders that are NOT bound to a player (e.g. Oraxen or ItemsAdder %img% placeholders)",
            Placeholders.URL_WIKI_TEXT
        ).read(config));

        this.setColumnName(ConfigValue.create("Column_Name",
            this.getId(),
            "Database column name where this currency will be saved.",
            Placeholders.WIKI_CROSS_SERVER
        ).read(config));

        this.setSynchronizable(ConfigValue.create("Synchronized",
            true,
            "Controls whether currency is included in data synchronization.",
            Placeholders.WIKI_CROSS_SERVER
        ).read(config));

        this.setDecimal(ConfigValue.create("Decimal",
            false,
            "Controls whether decimal values are allowed for this currency."
        ).read(config));

        this.setPermissionRequired(ConfigValue.create("Permission_Required",
            false,
            "Controls whether permission is required for this currency.",
            Placeholders.WIKI_PERMISSIONS
        ).read(config));

        this.setTransferAllowed(ConfigValue.create("Transfer_Allowed",
            true,
            "Controls whether players can send this currency to other players."
        ).read(config));

        this.setMinTransferAmount(ConfigValue.create("Transfer_Min_Amount",
            1D,
            "Min. amount to send this currency to other players.",
            "[*] Set to '-1' for no limit."
        ).read(config));

        this.setStartValue(ConfigValue.create("Start_Value",
            0D,
            "Start currency value for new players."
        ).read(config));

        this.setMaxValue(ConfigValue.create("Max_Value",
            -1D,
            "Max. possible value that players can have on their balance.",
            "[*] Set to '-1' to disable."
        ).read(config));

        this.setVaultEconomy(ConfigValue.create("Economy.Vault",
            false,
            "Controls whether currency will hook into " + Plugins.VAULT + " API to be registered as primary server economy provider.",
            Placeholders.WIKI_VAULT_HOOK
        ).read(config));

        this.setExchangeAllowed(ConfigValue.create("Exchange.Allowed",
            true,
            "Controls whether this currency can be exchanged for other ones.",
            Placeholders.WIKI_EXCHANGE
        ).read(config));

        if (config.getSection("Exchange.Rates").isEmpty()) {
            config.set("Exchange.Rates.mystery_coins", 5);
            config.set("Exchange.Rates.magic_coins", 10);
        }

        config.getSection("Exchange.Rates").forEach(sId -> {
            double rate = config.getDouble("Exchange.Rates." + sId);
            this.exchangeRates.put(sId.toLowerCase(), rate);
        });

        this.leaderboardEnabled = ConfigValue.create("Leaderboard.Enabled",
            true,
            "Controls whether this currency can have a leaderboard.",
            "[*] Requires the Tops module to be enabled.",
            Placeholders.WIKI_TOPS
        ).read(config);

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Name", this.name);
        config.set("Symbol", this.symbol);
        config.set("Prefix", this.prefix);
        config.set("Command_Aliases", String.join(",", Arrays.asList(this.commandAliases)));
        config.set("Format", this.format);
        config.set("Format_Short", this.formatShort);
        config.set("Icon", this.icon);

        config.set("Column_Name", this.dataColumn);
        config.set("Synchronized", this.dataSync);

        config.set("Permission_Required", this.permissionRequired);
        config.set("Decimal", this.decimal);
        config.set("Start_Value", this.startValue);
        config.set("Max_Value", this.maxValue);

        config.set("Transfer_Allowed", this.transferAllowed);
        config.set("Transfer_Min_Amount", this.minTransferAmount);

        config.set("Economy.Vault", this.vaultEconomy);

        config.set("Exchange.Allowed", this.exchangeAllowed);
        config.remove("Exchange.Rates");
        this.exchangeRates.forEach((id, rate) -> {
            config.set("Exchange.Rates." + id, rate);
        });

        config.set("Leaderboard.Enabled", this.leaderboardEnabled);
    }

    @NotNull
    @Override
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.forCurrency(this);
    }

    @Override
    public void sendPrefixed(@NotNull LangText text, @NotNull CommandSender sender) {
        this.sendPrefixed(text, sender, null);
    }

    @Override
    public void sendPrefixed(@NotNull LangText text, @NotNull CommandSender sender, @Nullable Consumer<Replacer> consumer) {
        this.sendPrefixed(text.getMessage(), sender, consumer);
    }

    @Override
    public void sendPrefixed(@NotNull LangMessage message, @NotNull CommandSender sender) {
        this.sendPrefixed(message, sender, null);
    }

    @Override
    public void sendPrefixed(@NotNull LangMessage message, @NotNull CommandSender sender, @Nullable Consumer<Replacer> consumer) {
        if (Config.CURRENCY_PREFIX_ENABLED.get()) {
            String prefix = this.replacePlaceholders().apply(Config.CURRENCY_PREFIX_FORMAT.get());
            message = message.setPrefix(prefix);
        }

        message.send(sender, consumer);
    }

    @Override
    public boolean hasPermission(@NotNull Player player) {
        return !this.permissionRequired || (player.hasPermission(this.getPermission()) || player.hasPermission(Perms.CURRENCY));
    }

    @Override
    public boolean isUnlimited() {
        return this.maxValue <= 0D;
    }

    @Override
    public boolean isLimited() {
        return !this.isUnlimited();
    }

    @Override
    public boolean isInteger() {
        return !this.decimal;
    }

    @Override
    public boolean isUnderLimit(double value) {
        return this.isUnlimited() || value <= this.maxValue;
    }

    @Override
    public double floorIfNeeded(double amount) {
        return Math.max(0, this.decimal ? amount : Math.floor(amount));
    }

    @Override
    public double limitIfNeeded(double amount) {
        return this.isLimited() ? Math.min(amount, this.maxValue) : amount;
    }

    @Override
    public double floorAndLimit(double amount) {
        return this.floorIfNeeded(this.limitIfNeeded(amount));
    }

    @Override
    @NotNull
    public String getPermission() {
        return Perms.PREFIX_CURRENCY + this.getId();
    }

    @Override
    @NotNull
    public CompactNumber compacted(double balance) {
        return NumberUtil.asCompact(this.floorIfNeeded(balance));
    }

    @Override
    @NotNull
    public String formatValue(double balance) {
        return NumberUtil.format(this.floorIfNeeded(balance));
    }

    @Override
    @NotNull
    public String format(double balance) {
        return this.getFormatted(this.format, balance, this::formatValue);
    }

    @Override
    @NotNull
    public String formatCompact(double balance) {
        return this.getFormatted(this.formatShort, balance, value -> this.compacted(value).format());
    }

    @NotNull
    private String getFormatted(@NotNull String originalFormat, double balance, @NotNull Function<Double, String> valueFormatter) {
        if (Config.useCurrencyFormatPAPI()) {
            originalFormat = PlaceholderAPI.setPlaceholders(null, originalFormat);
        }

        return this.replacePlaceholders().apply(originalFormat).replace(Placeholders.GENERIC_AMOUNT, valueFormatter.apply(balance));
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
    }

    @NotNull
    @Override
    public String getSymbol() {
        return this.symbol;
    }

    @Override
    public void setSymbol(@NotNull String symbol) {
        this.symbol = symbol;
    }

    @NotNull
    @Override
    public String getFormat() {
        return this.format;
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
        this.formatShort = formatShort.replace("%currency_short_symbol%", "");
    }

    @NotNull
    @Override
    public String getColumnName() {
        return this.dataColumn;
    }

    public void setColumnName(@NotNull String dataColumn) {
        this.dataColumn = dataColumn;
    }

    @NotNull
    @Override
    public String[] getCommandAliases() {
        return this.commandAliases;
    }

    @Override
    public void setCommandAliases(String[] commandAliases) {
        this.commandAliases = commandAliases;
    }

    @NotNull
    @Deprecated
    public ItemStack getIcon() {
        return this.icon.getItemStack();
    }

    @Override
    @Deprecated
    public void setIcon(@NotNull ItemStack icon) {
        this.setIcon(NightItem.fromItemStack(icon));
    }

    @Override
    @NotNull
    public NightItem icon() {
        return this.icon.copy();
    }

    @Override
    public void setIcon(@NotNull NightItem icon) {
        this.icon = icon;
    }

    @Override
    public boolean isDecimal() {
        return this.decimal;
    }

    @Override
    public void setDecimal(boolean decimal) {
        this.decimal = decimal;
    }

    @Override
    public boolean isSynchronizable() {
        return this.dataSync;
    }

    @Override
    public void setSynchronizable(boolean dataSync) {
        this.dataSync = dataSync;
    }

    @Override
    public boolean isPermissionRequired() {
        return this.permissionRequired;
    }

    @Override
    public void setPermissionRequired(boolean permissionRequired) {
        this.permissionRequired = permissionRequired;
    }

    @Override
    public boolean isTransferAllowed() {
        return this.transferAllowed;
    }

    @Override
    public void setTransferAllowed(boolean transferAllowed) {
        this.transferAllowed = transferAllowed;
    }

    public double getMinTransferAmount() {
        return this.minTransferAmount;
    }

    @Override
    public void setMinTransferAmount(double minTransferAmount) {
        this.minTransferAmount = minTransferAmount;
    }

    @Override
    public double getStartValue() {
        return this.startValue;
    }

    @Override
    public void setStartValue(double startValue) {
        this.startValue = startValue;
    }

    @Override
    public double getMaxValue() {
        return this.maxValue;
    }

    @Override
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public boolean isVaultEconomy() {
        return this.vaultEconomy;
    }

    @Override
    public void setVaultEconomy(boolean vaultEconomy) {
        this.vaultEconomy = vaultEconomy;
    }

    @Override
    public boolean isExchangeAllowed() {
        return this.exchangeAllowed;
    }

    @Override
    public void setExchangeAllowed(boolean exchangeAllowed) {
        this.exchangeAllowed = exchangeAllowed;
    }

    @Override
    @NotNull
    public Map<String, Double> getExchangeRates() {
        return this.exchangeRates;
    }

    @Override
    public double getExchangeRate(@NotNull Currency currency) {
        return this.getExchangeRate(currency.getId());
    }

    @Override
    public double getExchangeRate(@NotNull String id) {
        return this.exchangeRates.getOrDefault(id.toLowerCase(), 0D);
    }

    @Override
    public double getExchangeResult(@NotNull Currency other, double amount) {
        double rate = this.getExchangeRate(other);
        return other.floorIfNeeded(amount * rate);
    }

    @Override
    public boolean canExchangeTo(@NotNull Currency other) {
        return this.exchangeRates.containsKey(other.getId());
    }

    @Override
    public boolean isLeaderboardEnabled() {
        return this.leaderboardEnabled;
    }
}
