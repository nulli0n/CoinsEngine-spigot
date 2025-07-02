package su.nightexpress.coinsengine.command.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;

public interface CommandBuilder {

    void build(@NotNull Currency currency, @NotNull DirectNodeBuilder builder);
}
