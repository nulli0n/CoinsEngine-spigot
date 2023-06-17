package su.nightexpress.coinsengine.config;

import su.nexmedia.engine.api.config.JOption;

import java.time.format.DateTimeFormatter;

public class Config {

    public static final String DIR_CURRENCIES = "/currencies/";
    public static final String LOG_FILENAME   = "operations.log";

    public static final JOption<Integer> TOP_ENTRIES_PER_PAGE = JOption.create("Top.Entries_Per_Page",
        10,
        "Sets how many entries will be displayed per page for currency top commands.");

    public static final JOption<Integer> TOP_UPDATE_INTERVAL = JOption.create("Top.Update_Interval",
        300,
        "Sets how often (in seconds) currency top balances will be fetched & updated.");

    public static final JOption<DateTimeFormatter> LOGS_DATE_FORMAT = new JOption<DateTimeFormatter>("Logs.DateFormat",
        (cfg, path, def) -> DateTimeFormatter.ofPattern(cfg.getString(path, "dd/MM/yyyy HH:mm:ss")),
        () -> DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
        "Sets logs date format.")
        .setWriter((cfg, path, formatter) -> cfg.set(path, "dd/MM/yyyy HH:mm:ss"));

    public static final JOption<Boolean> LOGS_TO_CONSOLE = JOption.create("Logs.Enabled.Console",
        false,
        "Sets whether or not all currency command-based balance operations will be logged to console.");

    public static final JOption<Boolean> LOGS_TO_FILE = JOption.create("Logs.Enabled.File",
        true,
        "Sets whether or not all currency command-based balance operations will be logged to a file.");


}
