package su.nightexpress.coinsengine.command.currency.provider;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.CommandDefinition;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;

public interface CommandProvider {

    @NotNull String getName();

    void buildRoot(@NotNull Currency currency, @NotNull HubNodeBuilder builder);

    void build(@NotNull Currency currency, @NotNull LiteralNodeBuilder builder);

    boolean isAvailable(@NotNull Currency currency);

    @NotNull CommandDefinition getDefaultDefinition();
}
