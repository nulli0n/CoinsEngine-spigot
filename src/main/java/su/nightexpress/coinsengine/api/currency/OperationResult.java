package su.nightexpress.coinsengine.api.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.text.NightMessage;

public class OperationResult {

    private final long    timestamp;
    private final String  log;
    private final boolean success;

    public OperationResult(long timestamp, @NotNull String log, boolean success) {
        this.timestamp = timestamp;
        this.log = NightMessage.stripTags(log);
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @NotNull
    public String getLog() {
        return this.log;
    }

    public boolean isSuccess() {
        return this.success;
    }
}
