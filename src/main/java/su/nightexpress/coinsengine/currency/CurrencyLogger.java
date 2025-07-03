package su.nightexpress.coinsengine.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.OperationResult;
import su.nightexpress.coinsengine.api.event.CurrencyLoggerEvent;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.nightcore.util.TimeUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CurrencyLogger {

    private final CoinsEnginePlugin plugin;
    private final BlockingQueue<OperationResult> queue;
    private final DateTimeFormatter timeFormatter;
    private final BufferedWriter writer;

    private boolean running;

    public CurrencyLogger(@NotNull CoinsEnginePlugin plugin) throws IOException {
        this.plugin = plugin;
        this.queue = new LinkedBlockingQueue<>();
        this.timeFormatter = DateTimeFormatter.ofPattern(Config.LOGS_DATE_FORMAT.get());
        this.writer = new BufferedWriter(new FileWriter(plugin.getDataFolder() + "/" + Config.LOG_FILENAME, true));
        this.running = true;
    }

    public void shutdown() {
        this.running = false;
        this.queue.clear();

        try {
            this.writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void addOperation(@NotNull OperationResult result) {
        this.queue.add(result);
    }

    public void write() {
        try {
            while (this.running && !this.queue.isEmpty()) {
                OperationResult result = this.queue.poll(500, TimeUnit.MILLISECONDS);
                if (result != null) {
                    String date = TimeUtil.getLocalDateTimeOf(result.getTimestamp()).format(this.timeFormatter);
                    String message = String.format("[%s] %s", date, result.getLog());

                    plugin.getScheduler().runTask(plugin, () -> {
                        CurrencyLoggerEvent event = new CurrencyLoggerEvent(message);
                        plugin.getServer().getPluginManager().callEvent(event);
                    });

                    this.writer.append(message);
                    this.writer.newLine();
                    this.writer.flush();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
