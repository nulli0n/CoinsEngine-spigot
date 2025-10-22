package su.nightexpress.coinsengine.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.Lists;

public class CommandArguments {

    public static final String PLAYER   = "player";
    public static final String AMOUNT   = "amount";
    public static final String CURRENCY = "currency";
    public static final String NAME     = "name";
    public static final String SYMBOL   = "symbol";
    public static final String DECIMALS = "decimals";

    public static final String FLAG_SILENT          = "s";
    public static final String FLAG_SILENT_FEEDBACK = "sf";

    @NotNull
    public static ArgumentNodeBuilder<Currency> currency(@NotNull CurrencyRegistry registry) {
        return Commands.argument(CURRENCY, (context, string) -> registry.byId(string).orElseThrow(() -> CommandSyntaxException.custom(Lang.COMMAND_SYNTAX_INVALID_CURRENCY)))
            .localized(Lang.COMMAND_ARGUMENT_NAME_CURRENCY)
            .suggestions((reader, context) -> registry.getCurrencyIds());
    }

    @NotNull
    public static ArgumentNodeBuilder<Double> amount() {
        return Arguments.decimalCompact(AMOUNT)
            .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
            .suggestions((reader, context) -> Lists.newList("1", "10", "100", "500"));
    }
}
