package su.nightexpress.coinsengine.command.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public class CommandData implements Writeable {

    private final CommandVariant childrenVariant;
    private final CommandVariant dedicatedVariant;
    private final boolean fallback;

    public CommandData(@NotNull CommandVariant childrenVariant, @NotNull CommandVariant dedicatedVariant, boolean fallback) {
        this.childrenVariant = childrenVariant;
        this.dedicatedVariant = dedicatedVariant;
        this.fallback = fallback;
    }

    @NotNull
    public static CommandData read(@NotNull FileConfig config, @NotNull String path) {
        CommandVariant childVar = CommandVariant.read(config, path + ".Children");
        CommandVariant dedicVar = CommandVariant.read(config, path + ".Dedicated");
        boolean fallback = ConfigValue.create(path + ".IsFallback", false).read(config);

        return new CommandData(childVar, dedicVar, fallback);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".IsFallback", this.fallback);
        config.set(path + ".Children", this.childrenVariant);
        config.set(path + ".Dedicated", this.dedicatedVariant);
    }

    public boolean isFallback() {
        return this.fallback;
    }

    @NotNull
    public CommandVariant getChildrenVariant() {
        return this.childrenVariant;
    }

    @NotNull
    public CommandVariant getDedicatedVariant() {
        return this.dedicatedVariant;
    }
}
