package su.nightexpress.coinsengine.command.currency.provider.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.CommandArguments;
import su.nightexpress.coinsengine.command.currency.CommandDefinition;
import su.nightexpress.coinsengine.command.currency.CommandVariant;
import su.nightexpress.coinsengine.command.currency.provider.ProviderNames;
import su.nightexpress.coinsengine.command.currency.provider.type.CurrencyCommandProvider;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.config.Perms;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.coinsengine.currency.operation.NotificationTarget;
import su.nightexpress.coinsengine.currency.operation.OperationContext;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;

public class GiveAllProvider extends CurrencyCommandProvider {

    public GiveAllProvider(@NotNull CoinsEnginePlugin plugin, @NotNull CurrencyRegistry registry, @NotNull CurrencyManager manager) {
        super(plugin, registry, manager, ProviderNames.GIVE_ALL);
    }

    @Override
    public void buildRoot(@NotNull Currency currency, @NotNull HubNodeBuilder builder) {

    }

    @Override
    public void build(@NotNull Currency currency, @NotNull LiteralNodeBuilder builder) {
        builder
            .permission(Perms.COMMAND_CURRENCY_GIVE_ALL)
            .description(Lang.COMMAND_CURRENCY_GIVE_ALL_DESC)
            .withArguments(CommandArguments.amount())
            .withFlags(CommandArguments.FLAG_SILENT, CommandArguments.FLAG_SILENT_FEEDBACK)
            .executes((context, arguments) -> {
                double amount = arguments.getDouble(CommandArguments.AMOUNT);
                if (amount <= 0D) return false;

                OperationContext operationContext = OperationContext.of(context.getSender())
                    .silentFor(NotificationTarget.CONSOLE_LOGGER)
                    .silentFor(NotificationTarget.USER, context.hasFlag(CommandArguments.FLAG_SILENT))
                    .silentFor(NotificationTarget.EXECUTOR, context.hasFlag(CommandArguments.FLAG_SILENT_FEEDBACK));

                this.manager.giveAll(operationContext, currency, amount);

                return true;
            });
    }

    @Override
    public boolean isAvailable(@NotNull Currency currency) {
        return true;
    }

    @Override
    @NotNull
    public CommandDefinition getDefaultDefinition() {
        return new CommandDefinition(CommandVariant.enabled("giveall"), CommandVariant.disabled("ecogiveall"));
    }
}
