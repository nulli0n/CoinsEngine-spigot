package su.nightexpress.coinsengine.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.tops.TopEntry;
import su.nightexpress.coinsengine.tops.TopManager;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.text.night.NightMessage;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private interface TopPlaceholder {

        @NotNull String produce(@NotNull TopEntry entry, @NotNull Currency currency, int position);
    }

    private interface PlayerPlaceholder {

        @NotNull String produce(@NotNull Player player, @NotNull CoinsUser user, @NotNull Currency currency);
    }

    private static class Expansion extends PlaceholderExpansion {

        private static final DecimalFormat RAW_FORMAT = new DecimalFormat("#");

        static {
            RAW_FORMAT.setMaximumFractionDigits(8);
        }

        private final CoinsEnginePlugin              plugin;
        private final Map<String, PlayerPlaceholder> playerPlaceholders;
        private final Map<String, TopPlaceholder>    topPlaceholders;

        public Expansion(@NotNull CoinsEnginePlugin plugin) {
            this.plugin = plugin;
            this.playerPlaceholders = new LinkedHashMap<>();
            this.topPlaceholders = new LinkedHashMap<>();

            if (Config.isTopsEnabled()) {
                this.loadTopPlaceholders();
            }
            this.loadPlayerPlaceholders();
        }

        @Override
        @NotNull
        public String getAuthor() {
            return this.plugin.getDescription().getAuthors().getFirst();
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return LowerCase.INTERNAL.apply(this.plugin.getDescription().getName());
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

        private void loadTopPlaceholders() {
            this.topPlaceholders.put("balance_short_clean", (entry, currency, position) -> NightMessage.stripTags(currency.formatCompact(entry.getBalance())));
            this.topPlaceholders.put("balance_short_legacy", (entry, currency, position) -> NightMessage.asLegacy(currency.formatCompact(entry.getBalance())));
            this.topPlaceholders.put("balance_short", (entry, currency, position) -> currency.formatCompact(entry.getBalance()));

            this.topPlaceholders.put("balance_clean", (entry, currency, position) -> NightMessage.stripTags(currency.formatCompact(entry.getBalance())));
            this.topPlaceholders.put("balance_legacy", (entry, currency, position) -> NightMessage.asLegacy(currency.format(entry.getBalance())));
            this.topPlaceholders.put("balance", (entry, currency, position) -> currency.format(entry.getBalance()));

            this.topPlaceholders.put("player", (entry, currency, position) -> entry.getName());

            this.playerPlaceholders.put("leaderboard_position", (player, user, currency) -> {
                return this.plugin.getTopManager().map(topManager -> topManager.getTopEntry(currency, player.getName())).map(TopEntry::getPosition).map(String::valueOf).orElse("?");
            });
        }

        private void loadPlayerPlaceholders() {
            this.playerPlaceholders.put("server_balance_short_clean", (player, user, currency) -> {
                return NightMessage.stripTags(currency.formatCompact(plugin.getTopManager().orElseThrow().getTotalBalance(currency)));
            });

            this.playerPlaceholders.put("server_balance_short_legacy", (player, user, currency) -> {
                return NightMessage.asLegacy(currency.formatCompact(plugin.getTopManager().orElseThrow().getTotalBalance(currency)));
            });

            this.playerPlaceholders.put("server_balance_short", (player, user, currency) -> {
                return currency.formatCompact(plugin.getTopManager().orElseThrow().getTotalBalance(currency));
            });

            this.playerPlaceholders.put("server_balance_clean", (player, user, currency) -> {
                return NightMessage.stripTags(currency.format(plugin.getTopManager().orElseThrow().getTotalBalance(currency)));
            });

            this.playerPlaceholders.put("server_balance_legacy", (player, user, currency) -> {
                return NightMessage.asLegacy(currency.format(plugin.getTopManager().orElseThrow().getTotalBalance(currency)));
            });

            this.playerPlaceholders.put("server_balance_raw", (player, user, currency) -> {
                return RAW_FORMAT.format(plugin.getTopManager().orElseThrow().getTotalBalance(currency));
            });

            this.playerPlaceholders.put("server_balance", (player, user, currency) -> {
                return currency.format(plugin.getTopManager().orElseThrow().getTotalBalance(currency));
            });

            this.playerPlaceholders.put("payments_state", (player, user, currency) -> NightMessage.asLegacy(CoreLang.STATE_ENABLED_DISALBED.get(user.getSettings(currency).isPaymentsEnabled())));

            this.playerPlaceholders.put("balance_short_clean", (player, user, currency) -> NightMessage.stripTags(currency.formatCompact(user.getBalance(currency))));
            this.playerPlaceholders.put("balance_short_legacy", (player, user, currency) -> NightMessage.asLegacy(currency.formatCompact(user.getBalance(currency))));
            this.playerPlaceholders.put("balance_short", (player, user, currency) -> currency.formatCompact(user.getBalance(currency)));

            this.playerPlaceholders.put("balance_clean", (player, user, currency) -> NightMessage.stripTags(currency.format(user.getBalance(currency))));
            this.playerPlaceholders.put("balance_legacy", (player, user, currency) -> NightMessage.asLegacy(currency.format(user.getBalance(currency))));
            this.playerPlaceholders.put("balance_raw", (player, user, currency) -> NightMessage.stripTags(RAW_FORMAT.format(currency.floorIfNeeded(user.getBalance(currency)))));
            this.playerPlaceholders.put("balance", (player, user, currency) -> currency.format(user.getBalance(currency)));
        }

        @Override
        public String onPlaceholderRequest(Player player, @NotNull String params) {
            TopManager topManager = this.plugin.getTopManager().orElse(null);

            if (params.startsWith("top_") && topManager != null) {
                String type = params.substring("top_".length());

                for (var entry : this.topPlaceholders.entrySet()) {
                    String key = entry.getKey() + "_";
                    if (!type.startsWith(key)) continue;

                    String posAndCurrency = type.substring(key.length());
                    int index = posAndCurrency.indexOf('_');
                    if (index < 0) break;

                    String posRaw = posAndCurrency.substring(0, index);
                    String currencyId = posAndCurrency.substring(index + 1);

                    Currency currency = plugin.getCurrencyRegistry().getById(currencyId);
                    if (currency == null) break;

                    int position = NumberUtil.getIntegerAbs(posRaw);
                    if (position <= 0) return null;

                    List<TopEntry> baltop = topManager.getTopEntries(currency);
                    if (position > baltop.size()) return Lang.OTHER_NO_TOP_ENTRY.text();

                    TopEntry topEntry = baltop.get(position - 1);

                    return entry.getValue().produce(topEntry, currency, position);
                }

                return null;
            }

            if (player != null) {
                CoinsUser user = plugin.getUserManager().getOrFetch(player);

                for (var entry : this.playerPlaceholders.entrySet()) {
                    String key = entry.getKey() + "_";
                    if (!params.startsWith(key)) continue;

                    String currencyId = params.substring(key.length());
                    Currency currency = plugin.getCurrencyRegistry().getById(currencyId);
                    if (currency == null) continue;

                    return entry.getValue().produce(player, user, currency);
                }
            }

            return null;
        }
    }
}
