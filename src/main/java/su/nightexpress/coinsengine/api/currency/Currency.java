package su.nightexpress.coinsengine.api.currency;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.nightcore.db.sql.column.Column;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.number.CompactNumber;

import java.util.Map;
import java.util.function.UnaryOperator;

public interface Currency {

    @NotNull
    default UnaryOperator<String> replacePlaceholders() {
        return Placeholders.forCurrency(this);
    }

    @NotNull
    default LangMessage withPrefix(@NotNull LangMessage message) {
        if (!Config.CURRENCY_PREFIX_ENABLED.get()) return message;

        String prefix = this.replacePlaceholders().apply(Config.CURRENCY_PREFIX_FORMAT.get());
        return message.setPrefix(prefix);
    }

    default boolean isUnlimited() {
        return this.getMaxValue() <= 0D;
    }

    default boolean isLimited() {
        return !this.isUnlimited();
    }

    default boolean isInteger() {
        return !this.isDecimal();
    }

    default boolean isUnderLimit(double value) {
        return this.isUnlimited() || value <= this.getMaxValue();
    }

    default double fine(double amount) {
        return this.isDecimal() ? amount : Math.floor(amount);
    }

    default double limit(double amount) {
        return this.isLimited() ? Math.min(amount, this.getMaxValue()) : amount;
    }

    default double fineAndLimit(double amount) {
        return this.fine(this.limit(amount));
    }

    default double getExchangeRate(@NotNull Currency currency) {
        return this.getExchangeRate(currency.getId());
    }

    default double getExchangeRate(@NotNull String id) {
        return this.getExchangeRates().getOrDefault(id.toLowerCase(), 0.0);
    }

    @NotNull
    default String getPermission() {
        return Perms.PREFIX_CURRENCY + this.getId();
    }

    @NotNull
    default String formatValue(double balance) {
        return NumberUtil.format(this.fine(balance));
    }

    @NotNull
    default String format(double balance) {
        String format = this.getFormat();
        if (Config.useCurrencyFormatPAPI()) {
            format = PlaceholderAPI.setPlaceholders(null, format);
        }

        return this.replacePlaceholders().apply(format).replace(Placeholders.GENERIC_AMOUNT, this.formatValue(balance));
    }

    @NotNull
    default CompactNumber formatCompactValue(double balance) {
        return NumberUtil.asCompact(this.fine(balance));
    }

    @NotNull
    default String formatCompact(double balance) {
        String format = this.getFormatShort();
        if (Config.useCurrencyFormatPAPI()) {
            format = PlaceholderAPI.setPlaceholders(null, format);
        }

        CompactNumber compact = this.formatCompactValue(balance);

        return this.replacePlaceholders().apply(format).replace(Placeholders.GENERIC_AMOUNT, compact.format());
    }

    @NotNull String getId();

    @NotNull String getName();

    void setName(@NotNull String name);

    @NotNull String getPrefix();

    void setPrefix(@NotNull String prefix);

    @NotNull String getSymbol();

    void setSymbol(@NotNull String symbol);

    @NotNull String getFormat();

    void setFormat(@NotNull String format);

    @NotNull String getFormatShort();

    void setFormatShort(@NotNull String formatShort);

    @NotNull String[] getCommandAliases();

    void setCommandAliases(String... commandAliases);

    @NotNull String getColumnName();

    void setColumnName(@NotNull String columnName);

    @NotNull Column getColumn();

    @NotNull ItemStack getIcon();

    void setIcon(@NotNull ItemStack icon);

    boolean isDecimal();

    void setDecimal(boolean decimal);

    boolean isPermissionRequired();

    void setPermissionRequired(boolean permissionRequired);

    boolean isSynchronizable();

    void setSynchronizable(boolean synchronizable);

    boolean isTransferAllowed();

    void setTransferAllowed(boolean transferAllowed);

    double getMinTransferAmount();

    void setMinTransferAmount(double minTransferAmount);

    double getStartValue();

    void setStartValue(double startValue);

    double getMaxValue();

    void setMaxValue(double maxValue);

    boolean isVaultEconomy();

    void setVaultEconomy(boolean vaultEconomy);

    boolean isExchangeAllowed();

    void setExchangeAllowed(boolean exchangeAllowed);

    @NotNull Map<String, Double> getExchangeRates();
}

