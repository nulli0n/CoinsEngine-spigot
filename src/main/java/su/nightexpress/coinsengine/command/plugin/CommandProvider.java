package su.nightexpress.coinsengine.command.plugin;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;

public interface CommandProvider {

    void build(@NotNull HubNodeBuilder builder);
}
