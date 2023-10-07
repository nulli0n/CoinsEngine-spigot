package su.nightexpress.coinsengine.config;

import su.nexmedia.engine.api.config.JOption;
import su.nightexpress.coinsengine.currency.AmountShortcut;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Config {

    public static final String DIR_CURRENCIES = "/currencies/";
    public static final String LOG_FILENAME   = "operations.log";

    public static final JOption<Map<String, AmountShortcut>> AMOUNT_SHORTCUTS = JOption.forMap("General.Amount_Shortcuts",
        (cfg, path, key) -> AmountShortcut.read(cfg, path + "." + key),
        () -> {
            return Map.of(
                "thousand", new AmountShortcut("k", 1_000D),
                "million", new AmountShortcut("m", 1_000_000D),
                "billion", new AmountShortcut("b", 1_000_000_000D)
            );
        },
        "Here you can create custom amount shortcuts to use in commands.",
        "For example, with default settings you can use values like 1K (1000), 5KK or 5M (5 millions), 7KKK or 7B (7 billions).",
        "Also, this has addictive behavior, so using, for example, 2MM will result in 2 * (million * million)"
    ).setWriter((cfg, path, map) -> map.forEach((id, as) -> as.write(cfg, path + "." + id)));

    public static final JOption<Boolean> ECONOMY_COMMAND_SHORTCUTS_ENABLED = JOption.create("Economy.Command_Shortcuts.Enabled",
        true,
        "Sets whether or not plugin will register shortcut versions of 'balance', 'pay' and 'top' commands for Vault-compatible currency.",
        "When this setting disabled, you will have to use, for example, '/money balance' command instead of just '/balance'.");

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
