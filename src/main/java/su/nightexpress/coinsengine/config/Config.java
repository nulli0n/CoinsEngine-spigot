package su.nightexpress.coinsengine.config;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Plugins;

import java.time.format.DateTimeFormatter;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.coinsengine.Placeholders.*;

public class Config {

    public static final String DIR_CURRENCIES = "/currencies/";
    public static final String LOG_FILENAME   = "operations.log";

    public static final ConfigValue<Boolean> GENERAL_PLACEHOLDER_API_FOR_CURRENCY_FORMAT = ConfigValue.create("General.PlaceholderAPI_For_Currency_Format",
        true,
        "Sets whether to apply PlaceholderAPI placeholders for currency 'Format' setting.",
        "Allows you to use custom images from Oraxen or ItemsAdder, as well as any other player unrelated placeholders."
    );

    public static final ConfigValue<Boolean> CURRENCY_PREFIX_ENABLED = ConfigValue.create("Currency.Prefix.Enabled",
        true,
        "Controls whether or not currency messages will use custom prefix instead of the plugin's one."
    );

    public static final ConfigValue<String> CURRENCY_PREFIX_FORMAT = ConfigValue.create("Currency.Prefix.Format",
        LIGHT_YELLOW.enclose(BOLD.enclose(CURRENCY_PREFIX)) + DARK_GRAY.enclose(" » ") + GRAY.getBracketsName(),
        "Sets custom prefix format for currency messages.",
        "You can use 'Currency' placeholders: " + WIKI_PLACEHOLDERS
    );

    public static final ConfigValue<Boolean> CURRENCY_COMMAND_DEFAULT_TO_BALANCE = ConfigValue.create("Currency.Command.Default_To_Balance",
        true,
        "When enabled, fallbacks to the balance command when no or invalid argument(s) were provided.",
        "Example: /coins => /coins balance"
    );

    public static final ConfigValue<Boolean> ECONOMY_COMMAND_SHORTCUTS_ENABLED = ConfigValue.create("Economy.Command_Shortcuts.Enabled",
        true,
        "Sets whether or not plugin will register shortcut versions of 'balance', 'pay' and 'top' commands for Vault-compatible currency.",
        "When this setting disabled, you will have to use, for example, '/money balance' command instead of just '/balance'."
    );

    public static final ConfigValue<Integer> TOP_ENTRIES_PER_PAGE = ConfigValue.create("Top.Entries_Per_Page",
        10,
        "Sets how many entries will be displayed per page for currency top commands."
    );

    public static final ConfigValue<Integer> TOP_UPDATE_INTERVAL = ConfigValue.create("Top.Update_Interval",
        300,
        "Sets how often (in seconds) currency top balances will be fetched & updated."
    );

    public static final ConfigValue<DateTimeFormatter> LOGS_DATE_FORMAT = new ConfigValue<>("Logs.DateFormat",
        (cfg, path, def) -> DateTimeFormatter.ofPattern(cfg.getString(path, "dd/MM/yyyy HH:mm:ss")),
        (cfg, path, formatter) -> cfg.set(path, "dd/MM/yyyy HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
        "Sets logs date format."
    );

    public static final ConfigValue<Boolean> LOGS_TO_CONSOLE = ConfigValue.create("Logs.Enabled.Console",
        false,
        "Sets whether or not all currency command-based balance operations will be logged to console."
    );

    public static final ConfigValue<Boolean> LOGS_TO_FILE = ConfigValue.create("Logs.Enabled.File",
        true,
        "Sets whether or not all currency command-based balance operations will be logged to a file."
    );

    public static boolean useCurrencyFormatPAPI() {
        return GENERAL_PLACEHOLDER_API_FOR_CURRENCY_FORMAT.get() && Plugins.hasPlaceholderAPI();
    }
}
