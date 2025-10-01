package su.nightexpress.coinsengine.tops;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.profile.CachedProfile;
import su.nightexpress.nightcore.util.profile.PlayerProfiles;

import java.util.UUID;

public class TopEntry {

    private final int position;
    private final String name;
    private final UUID playerId;
    private final double balance;

    private final CachedProfile profile;

    public TopEntry(int position, @NotNull String name, @NotNull UUID playerId, double balance) {
        this.position = position;
        this.name = name;
        this.playerId = playerId;
        this.balance = balance;

        this.profile = PlayerProfiles.createProfile(this.playerId, this.name);
        this.profile.update();
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

    @NotNull
    public CachedProfile getProfile() {
        return this.profile;
    }
}
