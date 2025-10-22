package su.nightexpress.coinsengine.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.currency.operation.NotificationTarget;
import su.nightexpress.coinsengine.currency.operation.OperationContext;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.text.night.NightMessage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CurrencyLogger {

    private final CoinsEnginePlugin       plugin;
    private final BlockingQueue<LogEntry> queue;
    private final DateTimeFormatter       formatter;

    private final boolean logToConsole;
    private final boolean logToFile;

    private BufferedWriter writer;
    private boolean        running;

    public CurrencyLogger(@NotNull CoinsEnginePlugin plugin,
                          @NotNull DateTimeFormatter formatter,
                          @NotNull Path filePath,
                          boolean logToConsole,
                          boolean logToFile) throws IOException {
        this.plugin = plugin;
        this.formatter = formatter;
        this.logToConsole = logToConsole;
        this.logToFile = logToFile;
        this.queue = new LinkedBlockingQueue<>();

        if (logToFile) {
            this.writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            this.running = true;
        }
    }

    private record LogEntry(@NotNull String log, long timestamp) {}

    public void shutdown() {
        this.running = false;
        this.queue.clear();

        if (this.writer != null) {
            try {
                this.writer.close();
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void addEntry(@NotNull OperationContext context, @NotNull String log) {
        String stripped = NightMessage.stripTags(log);

        if (this.logToConsole && context.shouldNotify(NotificationTarget.CONSOLE_LOGGER)) {
            this.plugin.info(stripped);
        }
        if (this.logToFile && context.shouldNotify(NotificationTarget.FILE_LOGGER)) {
            this.queue.add(new LogEntry(stripped, System.currentTimeMillis()));
        }
    }

    public void write() {
        try {
            while (this.running && !this.queue.isEmpty()) {
                LogEntry result = this.queue.poll(500, TimeUnit.MILLISECONDS);
                if (result != null) {
                    String date = TimeUtil.getLocalDateTimeOf(result.timestamp()).format(this.formatter);
                    this.writer.append("[").append(date).append("] ").append(result.log());
                    this.writer.newLine();
                    this.writer.flush();
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
