package su.nightexpress.coinsengine.tops.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.tops.TopEntry;
import su.nightexpress.coinsengine.tops.TopManager;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;

import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.coinsengine.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class TopMenu extends LinkedMenu<CoinsEnginePlugin, Currency> implements Filled<TopEntry>, ConfigBased {

    private final TopManager topManager;

    private String       entryName;
    private List<String> entryLore;
    private int[]        entrySlots;

    public TopMenu(@NotNull CoinsEnginePlugin plugin, @NotNull TopManager topManager) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap("Balance Top - " + CURRENCY_NAME));
        this.topManager = topManager;
    }

    @Override
    @NotNull
    public MenuFiller<TopEntry> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        Currency currency = this.getLink(player);

        return MenuFiller.builder(this)
            .setSlots(this.entrySlots)
            .setItems(this.topManager.getTopEntries(currency))
            .setItemCreator(entry -> {
                return NightItem.fromType(Material.PLAYER_HEAD)
                    .hideAllComponents()
                    .setDisplayName(this.entryName)
                    .setLore(this.entryLore)
                    .setPlayerProfile(entry.getProfile())
                    .replacement(replacer -> replacer
                        .replace(GENERIC_POS, entry.getPosition())
                        .replace(PLAYER_NAME, entry.getName())
                        .replace(GENERIC_BALANCE, currency.format(entry.getBalance()))
                    );
            })
            .build();
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        Currency currency = this.getLink(viewer);

        return currency.replacePlaceholders().apply(super.getTitle(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.entryName = ConfigValue.create("Entry.Name", YELLOW.wrap("#" + GENERIC_POS) + " " + WHITE.wrap(PLAYER_NAME)).read(config);

        this.entryLore = ConfigValue.create("Entry.Lore", Lists.newList(
            GREEN.wrap(GENERIC_BALANCE)
        )).read(config);

        this.entrySlots = ConfigValue.create("Entry.Slots", IntStream.range(0, 36).toArray()).read(config);

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE).setHideTooltip(true).toMenuItem().setSlots(IntStream.range(36, 45).toArray()));

        loader.addDefaultItem(MenuItem.buildNextPage(this, 44).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 36).setPriority(10));

        loader.addDefaultItem(MenuItem.buildExit(this, 40).setPriority(10));
    }
}
