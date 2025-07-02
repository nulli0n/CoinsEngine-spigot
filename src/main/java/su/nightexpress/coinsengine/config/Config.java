package su.nightexpress.coinsengine.config;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Plugins;

import static su.nightexpress.coinsengine.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Config {

    public static final String DIR_CURRENCIES = "/currencies/";
    public static final String DIR_MENU = "/menu/";
    public static final String LOG_FILENAME   = "operations.log";

    public static final ConfigValue<Boolean> GENERAL_PLACEHOLDER_API_FOR_CURRENCY_FORMAT = ConfigValue.create("General.PlaceholderAPI_For_Currency_Format",
        true,
        "Sets whether to apply PlaceholderAPI placeholders for currency 'Format' setting.",
        "Allows you to use custom images from Oraxen or ItemsAdder, as well as any other player unrelated placeholders."
    );

    public static final ConfigValue<Boolean> TOPS_ENABLED = ConfigValue.create("Top.Enabled",
        true,
        "Controls whether Tops feature is enabled.",
        "[*] This feature is required for the 'server balance' placeholders to work.",
        WIKI_TOPS
    );

    public static final ConfigValue<Boolean> TOPS_USE_GUI = ConfigValue.create("Top.Use_GUI",
        true,
        "Controls whether GUI is preferred to display balance leaderboard.",
        "[*] Disable if you want it be text only."
    );

    public static final ConfigValue<Integer> TOPS_ENTRIES_PER_PAGE = ConfigValue.create("Top.Entries_Per_Page",
        10,
        "Sets how many entries displayed per page for currency top commands.",
        "[*] Works only for text leaderboards. GUI settings available in the '" + DIR_MENU + "' directory."
    );

    public static final ConfigValue<Integer> TOPS_UPDATE_INTERVAL = ConfigValue.create("Top.Update_Interval",
        900,
        "Sets update interval (in seconds) for currency top balance lists.",
        "[Asynchronous]",
        "[Default is 900 (15 minutes)]"
    );

    public static final ConfigValue<Boolean> CURRENCY_PREFIX_ENABLED = ConfigValue.create("Currency.Prefix.Enabled",
        true,
        "Controls whether or not currency messages will use custom prefix instead of the plugin's one.",
        WIKI_PREFIXES
    );

    public static final ConfigValue<String> CURRENCY_PREFIX_FORMAT = ConfigValue.create("Currency.Prefix.Format",
        LIGHT_YELLOW.wrap(BOLD.wrap(CURRENCY_PREFIX)) + DARK_GRAY.wrap(" » ") + GRAY.getBracketsName(),
        "Sets custom prefix format for currency messages.",
        "You can use 'Currency' placeholders: " + WIKI_PLACEHOLDERS
    );

    public static final ConfigValue<Boolean> WALLET_ENABLED = ConfigValue.create("Wallet.Enabled",
        true,
        "Controls whether Wallet feature is enabled.",
        WIKI_WALLET
    );

    public static final ConfigValue<String[]> WALLET_ALIASES = ConfigValue.create("Wallet.Command_Aliases",
        new String[]{"wallet"},
        "Command aliases for the Wallet feature."
    );

    public static final ConfigValue<Boolean> MIGRATION_ENABLED = ConfigValue.create("Migration.Enabled",
        true,
        "Controls whether Migration feature is available.",
        "Disable if you don't plan to migrate from other plugins to save some RAM.",
        WIKI_MIGRATION
    );

    public static final ConfigValue<Boolean> LOGS_TO_CONSOLE = ConfigValue.create("Logs.Enabled.Console",
        false,
        "Controls whether currency operations will be logged to console."
    );

    public static final ConfigValue<Boolean> LOGS_TO_FILE = ConfigValue.create("Logs.Enabled.File",
        true,
        "Controls whether currency operations will be logged to a file."
    );

    public static final ConfigValue<String> LOGS_DATE_FORMAT = ConfigValue.create("Logs.DateFormat",
        "dd/MM/yyyy HH:mm:ss",
        "Logs date format."
    );

    public static final ConfigValue<Integer> LOGS_WRITE_INTERVAL = ConfigValue.create("Logs.Write_Interval",
        5,
        "Controls how often currency operations writes to the log file."
    );

    public static boolean isTopsEnabled() {
        return TOPS_ENABLED.get();
    }

    public static boolean isWalletEnabled() {
        return WALLET_ENABLED.get();
    }

    public static boolean isMigrationEnabled() {
        return MIGRATION_ENABLED.get();
    }

    public static boolean useCurrencyFormatPAPI() {
        return GENERAL_PLACEHOLDER_API_FOR_CURRENCY_FORMAT.get() && Plugins.hasPlaceholderAPI();
    }
}
