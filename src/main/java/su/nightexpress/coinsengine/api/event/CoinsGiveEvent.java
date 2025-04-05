package su.nightexpress.coinsengine.api.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import org.jetbrains.annotations.NotNull;

public class CoinsGiveEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final OfflinePlayer user;
    private final CommandSender from;
    private final Currency currency;
    private final String currencyName;
    private double amount;

    public CoinsGiveEvent(@NotNull OfflinePlayer user, @NotNull Currency currency, double amount, @NotNull CommandSender from) {
        this.user = user;
        this.currency = currency;
        this.currencyName = currency.getName();
        this.amount = amount;
        this.from = from;
    }

    @NotNull
    public OfflinePlayer getUser() {
        return user;
    }

    @NotNull
    public CommandSender getFrom() {
        return from;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}