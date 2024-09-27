package su.nightexpress.coinsengine.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.builder.SimpleFlagBuilder;
import su.nightexpress.nightcore.command.experimental.flag.SimpleFlag;

public class CommandFlags {

    public static final String SILENT = "s";

    @NotNull
    public static SimpleFlagBuilder silent() {
        return SimpleFlag.builder(SILENT);
    }
}
