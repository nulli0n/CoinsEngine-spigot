package su.nightexpress.coinsengine.tops;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.tops.menu.TopMenu;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Version;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TopManager extends AbstractManager<CoinsEnginePlugin> {

    private final Map<String, Map<String, TopEntry>> topEntries;

    private TopMenu topMenu;

    public TopManager(@NotNull CoinsEnginePlugin plugin) {
        super(plugin);
        this.topEntries = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        if (Config.TOPS_USE_GUI.get()) {
            if (Version.isAtLeast(Version.MC_1_21_4)) {
                this.topMenu = this.addMenu(new TopMenu(this.plugin, this), Config.DIR_MENU, "leaderboard.yml");
            } else {
                this.plugin.error("Couldn't enabling top menu because your server's Minecraft version is not supported.");
            }
        }

        this.addListener(new TopsListener(this.plugin, this));

        this.addAsyncTask(this::updateBalances, Config.TOPS_UPDATE_INTERVAL.get());
    }

    @Override
    protected void onShutdown() {
        this.topEntries.clear();
    }

    public void updateBalances() {
        this.topEntries.clear();

        List<CoinsUser> users = this.plugin.getDataHandler().getUsers();

        users.removeIf(user -> {
            Player player = user.getPlayer();
            if (player != null) {
                this.hideFromTops(player);
            }
            return user.isHiddenFromTops();
        });

        this.plugin.getCurrencyManager().getCurrencies().forEach(currency -> {
            AtomicInteger counter = new AtomicInteger(0);
            Map<String, TopEntry> entries = new LinkedHashMap<>();

            users.stream().sorted(Comparator.comparingDouble((CoinsUser user) -> user.getBalance(currency)).reversed()).forEach(user -> {
                entries.put(user.getName().toLowerCase(), new TopEntry(counter.incrementAndGet(), user.getName(), user.getId(), user.getBalance(currency)));
            });

            this.topEntries.put(currency.getId(), entries);
        });
    }

    public void hideFromTops(@NotNull Player player) {
        CoinsUser user = this.plugin.getUserManager().getOrFetch(player);
        user.setHiddenFromTops(player.hasPermission(Perms.HIDE_FROM_TOPS));
    }

    public boolean showLeaderboard(@NotNull CommandSender sender, @NotNull Currency currency, int page) {
        if (sender instanceof Player player && this.topMenu != null) {
            this.topMenu.open(player, currency);
            return true;
        }

        int perPage = Config.TOPS_ENTRIES_PER_PAGE.get();

        List<TopEntry> full = this.getTopEntries(currency);

        List<List<TopEntry>> split = Lists.split(full, perPage);
        int pages = split.size();
        int index = Math.max(0, Math.min(pages, page) - 1);
        int realPage = index + 1;

        List<TopEntry> entries = pages > 0 ? split.get(index) : new ArrayList<>();

        boolean hasNextPage = realPage < pages;
        boolean hasPrevPage = index > 0;

        currency.sendPrefixed(Lang.TOP_LIST, sender, replacer -> replacer
            .replace(Placeholders.GENERIC_NEXT_PAGE, () -> (hasNextPage ? Lang.TOP_LIST_NEXT_PAGE_ACTIVE : Lang.TOP_LIST_NEXT_PAGE_INACTIVE).getString()
                .replace(Placeholders.GENERIC_VALUE, String.valueOf(realPage + 1))
            )
            .replace(Placeholders.GENERIC_PREVIOUS_PAGE, () -> (hasPrevPage ? Lang.TOP_LIST_PREVIOUS_PAGE_ACTIVE : Lang.TOP_LIST_PREVIOUS_PAGE_INACTIVE).getString()
                .replace(Placeholders.GENERIC_VALUE, String.valueOf(realPage - 1))
            )
            .replace(currency.replacePlaceholders())
            .replace(Placeholders.GENERIC_CURRENT, realPage)
            .replace(Placeholders.GENERIC_MAX, pages)
            .replace(Placeholders.GENERIC_ENTRY, list -> {
                for (TopEntry entry : entries) {
                    list.add(Lang.TOP_ENTRY.getString()
                        .replace(Placeholders.GENERIC_POS, NumberUtil.format(entry.getPosition()))
                        .replace(Placeholders.GENERIC_BALANCE, currency.format(entry.getBalance()))
                        .replace(Placeholders.PLAYER_NAME, entry.getName()));
                }
            })
        );

        return true;
    }

    @NotNull
    public Map<String, Map<String, TopEntry>> getTopEntriesMap() {
        return this.topEntries;
    }

    @NotNull
    public List<TopEntry> getTopEntries(@NotNull Currency currency) {
        return new ArrayList<>(this.topEntries.getOrDefault(currency.getId(), Collections.emptyMap()).values());
    }

    @Nullable
    public TopEntry getTopEntry(@NotNull Currency currency, @NotNull String name) {
        return this.topEntries.getOrDefault(currency.getId(), Collections.emptyMap()).get(name.toLowerCase());
    }

    public double getTotalBalance(@NotNull Currency currency) {
        return this.getTopEntries(currency).stream().mapToDouble(TopEntry::getBalance).sum();
    }
}
