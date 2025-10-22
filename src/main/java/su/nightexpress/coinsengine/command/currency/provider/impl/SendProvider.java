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
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;

public class SendProvider extends CurrencyCommandProvider {

    public SendProvider(@NotNull CoinsEnginePlugin plugin, @NotNull CurrencyRegistry registry, @NotNull CurrencyManager manager) {
        super(plugin, registry, manager, ProviderNames.PAY);
    }

    @Override
    public void buildRoot(@NotNull Currency currency, @NotNull HubNodeBuilder builder) {

    }

    @Override
    public void build(@NotNull Currency currency, @NotNull LiteralNodeBuilder builder) {
        builder
            .playerOnly()
            .permission(Perms.COMMAND_CURRENCY_SEND)
            .description(Lang.COMMAND_CURRENCY_SEND_DESC)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                CommandArguments.amount()
            )
            .executes((context, arguments) -> {
                Player from = context.getPlayerOrThrow();
                String targetName = arguments.getString(CommandArguments.PLAYER);
                double amount = arguments.getDouble(CommandArguments.AMOUNT);

                return this.manager.send(from, targetName, currency, amount);
            });
    }

    @Override
    public boolean isAvailable(@NotNull Currency currency) {
        return currency.isTransferAllowed();
    }

    @Override
    @NotNull
    public CommandDefinition getDefaultDefinition() {
        return new CommandDefinition(CommandVariant.enabled("pay"), CommandVariant.enabled("pay"));
    }
}
