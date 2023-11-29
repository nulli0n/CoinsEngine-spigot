package su.nightexpress.coinsengine.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.Pair;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PlaceholderAPIHook {

    private static PointsExpansion expansion;

    public static void setup(@NotNull CoinsEngine plugin) {
        if (expansion == null) {
            expansion = new PointsExpansion(plugin);
            expansion.register();
        }
    }

    public static void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    private static class PointsExpansion extends PlaceholderExpansion {

        private final CoinsEngine plugin;

        public PointsExpansion(@NotNull CoinsEngine plugin) {
            this.plugin = plugin;
        }

        @Override
        @NotNull
        public String getAuthor() {
            return this.plugin.getDescription().getAuthors().get(0);
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return this.plugin.getDescription().getName().toLowerCase();
        }

        @Override
        @NotNull
        public String getVersion() {
            return this.plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(Player player, String holder) {
            if (holder.startsWith("server_balance_raw_")) {
                String curId = holder.substring("server_balance_raw_".length());
                Currency currency = plugin.getCurrencyManager().getCurrency(curId);
                if (currency == null) return null;

                DecimalFormat format = new DecimalFormat("#");
                format.setMaximumFractionDigits(8);

                return format.format(plugin.getCurrencyManager().getBalanceMap().getOrDefault(currency, Collections.emptyList())
                    .stream().mapToDouble(Pair::getSecond).sum());
            }
            if (holder.startsWith("server_balance_")) {
                String curId = holder.substring("server_balance_".length());
                Currency currency = plugin.getCurrencyManager().getCurrency(curId);
                if (currency == null) return null;

                return currency.format(plugin.getCurrencyManager().getBalanceMap().getOrDefault(currency, Collections.emptyList())
                    .stream().mapToDouble(Pair::getSecond).sum());
            }

            // top_balance_coins_1
            // top_player_coins_1
            if (holder.startsWith("top_")) {
                String cut = holder.substring("top_".length()); // balance_coins_1
                String[] split = cut.split("_");
                if (split.length < 3) return null;

                String type = split[0];
                String currencyId = split[1];
                Currency currency = plugin.getCurrencyManager().getCurrency(currencyId);
                if (currency == null) return null;

                int pos = StringUtil.getInteger(split[2], 0);
                if (pos <= 0) return null;

                List<Pair<String, Double>> baltop = plugin.getCurrencyManager().getBalanceList(currency);
                if (pos > baltop.size()) return "-";

                Pair<String, Double> pair = baltop.get(pos - 1);
                if (type.equalsIgnoreCase("balance")) return currency.format(pair.getSecond());
                if (type.equalsIgnoreCase("player")) return pair.getFirst();

                return null;
            }

            if (player == null) return null;

            CoinsUser user = plugin.getUserManager().getUserData(player);

            if (holder.startsWith("balance_raw_")) {
                String currencyId = holder.substring("balance_raw_".length()); // coins
                Currency currency = plugin.getCurrencyManager().getCurrency(currencyId);
                if (currency == null) return null;

                DecimalFormat format = new DecimalFormat("#");
                format.setMaximumFractionDigits(8);

                return format.format(currency.fine(user.getCurrencyData(currency).getBalance()));
            }
            if (holder.startsWith("balance_rounded_")) {
                String currencyId = holder.substring("balance_rounded_".length()); // coins
                Currency currency = plugin.getCurrencyManager().getCurrency(currencyId);
                if (currency == null) return null;

                return NumberUtil.format(currency.fine(user.getCurrencyData(currency).getBalance()));
            }
            if (holder.startsWith("balance_short_")) {
                String currencyId = holder.substring("balance_short_".length()); // coins
                Currency currency = plugin.getCurrencyManager().getCurrency(currencyId);
                if (currency == null) return null;

                double balance = currency.fine(user.getCurrencyData(currency).getBalance());

                NumberFormat fmt = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
                fmt.setMinimumFractionDigits(1);
                fmt.setMaximumFractionDigits(2);
                return fmt.format(balance);
            }
            // balance_coins
            if (holder.startsWith("balance_")) {
                String currencyId = holder.substring("balance_".length()); // coins
                Currency currency = plugin.getCurrencyManager().getCurrency(currencyId);
                if (currency == null) return null;

                return currency.format(user.getCurrencyData(currency).getBalance());
            }

            return null;
        }
    }
}
