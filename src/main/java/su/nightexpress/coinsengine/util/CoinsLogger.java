package su.nightexpress.coinsengine.util;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class CoinsLogger {

    private final CoinsEnginePlugin plugin;

    public CoinsLogger(@NotNull CoinsEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public void logSend(@NotNull CoinsUser target, @NotNull Currency currency, double amount, @NotNull CommandSender from) {
        String text = from.getName() + " sent " + currency.formatValue(amount) + " " + currency.getName()
            + " to " + target.getName() + ". New balance: " + currency.format(target.getBalance(currency));
        log(text);
    }

    public void logGive(@NotNull CoinsUser user, @NotNull Currency currency, double amount, @NotNull CommandSender from) {
        String text = user.getName() + " received " + currency.formatValue(amount) + " " + currency.getName()
            + " from " + from.getName() + ". New balance: " + currency.format(user.getBalance(currency));
        log(text);
    }

    public void logSet(@NotNull CoinsUser user, @NotNull Currency currency, double amount, @NotNull CommandSender from) {
        String text = user.getName() + "'s " + currency.getName() + " balance set to " + currency.formatValue(amount)
            + " by " + from.getName() + ". New balance: " + currency.format(user.getBalance(currency));
        log(text);
    }

    public void logTake(@NotNull CoinsUser user, @NotNull Currency currency, double amount, @NotNull CommandSender from) {
        String text = user.getName() + " lost " + currency.formatValue(amount) + " " + currency.getName()
            + " by " + from.getName() + ". New balance: " + currency.format(user.getBalance(currency));
        log(text);
    }

    public void logExchange(@NotNull CoinsUser user, @NotNull Currency from, @NotNull Currency to, double amount, double result) {
        String text = user.getName() + " exchanged x" + NumberUtil.format(amount) + " " + from.getName()
            + " to x" + NumberUtil.format(result) + " " + to.getName()
            + ". New balance: " + from.format(user.getBalance(from)) + " and " + to.format(user.getBalance(to));
        log(text);
    }

    private void log(@NotNull String text) {
        if (!Config.LOGS_TO_CONSOLE.get() && !Config.LOGS_TO_FILE.get()) return;

        text = Colorizer.restrip(NightMessage.asLegacy(text));

        if (Config.LOGS_TO_CONSOLE.get()) {
            this.plugin.info(text);
        }
        if (Config.LOGS_TO_FILE.get()) {
            String date = LocalDateTime.now().format(Config.LOGS_DATE_FORMAT.get());
            String path = this.plugin.getDataFolder() + "/" + Config.LOG_FILENAME;
            BufferedWriter output;
            try {
                output = new BufferedWriter(new FileWriter(path, true));
                output.append("[").append(date).append("] ").append(text);
                output.newLine();
                output.close();
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
