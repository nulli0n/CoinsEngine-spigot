package su.nightexpress.coinsengine.config;

import su.nightexpress.coinsengine.command.CommandNames;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.locale.message.MessageData;

import static su.nightexpress.coinsengine.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class Lang implements LangContainer {

    public static final TextLocale COMMAND_ARGUMENT_NAME_CURRENCY = LangEntry.builder("Command.Argument.Name.Currency").text("currency");
    public static final TextLocale COMMAND_ARGUMENT_NAME_PAGE     = LangEntry.builder("Command.Argument.Name.Page").text("page");
    public static final TextLocale COMMAND_ARGUMENT_NAME_PLUGIN   = LangEntry.builder("Command.Argument.Name.Plugin").text("plugin");
    public static final TextLocale COMMAND_ARGUMENT_NAME_SYMBOL   = LangEntry.builder("Command.Argument.Name.Symbol").text("symbol");
    public static final TextLocale COMMAND_ARGUMENT_NAME_DECIMAL  = LangEntry.builder("Command.Argument.Name.Decimals").text("allowDecimals");

    public static final TextLocale COMMAND_CREATE_DESC    = LangEntry.builder("Command.Create.Desc").text("Create a new currency.");
    public static final TextLocale COMMAND_MIGRATE_DESC   = LangEntry.builder("Command.Migrate.Desc").text("Migrate data from other plugin(s).");
    public static final TextLocale COMMAND_RESET_DESC     = LangEntry.builder("Command.Reset.Desc").text("Reset player's balances.");
    public static final TextLocale COMMAND_RESET_ALL_DESC = LangEntry.builder("Command.ResetAll.Desc").text("Reset balances of all users.");
    public static final TextLocale COMMAND_WALLET_DESC    = LangEntry.builder("Command.Wallet.Desc").text("View full balance.");

    public static final TextLocale COMMAND_CURRENCY_ROOT_DESC     = LangEntry.builder("Command.Currency.Root.Desc").text(CURRENCY_NAME + " commands.");
    public static final TextLocale COMMAND_CURRENCY_BALANCE_DESC  = LangEntry.builder("Command.Currency.Balance.Desc").text("View balance.");
    public static final TextLocale COMMAND_CURRENCY_GIVE_DESC     = LangEntry.builder("Command.Currency.Give.Desc").text("Add currency to a player.");
    public static final TextLocale COMMAND_CURRENCY_GIVE_ALL_DESC = LangEntry.builder("Command.Currency.GiveAll.Desc").text("Add currency to all online players.");
    public static final TextLocale COMMAND_CURRENCY_TAKE_DESC     = LangEntry.builder("Command.Currency.Take.Desc").text("Take player's currency.");
    public static final TextLocale COMMAND_CURRENCY_SET_DESC      = LangEntry.builder("Command.Currency.Set.Desc").text("Set player's currency balance.");
    public static final TextLocale COMMAND_CURRENCY_SEND_DESC     = LangEntry.builder("Command.Currency.Send.Desc").text("Transfer currency to a player.");
    public static final TextLocale COMMAND_CURRENCY_PAYMENTS_DESC = LangEntry.builder("Command.Currency.Payments.Desc").text("Toggle payments acception from other players.");
    public static final TextLocale COMMAND_CURRENCY_EXCHANGE_DESC = LangEntry.builder("Command.Currency.Exchange.Desc").text("Exchange currency.");
    public static final TextLocale COMMAND_CURRENCY_TOP_DESC      = LangEntry.builder("Command.Currency.Top.Desc").text("List of players with the most balance.");

    public static final MessageLocale COMMAND_SYNTAX_INVALID_CURRENCY = LangEntry.builder("Command.Syntax.InvalidCurrency").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid currency!"));



    public static final MessageLocale CURRENCY_OPERATION_DISABLED = LangEntry.builder("Currency.Operation.Disabled").chatMessage(
        SOFT_RED.wrap("Currency operations are temporarily disabled.")
    );

    public static final MessageLocale CURRENCY_OPERATION_RESET_FEEDBACK = LangEntry.builder("Currency.Operation.Reset.Feedback").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + "'s balance has been reset to " + SOFT_YELLOW.wrap(GENERIC_BALANCE) + "."));

    public static final MessageLocale CURRENCY_OPERATION_RESET_NOTIFY = LangEntry.builder("Currency.Operation.Reset.Notify").chatMessage(
        GRAY.wrap("Your balance has been reset to " + SOFT_YELLOW.wrap(GENERIC_BALANCE) + "."));



    public static final MessageLocale MIGRATION_START_BLOCKED = LangEntry.builder("Migration.Start.Blocked").chatMessage(
        SOFT_RED.wrap("Could not start migration due to background tasks already running!"));

    public static final MessageLocale MIGRATION_START_BAD_PLUGIN = LangEntry.builder("Migration.Start.BadPlugin").chatMessage(
        SOFT_RED.wrap("Plugin is not supported or installed!"));

    public static final MessageLocale MIGRATION_START_BAD_CURRENCY = LangEntry.builder("Migration.Start.BadCurrency").chatMessage(
        SOFT_RED.wrap("Could not migrate data from " + GENERIC_NAME + " to " + CURRENCY_NAME + "."));

    public static final MessageLocale MIGRATION_STARTED = LangEntry.builder("Migration.Started").message(
        MessageData.CHAT_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + RED.wrap("→") + "] Data Migration:"),
        " ",
        GRAY.wrap("Started data migration from " + WHITE.wrap(GENERIC_NAME) + ". Please wait..."),
        " ",
        RED.wrap(UNDERLINED.wrap("All currency operations are disabled until migration is completed.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final MessageLocale MIGRATION_COMPLETED = LangEntry.builder("Migration.Completed").message(
        MessageData.CHAT_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + GREEN.wrap("→") + "] Data Migration:"),
        " ",
        GRAY.wrap("Finished data migration from " + WHITE.wrap(GENERIC_NAME) + "!"),
        " ",
        GREEN.wrap(UNDERLINED.wrap("All currency operations are enabled again.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );



    public static final MessageLocale RESET_ALL_START_BLOCKED = LangEntry.builder("ResetAll.Start.Blocked").chatMessage(
        SOFT_RED.wrap("Could not start balance reset due to background tasks already running!"));

    public static final MessageLocale RESET_ALL_STARTED_CURRENCY = LangEntry.builder("ResetAll.Started.Currency").message(
        MessageData.CHAT_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + RED.wrap("$") + "] Balance Reset:"),
        " ",
        GRAY.wrap("Started " + WHITE.wrap(CURRENCY_NAME) + " balance reset for all players. Please wait..."),
        " ",
        RED.wrap(UNDERLINED.wrap("All currency operations are disabled until reset is completed.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final MessageLocale RESET_ALL_COMPLETED_CURRENCY = LangEntry.builder("ResetAll.Completed.Currency").message(
        MessageData.CHAT_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + GREEN.wrap("$") + "] Balance Reset:"),
        " ",
        GRAY.wrap("Finished " + WHITE.wrap(CURRENCY_NAME) + " balance reset for all players!"),
        " ",
        GREEN.wrap(UNDERLINED.wrap("All currency operations are enabled again.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final MessageLocale RESET_ALL_STARTED_GLOBAL = LangEntry.builder("ResetAll.Started.Global").message(
        MessageData.CHAT_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + RED.wrap("$") + "] Balance Reset:"),
        " ",
        GRAY.wrap("Started balance reset for all currencies for all players. Please wait..."),
        " ",
        RED.wrap(UNDERLINED.wrap("All currency operations are disabled until reset is completed.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final MessageLocale RESET_ALL_COMPLETED_GLOBAL = LangEntry.builder("ResetAll.Completed.Global").message(
        MessageData.CHAT_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + GREEN.wrap("$") + "] Balance Reset:"),
        " ",
        GRAY.wrap("Finished balance reset for all currencies for all players!"),
        " ",
        GREEN.wrap(UNDERLINED.wrap("All currency operations are enabled again.")),
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );


    public static final MessageLocale CURRENCY_CREATE_BAD_NAME = LangEntry.builder("Currency.Create.BadName").chatMessage(
        SOFT_RED.wrap("Invalid name provided! Only latin letters and digits are supported.")
    );

    public static final MessageLocale CURRENCY_CREATE_DUPLICATED = LangEntry.builder("Currency.Create.Duplicated").chatMessage(
        SOFT_RED.wrap("There is already a currency with this name.")
    );

    public static final MessageLocale CURRENCY_CREATE_SUCCESS = LangEntry.builder("Currency.Create.Success").chatMessage(
        GRAY.wrap("Created new currency: " + GREEN.wrap(CURRENCY_NAME) + " (ID: " + WHITE.wrap(CURRENCY_ID) + ")")
    );


    public static final MessageLocale COMMAND_CURRENCY_GIVE_DONE = LangEntry.builder("Command.Currency.Give.Done").chatMessage(
        GRAY.wrap("Added " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s balance. New balance: " + SOFT_YELLOW.wrap(GENERIC_BALANCE) + "."));

    public static final MessageLocale COMMAND_CURRENCY_GIVE_NOTIFY = LangEntry.builder("Command.Currency.Give.Notify").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " has been added to your account!"));


    public static final MessageLocale COMMAND_CURRENCY_GIVE_ALL_DONE = LangEntry.builder("Command.Currency.GiveAll.Done").chatMessage(
        GRAY.wrap("Added " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " to " + SOFT_YELLOW.wrap("All Online") + " players.")
    );


    public static final MessageLocale COMMAND_CURRENCY_TAKE_DONE = LangEntry.builder("Command.Currency.Take.Done").chatMessage(
        GRAY.wrap("Taken " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " from " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s balance. New balance: " + SOFT_YELLOW.wrap(GENERIC_BALANCE) + "."));

    public static final MessageLocale COMMAND_CURRENCY_TAKE_NOTIFY = LangEntry.builder("Command.Currency.Take.Notify").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " has been taken from your account!"));


    public static final MessageLocale COMMAND_CURRENCY_SET_DONE = LangEntry.builder("Command.Currency.Set.Done").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(CURRENCY_NAME) + " balance to " + SOFT_YELLOW.wrap(GENERIC_BALANCE) + "."));

    public static final MessageLocale COMMAND_CURRENCY_SET_NOTIFY = LangEntry.builder("Command.Currency.Set.Notify").chatMessage(
        GRAY.wrap("Your " + SOFT_YELLOW.wrap(CURRENCY_NAME) + " balance has been set to " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + "."));


    public static final MessageLocale CURRENCY_SEND_ERROR_NOT_ENOUGH = LangEntry.builder("Command.Currency.Send.Error.NotEnough").chatMessage(
        GRAY.wrap("You don't have enough " + SOFT_RED.wrap(CURRENCY_NAME) + "!"));

    public static final MessageLocale CURRENCY_SEND_ERROR_TOO_LOW = LangEntry.builder("Command.Currency.Send.Error.TooLow").chatMessage(
        GRAY.wrap("You can not send smaller than " + SOFT_RED.wrap(GENERIC_AMOUNT) + "!"));

    public static final MessageLocale CURRENCY_SEND_ERROR_NO_PAYMENTS = LangEntry.builder("Command.Currency.Send.Error.NoPayments").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(PLAYER_NAME) + " does not accept " + SOFT_RED.wrap(CURRENCY_NAME) + "!"));

    public static final MessageLocale CURRENCY_SEND_DONE_SENDER = LangEntry.builder("Command.Currency.Send.Done.Sender").chatMessage(
        GRAY.wrap("You sent " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "!"));

    public static final MessageLocale CURRENCY_SEND_DONE_NOTIFY = LangEntry.builder("Command.Currency.Send.Done.Notify").chatMessage(
        GRAY.wrap("You received " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " from " + SOFT_YELLOW.wrap(PLAYER_NAME) + "!"));


    public static final MessageLocale COMMAND_CURRENCY_PAYMENTS_TOGGLE = LangEntry.builder("Command.Currency.Payments.Toggle").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(CURRENCY_NAME) + " payments acception: " + SOFT_YELLOW.wrap(GENERIC_STATE) + "."));

    public static final MessageLocale COMMAND_CURRENCY_PAYMENTS_TARGET = LangEntry.builder("Command.Currency.Payments.Target").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(CURRENCY_NAME) + " payments acception for " + SOFT_YELLOW.wrap(PLAYER_NAME) + ": " + SOFT_YELLOW.wrap(GENERIC_STATE) + "."));


    public static final MessageLocale TOP_LIST = LangEntry.builder("Command.Currency.Top.List").message(
        MessageData.CHAT_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("Top players with " + WHITE.wrap(CURRENCY_NAME)),
        " ",
        GENERIC_ENTRY,
        " ",
        GENERIC_PREVIOUS_PAGE + "  " + GRAY.wrap("Page " + WHITE.wrap(GENERIC_CURRENT) + DARK_GRAY.wrap("/") + WHITE.wrap(GENERIC_MAX)) + "  " + GENERIC_NEXT_PAGE,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final TextLocale TOP_ENTRY = LangEntry.builder("Command.Currency.Top.Entry").text(
        YELLOW.wrap("#" + GENERIC_POS) + " " + WHITE.wrap(PLAYER_NAME) + DARK_GRAY.wrap(" ▪ ") + GREEN.wrap(GENERIC_BALANCE)
    );

    public static final TextLocale TOP_LIST_NEXT_PAGE_ACTIVE = LangEntry.builder("TopList.NextPage.Active").text(
        SHOW_TEXT.with(GRAY.wrap("Click to get to the next page.")).wrap(
            RUN_COMMAND.with("/" + CURRENCY_LABEL + " " + CommandNames.CURRENCY_TOP + " " + GENERIC_VALUE).wrap(
                GREEN.wrap("[→]")
            )
        )
    );

    public static final TextLocale TOP_LIST_NEXT_PAGE_INACTIVE = LangEntry.builder("TopList.NextPage.Inactive").text(
        SHOW_TEXT.with(GRAY.wrap("There are no more pages.")).wrap(GRAY.wrap("[→]"))
    );

    public static final TextLocale TOP_LIST_PREVIOUS_PAGE_ACTIVE = LangEntry.builder("TopList.PreviousPage.Active").text(
        SHOW_TEXT.with(GRAY.wrap("Click to get to the previous page.")).wrap(
            RUN_COMMAND.with("/" + CURRENCY_LABEL + " " + CommandNames.CURRENCY_TOP + " " + GENERIC_VALUE).wrap(
                GREEN.wrap("[←]")
            )
        )
    );

    public static final TextLocale TOP_LIST_PREVIOUS_PAGE_INACTIVE = LangEntry.builder("TopList.PreviousPage.Inactive").text(
        SHOW_TEXT.with(GRAY.wrap("There are no more pages.")).wrap(GRAY.wrap("[←]"))
    );


    public static final MessageLocale CURRENCY_BALANCE_DISPLAY_OWN = LangEntry.builder("Currency.Balance.Display.Own").chatMessage(
        GRAY.wrap("Balance: " + SOFT_YELLOW.wrap(GENERIC_BALANCE) + "."));

    public static final MessageLocale CURRENCY_BALANCE_DISPLAY_OTHERS = LangEntry.builder("Currency.Balance.Display.Others").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + "'s balance: " + SOFT_YELLOW.wrap(GENERIC_BALANCE) + "."));


    public static final MessageLocale CURRENCY_WALLET_OWN = LangEntry.builder("Currency.Wallet.Own").message(
        MessageData.CHAT_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + GREEN.wrap("$") + "] Your Wallet:"),
        " ",
        GENERIC_ENTRY,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final MessageLocale CURRENCY_WALLET_OTHERS = LangEntry.builder("Currency.Wallet.Others").message(
        MessageData.CHAT_NO_PREFIX,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32))),
        GRAY.wrap("[" + GREEN.wrap("$") + "] " + WHITE.wrap(PLAYER_NAME) + "'s Wallet:"),
        " ",
        GENERIC_ENTRY,
        DARK_GRAY.wrap(STRIKETHROUGH.wrap("-".repeat(32)))
    );

    public static final TextLocale CURRENCY_WALLET_ENTRY = LangEntry.builder("Currency.Wallet.Entry").text(
        YELLOW.wrap("•") + " " + WHITE.wrap(CURRENCY_NAME + ":") + " " + GREEN.wrap(GENERIC_BALANCE)
    );


    public static final MessageLocale CURRENCY_EXCHANGE_ERROR_DISABLED = LangEntry.builder("Currency.Exchange.Error.Disabled").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(CURRENCY_NAME) + " can not be exchanged!"));

    public static final MessageLocale CURRENCY_EXCHANGE_ERROR_NO_RATE = LangEntry.builder("Currency.Exchange.Error.NoRate").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(CURRENCY_NAME) + " can not be exchanged for " + SOFT_RED.wrap(GENERIC_NAME) + "!"));

    public static final MessageLocale CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT = LangEntry.builder("Currency.Exchange.Error.LowAmount").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(CURRENCY_NAME) + " amount is too low for exchange!"));

    public static final MessageLocale CURRENCY_EXCHANGE_ERROR_LIMIT_EXCEED = LangEntry.builder("Currency.Exchange.Error.LimitExceed").chatMessage(
        GRAY.wrap("You could get " + SOFT_RED.wrap(GENERIC_AMOUNT) + ", but you can't hold more than " + SOFT_RED.wrap(GENERIC_MAX) + " total!"));

    public static final MessageLocale CURRENCY_EXCHANGE_ERROR_LOW_BALANCE = LangEntry.builder("Currency.Exchange.Error.LowBalance").chatMessage(
        GRAY.wrap("You don't have " + SOFT_RED.wrap(GENERIC_AMOUNT) + " for exchange!"));

    public static final MessageLocale CURRENCY_EXCHANGE_SUCCESS = LangEntry.builder("Currency.Exchange.Success").chatMessage(
        GRAY.wrap("You exchanged " + SOFT_YELLOW.wrap(GENERIC_BALANCE) + " for " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + "!"));


    public static final TextLocale ECONOMY_ERROR_INVALID_PLAYER     = LangEntry.builder("VaultEconomy.Error.InvalidPlayer").text("Player not found.");
    public static final TextLocale ECONOMY_ERROR_INSUFFICIENT_FUNDS = LangEntry.builder("VaultEconomy.Error.InsufficientFunds").text("Insufficient Funds!");


    public static final TextLocale OTHER_NO_TOP_ENTRY = LangEntry.builder("Other.NoTopEntry").text("<none>");
}
