package su.nightexpress.coinsengine.currency;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.api.currency.CurrencyOperation;
import su.nightexpress.coinsengine.currency.operation.ConsoleOperation;
import su.nightexpress.coinsengine.currency.operation.OperationProvider;
import su.nightexpress.coinsengine.currency.operation.impl.AddOperation;
import su.nightexpress.coinsengine.currency.operation.impl.RemoveOperation;
import su.nightexpress.coinsengine.currency.operation.impl.SetOperation;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public class CurrencyOperations {

    @NotNull
    private static <T extends CurrencyOperation> T silent(@NotNull T operation) {
        operation.setLoggable(false);

        if (operation instanceof ConsoleOperation<?> consoleOperation) {
            consoleOperation.setFeedback(false);
            consoleOperation.setNotify(false);
        }

        return operation;
    }

    @NotNull
    public static AddOperation forAddSilently(@NotNull Currency currency, double amount, @NotNull CoinsUser user) {
        return createSilently(currency, amount, user, AddOperation::new);
    }

    @NotNull
    public static AddOperation forAdd(@NotNull Currency currency, double amount, @NotNull CoinsUser user) {
        return create(currency, amount, user, AddOperation::new);
    }

    @NotNull
    public static AddOperation forAdd(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull CommandSender sender) {
        return create(currency, amount, user, sender, AddOperation::new);
    }

    @NotNull
    public static SetOperation forSetSilently(@NotNull Currency currency, double amount, @NotNull CoinsUser user) {
        return createSilently(currency, amount, user, SetOperation::new);
    }

    @NotNull
    public static SetOperation forSet(@NotNull Currency currency, double amount, @NotNull CoinsUser user) {
        return create(currency, amount, user, SetOperation::new);
    }

    @NotNull
    public static SetOperation forSet(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull CommandSender sender) {
        return create(currency, amount, user, sender, SetOperation::new);
    }

    @NotNull
    public static RemoveOperation forRemoveSilently(@NotNull Currency currency, double amount, @NotNull CoinsUser user) {
        return createSilently(currency, amount, user, RemoveOperation::new);
    }

    @NotNull
    public static RemoveOperation forRemove(@NotNull Currency currency, double amount, @NotNull CoinsUser user) {
        return create(currency, amount, user, RemoveOperation::new);
    }

    @NotNull
    public static RemoveOperation forRemove(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull CommandSender sender) {
        return create(currency, amount, user, sender, RemoveOperation::new);
    }

    @NotNull
    private static <T extends CurrencyOperation> T createSilently(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull OperationProvider<T> provider) {
        return silent(create(currency, amount, user, provider));
    }

    @NotNull
    private static <T extends CurrencyOperation> T create(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull OperationProvider<T> provider) {
        return create(currency, amount, user, Bukkit.getConsoleSender(), provider);
    }

    @NotNull
    private static <T extends CurrencyOperation> T create(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull CommandSender sender, @NotNull OperationProvider<T> provider) {
        return provider.provide(currency, amount, user, sender);
    }
}
