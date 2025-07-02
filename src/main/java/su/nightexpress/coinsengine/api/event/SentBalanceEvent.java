package su.nightexpress.coinsengine.api.event;


import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public class SentBalanceEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final CoinsUser from;
    private final CoinsUser target;
    private final Currency currency;
    private final double amount;

    public SentBalanceEvent(@NotNull CoinsUser from, @NotNull CoinsUser target, @NotNull Currency currency, double amount) {
        super(!Bukkit.isPrimaryThread());
        this.from = from;
        this.target = target;
        this.currency = currency;
        this.amount = amount;
    }

    @NotNull
    public CoinsUser getFrom() {
        return from;
    }

    @NotNull
    public CoinsUser getTarget() {
        return target;
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
