package su.nightexpress.coinsengine.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.builder.SimpleFlagBuilder;
import su.nightexpress.nightcore.command.experimental.flag.SimpleFlag;

public class CommandFlags {

    public static final String SILENT          = "s";
    public static final String SILENT_FEEDBACK = "sf";

    @NotNull
    public static SimpleFlagBuilder silent() {
        return SimpleFlag.builder(SILENT);
    }

    @NotNull
    public static SimpleFlagBuilder silentOutput() {
        return SimpleFlag.builder(SILENT_FEEDBACK);
    }
}
