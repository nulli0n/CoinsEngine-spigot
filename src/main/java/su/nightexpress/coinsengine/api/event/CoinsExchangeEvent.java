package su.nightexpress.coinsengine.api.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import org.jetbrains.annotations.NotNull;

public class CoinsExchangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final CoinsUser user;
    private final Currency fromCurrency;
    private final Currency toCurrency;
    private double fromAmount;
    private double toAmount;

    public CoinsExchangeEvent(@NotNull CoinsUser user, @NotNull Currency fromCurrency, @NotNull Currency toCurrency, double fromAmount, double toAmount) {
        this.user = user;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
    }

    @NotNull
    public CoinsUser getUser() {
        return user;
    }

    @NotNull
    public Currency getFromCurrency() {
        return fromCurrency;
    }

    @NotNull
    public Currency getToCurrency() {
        return toCurrency;
    }

    public double getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(double fromAmount) {
        this.fromAmount = fromAmount;
    }

    public double getToAmount() {
        return toAmount;
    }

    public void setToAmount(double toAmount) {
        this.toAmount = toAmount;
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