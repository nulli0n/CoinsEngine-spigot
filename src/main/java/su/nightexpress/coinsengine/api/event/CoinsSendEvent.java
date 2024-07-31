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

public class CoinsSendEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Currency currency;
    private final String currencyName;
    private double amount;
    private final OfflinePlayer target;
    private final CommandSender from;

    public CoinsSendEvent(@NotNull OfflinePlayer target, @NotNull Currency currency, double amount, @NotNull CommandSender from) {
        this.target = target;
        this.currency = currency;
        this.currencyName = currency.getName();
        this.amount = amount;
        this.from = from;
    }

    @NotNull
    public OfflinePlayer getTarget() {
        return target;
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrencyName() {
        return currencyName;
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