package su.nightexpress.coinsengine.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.CommandFlag;
import su.nightexpress.nightcore.command.experimental.builder.SimpleFlagBuilder;
import su.nightexpress.nightcore.command.experimental.flag.SimpleFlag;

public class CommandFlags {

    public static final CommandFlag<Boolean> SILENT_LEGACY  = CommandFlag.booleanFlag("s");
    public static final CommandFlag<Boolean> NO_SAVE_LEGACY = CommandFlag.booleanFlag("nosave");

    public static final String SILENT = "s";

    @NotNull
    public static SimpleFlagBuilder silent() {
        return SimpleFlag.builder(SILENT);
    }
}
