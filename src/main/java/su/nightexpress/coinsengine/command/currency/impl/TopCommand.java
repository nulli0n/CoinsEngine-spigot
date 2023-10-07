package su.nightexpress.coinsengine.command.currency.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.Pair;
import su.nightexpress.coinsengine.CoinsEngine;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.CurrencySubCommand;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TopCommand extends CurrencySubCommand {

    public TopCommand(@NotNull CoinsEngine plugin, @NotNull Currency currency, @NotNull String... aliases) {
        super(plugin, currency, aliases, Perms.COMMAND_CURRENCY_TOP);
        this.setDescription(plugin.getMessage(Lang.COMMAND_CURRENCY_TOP_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_CURRENCY_TOP_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.asList("1", "2", "3", "4", "5", "10", "20");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        int perPage = Config.TOP_ENTRIES_PER_PAGE.get();

        List<Pair<String, Double>> full = this.plugin.getCurrencyManager().getBalanceMap().getOrDefault(this.currency, Collections.emptyList());
        List<List<Pair<String, Double>>> split = CollectionsUtil.split(full, perPage);
        int pages = split.size();
        int page = Math.max(0, Math.min(pages, Math.abs(result.getInt(1, 1))) - 1);

        List<Pair<String, Double>> list = pages > 0 ? split.get(page) : new ArrayList<>();
        AtomicInteger pos = new AtomicInteger(1 + perPage * page);

        this.plugin.getMessage(Lang.COMMAND_CURRENCY_TOP_LIST)
            .replace(currency.replacePlaceholders())
            .replace(Placeholders.GENERIC_CURRENT, page + 1)
            .replace(Placeholders.GENERIC_MAX, pages)
            .replace(str -> str.contains(Placeholders.GENERIC_BALANCE), (line, list1) -> {
                for (Pair<String, Double> pair : list) {
                    list1.add(line
                        .replace(Placeholders.GENERIC_POS, NumberUtil.format(pos.getAndIncrement()))
                        .replace(Placeholders.GENERIC_BALANCE, currency.format(pair.getSecond()))
                        .replace(Placeholders.PLAYER_NAME, pair.getFirst()));
                }
            })
            .send(sender);
    }
}
