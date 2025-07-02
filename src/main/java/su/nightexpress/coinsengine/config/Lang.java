package su.nightexpress.coinsengine.config;

import su.nightexpress.coinsengine.command.currency.CurrencyCommands;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;

import static su.nightexpress.coinsengine.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Lang extends CoreLang {

    public static final LangString COMMAND_ARGUMENT_NAME_CURRENCY = LangString.of("Command.Argument.Name.Currency", "currency");
    public static final LangString COMMAND_ARGUMENT_NAME_PAGE     = LangString.of("Command.Argument.Name.Page", "page");
    public static final LangString COMMAND_ARGUMENT_NAME_PLUGIN   = LangString.of("Command.Argument.Name.Plugin", "plugin");
    public static final LangString COMMAND_ARGUMENT_NAME_SYMBOL   = LangString.of("Command.Argument.Name.Symbol", "symbol");
    public static final LangString COMMAND_ARGUMENT_NAME_DECIMAL   = LangString.of("Command.Argument.Name.Decimals", "allowDecimals");

    public static final LangString COMMAND_CREATE_DESC   = LangString.of("Command.Create.Desc", "Create a new currency.");
    public static final LangString COMMAND_MIGRATE_DESC   = LangString.of("Command.Migrate.Desc", "Migrate data from other plugin(s).");
    public static final LangString COMMAND_RESET_DESC     = LangString.of("Command.Reset.Desc", "Reset player's balances.");
    public static final LangString COMMAND_RESET_ALL_DESC = LangString.of("Command.ResetAll.Desc", "Reset balances of all users.");
    public static final LangString COMMAND_WALLET_DESC    = LangString.of("Command.Wallet.Desc", "View full balance.");

    public static final LangString COMMAND_CURRENCY_ROOT_DESC     = LangString.of("Command.Currency.Root.Desc", CURRENCY_NAME + " commands.");
    public static final LangString COMMAND_CURRENCY_BALANCE_DESC  = LangString.of("Command.Currency.Balance.Desc", "View balance.");
    public static final LangString COMMAND_CURRENCY_GIVE_DESC     = LangString.of("Command.Currency.Give.Desc", "Add currency to a player.");
    public static final LangString COMMAND_CURRENCY_GIVE_ALL_DESC = LangString.of("Command.Currency.GiveAll.Desc", "Add currency to all online players.");
    public static final LangString COMMAND_CURRENCY_TAKE_DESC     = LangString.of("Command.Currency.Take.Desc", "Take player's currency.");
    public static final LangString COMMAND_CURRENCY_SET_DESC      = LangString.of("Command.Currency.Set.Desc", "Set player's currency balance.");
    public static final LangString COMMAND_CURRENCY_SEND_DESC     = LangString.of("Command.Currency.Send.Desc", "Transfer currency to a player.");
    public static final LangString COMMAND_CURRENCY_PAYMENTS_DESC = LangString.of("Command.Currency.Payments.Desc", "Toggle payments acception from other players.");
    public static final LangString COMMAND_CURRENCY_EXCHANGE_DESC = LangString.of("Command.Currency.Exchange.Desc", "Exchange currency.");
    public static final LangString COMMAND_CURRENCY_TOP_DESC      = LangString.of("Command.Currency.Top.Desc", "List of players with the most balance.");



    public static final LangText MIGRATION_START_BLOCKED = LangText.of("Migration.Start.Blocked",
        LIGHT_RED.wrap("Could not start migration due to background tasks already running!"));

    public static final LangText MIGRATION_START_BAD_PLUGIN = LangText.of("Migration.Start.BadPlugin",
        LIGHT_RED.wrap("Plugin is not supported or installed!"));

    public static final LangText MIGRATION_START_BAD_CURRENCY = LangText.of("Migration.Start.BadCurrency",
        LIGHT_RED.wrap("Could not migrate data from " + GENERIC_NAME + " to " + CURRENCY_NAME + "."));

    public static final LangText MIGRATION_STARTED = LangText.of("Migration.Started",
        TAG_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + RED.wrap("→") + "] Data Migration:"),
        " ",
        GRAY.wrap("Started data migration from " + WHITE.wrap(GENERIC_NAME) + ". Please wait..."),
        " ",
        RED.wrap(UNDERLINED.wrap("All currency operations are disabled until migration is completed.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final LangText MIGRATION_COMPLETED = LangText.of("Migration.Completed",
        TAG_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + GREEN.wrap("→") + "] Data Migration:"),
        " ",
        GRAY.wrap("Finished data migration from " + WHITE.wrap(GENERIC_NAME) + "!"),
        " ",
        GREEN.wrap(UNDERLINED.wrap("All currency operations are enabled again.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );



    public static final LangText COMMAND_RESET_DONE = LangText.of("Command.Reset.Done",
        LIGHT_GRAY.wrap("Reset all currency balances for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "!"));



    public static final LangText RESET_ALL_START_BLOCKED = LangText.of("ResetAll.Start.Blocked",
        LIGHT_RED.wrap("Could not start balance reset due to background tasks already running!"));

    public static final LangText RESET_ALL_STARTED_CURRENCY = LangText.of("ResetAll.Started.Currency",
        TAG_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + RED.wrap("$") + "] Balance Reset:"),
        " ",
        GRAY.wrap("Started " + WHITE.wrap(CURRENCY_NAME) + " balance reset for all players. Please wait..."),
        " ",
        RED.wrap(UNDERLINED.wrap("All currency operations are disabled until reset is completed.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final LangText RESET_ALL_COMPLETED_CURRENCY = LangText.of("ResetAll.Completed.Currency",
        TAG_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + GREEN.wrap("$") + "] Balance Reset:"),
        " ",
        GRAY.wrap("Finished " + WHITE.wrap(CURRENCY_NAME) + " balance reset for all players!"),
        " ",
        GREEN.wrap(UNDERLINED.wrap("All currency operations are enabled again.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final LangText RESET_ALL_STARTED_GLOBAL = LangText.of("ResetAll.Started.Global",
        TAG_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + RED.wrap("$") + "] Balance Reset:"),
        " ",
        GRAY.wrap("Started balance reset for all currencies for all players. Please wait..."),
        " ",
        RED.wrap(UNDERLINED.wrap("All currency operations are disabled until reset is completed.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final LangText RESET_ALL_COMPLETED_GLOBAL = LangText.of("ResetAll.Completed.Global",
        TAG_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + GREEN.wrap("$") + "] Balance Reset:"),
        " ",
        GRAY.wrap("Finished balance reset for all currencies for all players!"),
        " ",
        GREEN.wrap(UNDERLINED.wrap("All currency operations are enabled again.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );



    public static final LangText CURRENCY_CREATE_BAD_NAME = LangText.of("Currency.Create.BadName",
        LIGHT_RED.wrap("Invalid name provided! Only latin letters and digits are supported.")
    );

    public static final LangText CURRENCY_CREATE_DUPLICATED = LangText.of("Currency.Create.Duplicated",
        LIGHT_RED.wrap("There is already a currency with this name.")
    );

    public static final LangText CURRENCY_CREATE_SUCCESS = LangText.of("Currency.Create.Success",
        GRAY.wrap("Created new currency: " + GREEN.wrap(CURRENCY_NAME) + " (ID: " + WHITE.wrap(CURRENCY_ID) + ")")
    );


    public static final LangText COMMAND_CURRENCY_GIVE_DONE = LangText.of("Command.Currency.Give.Done",
        LIGHT_GRAY.wrap("Added " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s balance. New balance: " + LIGHT_YELLOW.wrap(GENERIC_BALANCE) + "."));

    public static final LangText COMMAND_CURRENCY_GIVE_NOTIFY = LangText.of("Command.Currency.Give.Notify",
        LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " has been added to your account!"));



    public static final LangText COMMAND_CURRENCY_GIVE_ALL_DONE = LangText.of("Command.Currency.GiveAll.Done",
        LIGHT_GRAY.wrap("Added " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " to " + LIGHT_YELLOW.wrap("All Online") + " players.")
    );



    public static final LangText COMMAND_CURRENCY_TAKE_DONE = LangText.of("Command.Currency.Take.Done",
        LIGHT_GRAY.wrap("Taken " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " from " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s balance. New balance: " + LIGHT_YELLOW.wrap(GENERIC_BALANCE) + "."));

    public static final LangText COMMAND_CURRENCY_TAKE_NOTIFY = LangText.of("Command.Currency.Take.Notify",
        LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " has been taken from your account!"));



    public static final LangText COMMAND_CURRENCY_SET_DONE = LangText.of("Command.Currency.Set.Done",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(CURRENCY_NAME) + " balance to " + LIGHT_YELLOW.wrap(GENERIC_BALANCE) + "."));

    public static final LangText COMMAND_CURRENCY_SET_NOTIFY = LangText.of("Command.Currency.Set.Notify",
        LIGHT_GRAY.wrap("Your " + LIGHT_YELLOW.wrap(CURRENCY_NAME) + " balance has been set to " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + "."));



    public static final LangText CURRENCY_SEND_ERROR_NOT_ENOUGH = LangText.of("Command.Currency.Send.Error.NotEnough",
        LIGHT_GRAY.wrap("You don't have enough " + LIGHT_RED.wrap(CURRENCY_NAME) + "!"));

    public static final LangText CURRENCY_SEND_ERROR_TOO_LOW = LangText.of("Command.Currency.Send.Error.TooLow",
        LIGHT_GRAY.wrap("You can not send smaller than " + LIGHT_RED.wrap(GENERIC_AMOUNT) + "!"));

    public static final LangText CURRENCY_SEND_ERROR_NO_PAYMENTS = LangText.of("Command.Currency.Send.Error.NoPayments",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(PLAYER_NAME) + " does not accept " + LIGHT_RED.wrap(CURRENCY_NAME) + "!"));

    public static final LangText CURRENCY_SEND_DONE_SENDER = LangText.of("Command.Currency.Send.Done.Sender",
        LIGHT_GRAY.wrap("You sent " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " to " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "!"));

    public static final LangText CURRENCY_SEND_DONE_NOTIFY = LangText.of("Command.Currency.Send.Done.Notify",
        LIGHT_GRAY.wrap("You received " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " from " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "!"));



    public static final LangText COMMAND_CURRENCY_PAYMENTS_TOGGLE = LangText.of("Command.Currency.Payments.Toggle",
        LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap(CURRENCY_NAME) + " payments acception: " + LIGHT_YELLOW.wrap(GENERIC_STATE) + "."));

    public static final LangText COMMAND_CURRENCY_PAYMENTS_TARGET = LangText.of("Command.Currency.Payments.Target",
        LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap(CURRENCY_NAME) + " payments acception for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + ": " + LIGHT_YELLOW.wrap(GENERIC_STATE) + "."));



    public static final LangText TOP_LIST = LangText.of("Command.Currency.Top.List",
        TAG_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("Top players with " + WHITE.wrap(CURRENCY_NAME)),
        " ",
        GENERIC_ENTRY,
        " ",
        GENERIC_PREVIOUS_PAGE + "  " + GRAY.wrap("Page " + WHITE.wrap(GENERIC_CURRENT) + DARK_GRAY.wrap("/") + WHITE.wrap(GENERIC_MAX)) + "  " + GENERIC_NEXT_PAGE,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final LangString TOP_ENTRY = LangString.of("Command.Currency.Top.Entry",
        YELLOW.wrap("#" + GENERIC_POS) + " " + WHITE.wrap(PLAYER_NAME) + DARK_GRAY.wrap(" ▪ ") + GREEN.wrap(GENERIC_BALANCE)
    );

    public static final LangString TOP_LIST_NEXT_PAGE_ACTIVE = LangString.of("TopList.NextPage.Active",
        HOVER.wrapShowText(
            CLICK.wrapRunCommand(
                GREEN.wrap("[→]"), "/" + CURRENCY_LABEL + " " + CurrencyCommands.DEFAULT_TOP_ALIAS + " " + GENERIC_VALUE
            ),
            GRAY.wrap("Click to get to the next page.")
        )
    );

    public static final LangString TOP_LIST_NEXT_PAGE_INACTIVE = LangString.of("TopList.NextPage.Inactive",
        HOVER.wrapShowText(GRAY.wrap("[→]"), GRAY.wrap("There are no more pages."))
    );

    public static final LangString TOP_LIST_PREVIOUS_PAGE_ACTIVE = LangString.of("TopList.PreviousPage.Active",
        HOVER.wrapShowText(
            CLICK.wrapRunCommand(
                GREEN.wrap("[←]"), "/" + CURRENCY_LABEL + " " + CurrencyCommands.DEFAULT_TOP_ALIAS + " " + GENERIC_VALUE
            ),
            GRAY.wrap("Click to get to the previous page.")
        )
    );

    public static final LangString TOP_LIST_PREVIOUS_PAGE_INACTIVE = LangString.of("TopList.PreviousPage.Inactive",
        HOVER.wrapShowText(GRAY.wrap("[←]"), GRAY.wrap("There are no more pages."))
    );



    public static final LangText CURRENCY_BALANCE_DISPLAY_OWN = LangText.of("Currency.Balance.Display.Own",
        LIGHT_GRAY.wrap("Balance: " + LIGHT_YELLOW.wrap(GENERIC_BALANCE) + "."));

    public static final LangText CURRENCY_BALANCE_DISPLAY_OTHERS = LangText.of("Currency.Balance.Display.Others",
        LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s balance: " + LIGHT_YELLOW.wrap(GENERIC_BALANCE) + "."));



    public static final LangText CURRENCY_WALLET_OWN = LangText.of("Currency.Wallet.Own",
        TAG_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + GREEN.wrap("$") + "] Your Wallet:"),
        " ",
        GENERIC_ENTRY,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final LangText CURRENCY_WALLET_OTHERS = LangText.of("Currency.Wallet.Others",
        TAG_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + GREEN.wrap("$") + "] " + WHITE.wrap(PLAYER_NAME) + "'s Wallet:"),
        " ",
        GENERIC_ENTRY,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final LangString CURRENCY_WALLET_ENTRY = LangString.of("Currency.Wallet.Entry",
        YELLOW.wrap("•") + " " + WHITE.wrap(CURRENCY_NAME + ":") + " " + GREEN.wrap(GENERIC_BALANCE)
    );


    public static final LangText CURRENCY_EXCHANGE_ERROR_DISABLED = LangText.of("Currency.Exchange.Error.Disabled",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(CURRENCY_NAME) + " can not be exchanged!"));

    public static final LangText CURRENCY_EXCHANGE_ERROR_NO_RATE = LangText.of("Currency.Exchange.Error.NoRate",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(CURRENCY_NAME) + " can not be exchanged for " + LIGHT_RED.wrap(GENERIC_NAME) + "!"));

    public static final LangText CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT = LangText.of("Currency.Exchange.Error.LowAmount",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(CURRENCY_NAME) + " amount is too low for exchange!"));

    public static final LangText CURRENCY_EXCHANGE_ERROR_LIMIT_EXCEED = LangText.of("Currency.Exchange.Error.LimitExceed",
        LIGHT_GRAY.wrap("You could get " + LIGHT_RED.wrap(GENERIC_AMOUNT) + ", but you can't hold more than " + LIGHT_RED.wrap(GENERIC_MAX) + " total!"));

    public static final LangText CURRENCY_EXCHANGE_ERROR_LOW_BALANCE = LangText.of("Currency.Exchange.Error.LowBalance",
        LIGHT_GRAY.wrap("You don't have " + LIGHT_RED.wrap(GENERIC_AMOUNT) + " for exchange!"));

    public static final LangText CURRENCY_EXCHANGE_SUCCESS = LangText.of("Currency.Exchange.Success",
        LIGHT_GRAY.wrap("You exchanged " + LIGHT_YELLOW.wrap(GENERIC_BALANCE) + " for " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + "!"));


    public static final LangString ECONOMY_ERROR_INVALID_PLAYER     = LangString.of("VaultEconomy.Error.InvalidPlayer", "Player not found.");
    public static final LangString ECONOMY_ERROR_INSUFFICIENT_FUNDS = LangString.of("VaultEconomy.Error.InsufficientFunds", "Insufficient Funds!");


    public static final LangString OTHER_NO_TOP_ENTRY = LangString.of("Other.NoTopEntry", "<none>");

    public static final LangText ERROR_COMMAND_ARGUMENT_INVALID_CURRENCY = LangText.of("Currency.Error.Invalid",
        GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid currency!"));
}
