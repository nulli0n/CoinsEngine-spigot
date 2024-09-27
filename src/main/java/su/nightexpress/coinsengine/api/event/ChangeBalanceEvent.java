package su.nightexpress.coinsengine.api.event;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public class ChangeBalanceEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final CoinsUser user;
    private final Currency  currency;
    private final double    oldAmount;
    private final double    newAmount;


    public ChangeBalanceEvent(@NotNull CoinsUser user, @NotNull Currency currency, double oldAmount, double newAmount) {
        super(!Bukkit.isPrimaryThread());
        this.user = user;
        this.currency = currency;
        this.oldAmount = oldAmount;
        this.newAmount = newAmount;
    }

    @NotNull
    public CoinsUser getUser() {
        return user;
    }

    @Nullable
    public final Player getPlayer() {
        return this.user.getPlayer();
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
