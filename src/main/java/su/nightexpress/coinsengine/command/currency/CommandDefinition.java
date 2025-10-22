package su.nightexpress.coinsengine.command.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public record CommandDefinition(@NotNull CommandVariant children, @NotNull CommandVariant dedicated) implements Writeable {

    @NotNull
    public static CommandDefinition read(@NotNull FileConfig config, @NotNull String path) {
        CommandVariant childVar = CommandVariant.read(config, path + ".Children");
        CommandVariant dedicVar = CommandVariant.read(config, path + ".Dedicated");

        return new CommandDefinition(childVar, dedicVar);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Children", this.children);
        config.set(path + ".Dedicated", this.dedicated);
    }
}
