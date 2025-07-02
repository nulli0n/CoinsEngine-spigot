package su.nightexpress.coinsengine.api.event;


import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BalanceLogEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final String message;

    public BalanceLogEvent(@NotNull String message) {
        super(!Bukkit.isPrimaryThread());
        this.message = message;
    }

    @NotNull
    public String getMessage() {
        return message;
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
