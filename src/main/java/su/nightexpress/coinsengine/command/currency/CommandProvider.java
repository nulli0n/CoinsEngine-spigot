package su.nightexpress.coinsengine.command.currency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;

import java.util.function.Predicate;

public class CommandProvider {

    private final Predicate<Currency> predicate;
    private final CommandBuilder      builder;

    public CommandProvider(@NotNull CommandBuilder builder, @Nullable Predicate<Currency> predicate) {
        this.predicate = predicate;
        this.builder = builder;
    }

    public boolean canProvide(@NotNull Currency currency) {
        return this.predicate == null || this.predicate.test(currency);
    }

    public void build(@NotNull Currency currency, @NotNull DirectNodeBuilder builder) {
        this.builder.build(currency, builder);
    }
}
