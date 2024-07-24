package su.nightexpress.coinsengine.api.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import org.jetbrains.annotations.NotNull;

public class CoinsSetEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final CoinsUser user;
    private final Currency currency;
    private double amount;
    private final CommandSender from;

    public CoinsSetEvent(@NotNull CoinsUser user, @NotNull Currency currency, double amount, @NotNull CommandSender from) {
        this.user = user;
        this.currency = currency;
        this.amount = amount;
        this.from = from;
    }

    @NotNull
    public CoinsUser getUser() {
        return user;
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
    public CommandSender getFrom() {
        return from;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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