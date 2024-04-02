package su.nightexpress.coinsengine.api.currency;

import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Pair;
import su.nightexpress.nightcore.util.placeholder.Placeholder;

public interface Currency extends Placeholder {

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
        return Math.max(0D, this.isDecimal() ? amount : Math.floor(amount));
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
        return this.replacePlaceholders()
                .apply(this.getFormat()).replace(Placeholders.GENERIC_AMOUNT, this.formatValue(balance));
    }

    @NotNull
    default Pair<String, String> formatCompactValue(double balance) {
        return NumberUtil.formatCompact(this.fine(balance));
    }

    @NotNull
    default String formatCompact(double balance) {
        Pair<String, String> compact = this.formatCompactValue(balance);
        return this.replacePlaceholders().apply(this.getFormatShort())
                .replace(Placeholders.GENERIC_AMOUNT, compact.getFirst())
                .replace(Placeholders.CURRENCY_SHORT_SYMBOL, compact.getSecond());
    }

    @NotNull String getId();

    @NotNull String getName();

    void setName(@NotNull String name);

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

    @NotNull SQLColumn getColumn();

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

