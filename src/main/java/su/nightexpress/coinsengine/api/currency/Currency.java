package su.nightexpress.coinsengine.api.currency;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.number.CompactNumber;
import su.nightexpress.nightcore.util.placeholder.Replacer;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public interface Currency {

    void onRegister();

    void onUnregister();

    @NotNull UnaryOperator<String> replacePlaceholders();

    void sendPrefixed(@NotNull MessageLocale locale, @NotNull CommandSender sender);

    void sendPrefixed(@NotNull MessageLocale locale, @NotNull CommandSender sender, @Nullable Consumer<Replacer> consumer);

    boolean hasPermission(@NotNull Player player);

    boolean isPrimary();

    boolean isUnlimited();

    boolean isLimited();

    boolean isInteger();

    boolean isUnderLimit(double value);

    @Deprecated
    default double fine(double amount) {
        return this.floorIfNeeded(amount);
    }

    double floorIfNeeded(double amount);

    @Deprecated
    default double limit(double amount) {
        return this.limitIfNeeded(amount);
    }

    double limitIfNeeded(double amount);

    @Deprecated
    default double fineAndLimit(double amount) {
        return this.floorAndLimit(amount);
    }

    double floorAndLimit(double amount);

    @NotNull String getPermission();

    @NotNull String formatValue(double balance);

    @NotNull String format(double balance);

    @NotNull
    @Deprecated
    default CompactNumber formatCompactValue(double balance) {
        return this.compacted(balance);
    }

    @NotNull CompactNumber compacted(double balance);

    @NotNull String formatCompact(double balance);

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

    void setColumnName(@NotNull String dataColumn);

    @Deprecated
    @NotNull ItemStack getIcon();

    @Deprecated
    void setIcon(@NotNull ItemStack icon);

    @NotNull NightItem icon();

    void setIcon(@NotNull NightItem icon);

    boolean isDecimal();

    void setDecimal(boolean decimal);

    boolean isPermissionRequired();

    void setPermissionRequired(boolean permissionRequired);

    boolean isSynchronizable();

    void setSynchronizable(boolean dataSync);

    boolean isTransferAllowed();

    void setTransferAllowed(boolean transferAllowed);

    double getMinTransferAmount();

    void setMinTransferAmount(double minTransferAmount);

    double getStartValue();

    void setStartValue(double startValue);

    double getMaxValue();

    void setMaxValue(double maxValue);

    @Deprecated
    boolean isVaultEconomy();

    boolean isExchangeAllowed();

    void setExchangeAllowed(boolean exchangeAllowed);

    @NotNull Map<String, Double> getExchangeRates();

    double getExchangeRate(@NotNull Currency currency);

    double getExchangeRate(@NotNull String id);

    boolean canExchangeTo(@NotNull Currency other);

    double getExchangeResult(@NotNull Currency other, double amount);

    boolean isLeaderboardEnabled();
}
