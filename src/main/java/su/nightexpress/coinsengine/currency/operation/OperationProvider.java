package su.nightexpress.coinsengine.currency.operation;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.api.currency.CurrencyOperation;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public interface OperationProvider<T extends CurrencyOperation> {

    @NotNull T provide(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull CommandSender sender);
}
