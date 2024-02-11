package su.nightexpress.coinsengine.command;

import su.nightexpress.nightcore.command.CommandFlag;

public class CommandFlags {

    public static final CommandFlag<Boolean> SILENT  = CommandFlag.booleanFlag("s");
    public static final CommandFlag<Boolean> NO_SAVE = CommandFlag.booleanFlag("nosave");
}
