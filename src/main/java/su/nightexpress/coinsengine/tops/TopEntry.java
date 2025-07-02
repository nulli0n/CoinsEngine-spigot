package su.nightexpress.coinsengine.tops;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TopEntry {

    private final int position;
    private final String name;
    private final UUID playerId;
    private final double balance;

    public TopEntry(int position, @NotNull String name, @NotNull UUID playerId, double balance) {
        this.position = position;
        this.name = name;
        this.playerId = playerId;
        this.balance = balance;
    }

    public int getPosition() {
        return this.position;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public UUID getPlayerId() {
        return this.playerId;
    }

    public double getBalance() {
        return this.balance;
    }
}
