package su.nightexpress.coinsengine.command.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public class CommandVariant implements Writeable {

    private final boolean  enabled;
    private final String[] aliases;

    public CommandVariant(boolean enabled, String[] aliases) {
        this.enabled = enabled;
        this.aliases = aliases;
    }

    @NotNull
    public static CommandVariant enabled(@NotNull String... aliases) {
        return new CommandVariant(true, aliases);
    }

    @NotNull
    public static CommandVariant disabled(@NotNull String... aliases) {
        return new CommandVariant(false, aliases);
    }

    @NotNull
    public static CommandVariant read(@NotNull FileConfig config, @NotNull String path) {
        boolean enabled = ConfigValue.create(path + ".Enabled", false).read(config);
        String[] aliases = config.getStringArray(path + ".Aliases");

        return new CommandVariant(enabled, aliases);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Enabled", this.enabled);
        config.setStringArray(path + ".Aliases", this.aliases);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String[] getAliases() {
        return this.aliases;
    }
}
