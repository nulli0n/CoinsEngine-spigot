package su.nightexpress.coinsengine.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.util.Lists;

public class CommandArguments {

    public static final String PLAYER   = "player";
    public static final String AMOUNT   = "amount";
    public static final String CURRENCY = "currency";
    public static final String NAME     = "name";
    public static final String SYMBOL   = "symbol";
    public static final String DECIMALS = "decimals";

    @NotNull
    public static ArgumentBuilder<Currency> currency(@NotNull CoinsEnginePlugin plugin) {
        return CommandArgument.builder(CURRENCY, (string, context) -> plugin.getCurrencyManager().getCurrency(string))
            .localized(Lang.COMMAND_ARGUMENT_NAME_CURRENCY)
            .customFailure(Lang.ERROR_COMMAND_ARGUMENT_INVALID_CURRENCY)
            .withSamples(context -> plugin.getCurrencyManager().getCurrencyIds())
            ;
    }

    @NotNull
    public static ArgumentBuilder<Double> amount() {
        return ArgumentTypes.decimalCompactAbs(AMOUNT)
            .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
            .withSamples(context -> Lists.newList("1", "10", "100", "500"))
            ;
    }
}
