package su.nightexpress.coinsengine.command.currency.provider.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandArguments;
import su.nightexpress.coinsengine.command.currency.CommandDefinition;
import su.nightexpress.coinsengine.command.currency.CommandVariant;
import su.nightexpress.coinsengine.command.currency.provider.type.CurrencyCommandProvider;
import su.nightexpress.coinsengine.command.currency.provider.ProviderNames;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;

public class ExchangeProvider extends CurrencyCommandProvider {

    public ExchangeProvider(@NotNull CoinsEnginePlugin plugin, @NotNull CurrencyRegistry registry, @NotNull CurrencyManager manager) {
        super(plugin, registry, manager, ProviderNames.EXCHANGE);
    }

    @Override
    public void buildRoot(@NotNull Currency currency, @NotNull HubNodeBuilder builder) {

    }

    @Override
    public void build(@NotNull Currency currency, @NotNull LiteralNodeBuilder builder) {
        builder
            .playerOnly()
            .permission(Perms.COMMAND_CURRENCY_EXCHANGE)
            .description(Lang.COMMAND_CURRENCY_EXCHANGE_DESC)
            .withArguments(CommandArguments.currency(this.registry)
                .suggestions((reader, context) -> this.registry.getCurrencies().stream().filter(currency::canExchangeTo).map(Currency::getId).toList()),
                CommandArguments.amount()
            )
            .executes((context, arguments) -> {
                Player player = context.getPlayerOrThrow();
                Currency targetCurrency = arguments.get(CommandArguments.CURRENCY, Currency.class);
                double amount = arguments.getDouble(CommandArguments.AMOUNT);

                return this.manager.exchange(player, currency, targetCurrency, amount);
            });
    }

    @Override
    public boolean isAvailable(@NotNull Currency currency) {
        return currency.isExchangeAllowed();
    }

    @Override
    @NotNull
    public CommandDefinition getDefaultDefinition() {
        return new CommandDefinition(CommandVariant.enabled("exchange"), CommandVariant.disabled("exchange"));
    }
}
