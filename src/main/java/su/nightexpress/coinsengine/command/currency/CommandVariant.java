package su.nightexpress.coinsengine.command.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public record CommandVariant(boolean enabled, String[] aliases) implements Writeable {

    @NotNull
    public static CommandVariant read(@NotNull FileConfig config, @NotNull String path) {
        boolean enabled = ConfigValue.create(path + ".Enabled", false).read(config);
        String[] aliases = config.getStringArray(path + ".Aliases");

        return new CommandVariant(enabled, aliases);
    }

    @NotNull
    public static CommandVariant enabled(@NotNull String... aliases) {
        return new CommandVariant(true, aliases);
    }

    @NotNull
    public static CommandVariant disabled(@NotNull String... aliases) {
        return new CommandVariant(false, aliases);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Enabled", this.enabled);
        config.setStringArray(path + ".Aliases", this.aliases);
    }
}
