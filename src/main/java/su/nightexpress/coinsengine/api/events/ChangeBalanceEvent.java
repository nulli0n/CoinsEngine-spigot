package su.nightexpress.coinsengine.api.events;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;

public class ChangeBalanceEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Currency currency;
    private final double oldAmount;
    private final double newAmount;


    public ChangeBalanceEvent(@NotNull Player who, @NotNull Currency currency,double oldAmount, double newAmount) {
        this.player = who;
        this.currency = currency;
        this.oldAmount = oldAmount;
        this.newAmount = newAmount;
    }

    @NotNull
    public final Player getPlayer() {
        return this.player;
    }

    @NotNull
    public final Currency getCurrency() {
        return this.currency;
    }

    public final double getOldAmount() {
        return this.oldAmount;
    }

    public final double getNewAmount() {
        return this.newAmount;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
