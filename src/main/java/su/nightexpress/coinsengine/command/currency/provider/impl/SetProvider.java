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
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;

public class SetProvider extends CurrencyCommandProvider {

    public SetProvider(@NotNull CoinsEnginePlugin plugin, @NotNull CurrencyRegistry registry, @NotNull CurrencyManager manager) {
        super(plugin, registry, manager, ProviderNames.SET);
    }

    @Override
    public void buildRoot(@NotNull Currency currency, @NotNull HubNodeBuilder builder) {

    }

    @Override
    public void build(@NotNull Currency currency, @NotNull LiteralNodeBuilder builder) {
        builder
            .permission(Perms.COMMAND_CURRENCY_SET)
            .description(Lang.COMMAND_CURRENCY_SET_DESC)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                CommandArguments.amount()
            )
            .withFlags(CommandArguments.FLAG_SILENT, CommandArguments.FLAG_SILENT_FEEDBACK)
            .executes((context, arguments) -> {
                double amount = Math.max(0, arguments.getDouble(CommandArguments.AMOUNT));
                String playerName = arguments.getString(CommandArguments.PLAYER);

                this.plugin.getUserManager().manageUser(playerName, user -> {
                    if (user == null) {
                        context.errorBadPlayer();
                        return;
                    }

                    OperationContext operationContext = OperationContext.of(context.getSender())
                        .silentFor(NotificationTarget.CONSOLE_LOGGER)
                        .silentFor(NotificationTarget.USER, context.hasFlag(CommandArguments.FLAG_SILENT))
                        .silentFor(NotificationTarget.EXECUTOR, context.hasFlag(CommandArguments.FLAG_SILENT_FEEDBACK));

                    this.manager.set(operationContext, user, currency, amount);
                });
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
        return new CommandDefinition(CommandVariant.enabled("set"), CommandVariant.disabled("setmoney"));
    }
}
