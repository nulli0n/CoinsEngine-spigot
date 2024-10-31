package su.nightexpress.coinsengine.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.util.TopEntry;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PlaceholderAPIHook {

    private static Expansion expansion;

    public static void setup(@NotNull CoinsEnginePlugin plugin) {
        if (expansion == null) {
            expansion = new Expansion(plugin);
            expansion.register();
        }
    }

    public static void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    private static class Expansion extends PlaceholderExpansion {

        private static final DecimalFormat RAW_FORMAT = new DecimalFormat("#");

        static {
            RAW_FORMAT.setMaximumFractionDigits(8);
        }

        private final CoinsEnginePlugin plugin;
        private final Map<String, BiFunction<Player, Currency, String>> placeholders;

        public Expansion(@NotNull CoinsEnginePlugin plugin) {
            this.plugin = plugin;
            this.placeholders = new LinkedHashMap<>();

            this.placeholders.put("server_balance_raw", (player, currency) -> {
                return RAW_FORMAT.format(plugin.getCurrencyManager().getTotalBalance(currency));
            });

            this.placeholders.put("server_balance_short_plain", (player, currency) -> {
                return currency.formatCompact(plugin.getCurrencyManager().getTotalBalance(currency));
            });

            this.placeholders.put("server_balance_short", (player, currency) -> {
                return NightMessage.asLegacy(currency.formatCompact(plugin.getCurrencyManager().getTotalBalance(currency)));
            });

            this.placeholders.put("server_balance_plain", (player, currency) -> {
                return currency.format(plugin.getCurrencyManager().getTotalBalance(currency));
            });

            this.placeholders.put("server_balance", (player, currency) -> {
                return NightMessage.asLegacy(currency.format(plugin.getCurrencyManager().getTotalBalance(currency)));
            });



            this.placeholders.put("payments_state", (player, currency) -> {
                return handleUserCurrency(player, user -> {
                    return NightMessage.asLegacy(Lang.getEnabledOrDisabled(user.getSettings(currency).isPaymentsEnabled()));
                });
            });

            this.placeholders.put("balance_raw", (player, currency) -> {
                return handleUserCurrency(player, user -> {
                    return RAW_FORMAT.format(currency.fine(user.getBalance(currency)));
                });
            });

            this.placeholders.put("balance_rounded", (player, currency) -> {
                return handleUserCurrency(player, user -> {
                    return NumberUtil.format(currency.fine(user.getBalance(currency)));
                });
            });

            this.placeholders.put("balance_short_plain", (player, currency) -> {
                return handleUserCurrency(player, user -> {
                    return currency.formatCompact(user.getBalance(currency));
                });
            });

            this.placeholders.put("balance_short", (player, currency) -> {
                return handleUserCurrency(player, user -> {
                    return NightMessage.asLegacy(currency.formatCompact(user.getBalance(currency)));
                });
            });

            this.placeholders.put("balance_plain", (player, currency) -> {
                return handleUserCurrency(player, user -> {
                    return currency.format(user.getBalance(currency));
                });
            });

            this.placeholders.put("balance", (player, currency) -> {
                return handleUserCurrency(player, user -> {
                    return NightMessage.asLegacy(currency.format(user.getBalance(currency)));
                });
            });
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
        public String onPlaceholderRequest(Player player, @NotNull String params) {
            // top_balance_coins_1
            // top_player_coins_1
            if (params.startsWith("top_")) {
                String cut = params.substring("top_".length()); // balance_coins_1
                String[] split = cut.split("_");
                if (split.length < 3) return null;

                String type = split[0];
                String currencyId = split[1];
                Currency currency = plugin.getCurrencyManager().getCurrency(currencyId);
                if (currency == null) return null;

                int pos = NumberUtil.getIntegerAbs(split[2]);
                if (pos <= 0) return null;

                List<TopEntry> baltop = plugin.getCurrencyManager().getTopBalances(currency);
                if (pos > baltop.size()) return "-";

                TopEntry entry = baltop.get(pos - 1);
                if (type.equalsIgnoreCase("balance-short")) return NightMessage.asLegacy(currency.formatCompact(entry.balance()));
                if (type.equalsIgnoreCase("balance-short-plain")) return currency.formatCompact(entry.balance());
                if (type.equalsIgnoreCase("balance-plain")) return currency.format(entry.balance());
                if (type.equalsIgnoreCase("balance")) return NightMessage.asLegacy(currency.format(entry.balance()));
                if (type.equalsIgnoreCase("player")) return entry.name();

                return null;
            }

            if (player == null) return "";

            for (var entry : this.placeholders.entrySet()) {
                String prefix = entry.getKey() + "_";
                if (!params.startsWith(prefix)) continue;

                String currencyId = params.substring(prefix.length());
                Currency currency = plugin.getCurrencyManager().getCurrency(currencyId);
                if (currency == null) continue;

                return entry.getValue().apply(player, currency);
            }

            return null;
        }

        @Nullable
        private String handleUserCurrency(@NotNull Player player, @NotNull Function<CoinsUser, String> function) {
            CoinsUser user = plugin.getUserManager().getOrFetch(player);
            return function.apply(user);
        }
    }
}
