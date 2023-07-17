package su.nightexpress.coinsengine.util;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class CoinsLogger {

    public static void logGive(@NotNull CoinsUser user, @NotNull Currency currency, double amount, @NotNull CommandSender from) {
        String text = user.getName() + " received " + currency.formatValue(amount) + " " + currency.getName()
            + " from " + from.getName() + ". New balance: " + currency.format(user.getCurrencyData(currency).getBalance());
        log(text);
    }

    public static void logSet(@NotNull CoinsUser user, @NotNull Currency currency, double amount, @NotNull CommandSender from) {
        String text = user.getName() + "'s " + currency.getName() + " balance set to " + currency.formatValue(amount)
            + " by " + from.getName() + ". New balance: " + currency.format(user.getCurrencyData(currency).getBalance());
        log(text);
    }

    public static void logTake(@NotNull CoinsUser user, @NotNull Currency currency, double amount, @NotNull CommandSender from) {
        String text = user.getName() + " lost " + currency.formatValue(amount) + " " + currency.getName()
            + " by " + from.getName() + ". New balance: " + currency.format(user.getCurrencyData(currency).getBalance());
        log(text);
    }

    private static void log(@NotNull String text) {
        if (!Config.LOGS_TO_CONSOLE.get() && !Config.LOGS_TO_FILE.get()) return;

        if (Config.LOGS_TO_CONSOLE.get()) {
            CoinsEngineAPI.PLUGIN.info(text);
        }
        if (Config.LOGS_TO_FILE.get()) {
            String date = LocalDateTime.now().format(Config.LOGS_DATE_FORMAT.get());
            String path = CoinsEngineAPI.PLUGIN.getDataFolder() + "/" + Config.LOG_FILENAME;
            BufferedWriter output;
            try {
                output = new BufferedWriter(new FileWriter(path, true));
                output.append("[").append(date).append("] ").append(text);
                output.newLine();
                output.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
