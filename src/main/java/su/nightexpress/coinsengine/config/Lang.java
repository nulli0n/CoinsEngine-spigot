package su.nightexpress.coinsengine.config;

import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.coinsengine.Placeholders.*;

public class Lang {

    public static final LangString COMMAND_MIGRATE_DESC  = LangString.of("Command.Migrate.Desc",
        "Migrate data from other plugin(s).");

    public static final LangString COMMAND_MIGRATE_USAGE = LangString.of("Command.Migrate.Usage",
        "<plugin> <currency>");

    public static final LangText COMMAND_MIGRATE_ERROR_PLUGIN = LangText.of("Command.Migrate.Error.Plugin",
        LIGHT_GRAY.enclose("Plugin is not supported or installed!"));

    public static final LangText COMMAND_MIGRATE_START = LangText.of("Command.Migrate.Start",
        LIGHT_YELLOW.enclose("Started data migration from the " + LIGHT_ORANGE.enclose(GENERIC_NAME) + "! This may take a while."));

    public static final LangText COMMAND_MIGRATE_DONE = LangText.of("Command.Migrate.Done",
        LIGHT_YELLOW.enclose("Migrated data from the " + LIGHT_ORANGE.enclose(GENERIC_NAME) + "!"));

    public static final LangString COMMAND_RESET_DESC  = LangString.of("Command.Reset.Desc",
        "Reset player's balances.");

    public static final LangString COMMAND_RESET_USAGE = LangString.of("Command.Reset.Usage",
        "<player>");

    public static final LangText COMMAND_RESET_DONE = LangText.of("Command.Reset.Done",
        LIGHT_YELLOW.enclose("Reset all currency balances for " + LIGHT_ORANGE.enclose(PLAYER_NAME) + "!"));

    public static final LangString COMMAND_WIPE_DESC  = LangString.of("Command.Wipe.Desc",
        "Reset all currencies for all users.");

    public static final LangString COMMAND_WIPE_USAGE = LangString.of("Command.Wipe.Usage",
        "<currency>");

    public static final LangText COMMAND_WIPE_START = LangText.of("Command.Wipe.Start",
        LIGHT_YELLOW.enclose("Started currency data wipe for " + LIGHT_ORANGE.enclose(CURRENCY_NAME) + ". This may take a while..."));

    public static final LangText COMMAND_WIPE_FINISH = LangText.of("Command.Wipe.Finish",
        LIGHT_YELLOW.enclose("Finished currency data wipe for " + LIGHT_ORANGE.enclose(CURRENCY_NAME) + "."));

    public static final LangString COMMAND_CURRENCY_BALANCE_USAGE = LangString.of("Command.Currency.Balance.Usage",
        "[player]");

    public static final LangString COMMAND_CURRENCY_BALANCE_DESC  = LangString.of("Command.Currency.Balance.Desc",
        "Check [player's] currency balance.");

    public static final LangString COMMAND_CURRENCY_GIVE_USAGE = LangString.of("Command.Currency.Give.Usage",
        "<player> <amount> [-s]");

    public static final LangString COMMAND_CURRENCY_GIVE_DESC  = LangString.of("Command.Currency.Give.Desc",
        "Add currency to a player.");

    public static final LangText COMMAND_CURRENCY_GIVE_DONE = LangText.of("Command.Currency.Give.Done",
        LIGHT_YELLOW.enclose("Added " + LIGHT_ORANGE.enclose(GENERIC_AMOUNT) + " to " + LIGHT_ORANGE.enclose(PLAYER_NAME) + "'s balance. New balance: " + LIGHT_ORANGE.enclose(GENERIC_BALANCE) + "."));

    public static final LangText COMMAND_CURRENCY_GIVE_NOTIFY = LangText.of("Command.Currency.Give.Notify",
        LIGHT_YELLOW.enclose(LIGHT_ORANGE.enclose(GENERIC_AMOUNT) + " has been added to your account!"));

    public static final LangString COMMAND_CURRENCY_TAKE_USAGE = LangString.of("Command.Currency.Take.Usage",
        "<player> <amount> [-s]");

    public static final LangString COMMAND_CURRENCY_TAKE_DESC  = LangString.of("Command.Currency.Take.Desc",
        "Take player's currency.");

    public static final LangText COMMAND_CURRENCY_TAKE_DONE = LangText.of("Command.Currency.Take.Done",
        LIGHT_YELLOW.enclose("Taken " + LIGHT_ORANGE.enclose(GENERIC_AMOUNT) + " from " + LIGHT_ORANGE.enclose(PLAYER_NAME) + "'s balance. New balance: " + LIGHT_ORANGE.enclose(GENERIC_BALANCE) + "."));

    public static final LangText COMMAND_CURRENCY_TAKE_NOTIFY = LangText.of("Command.Currency.Take.Notify",
        LIGHT_YELLOW.enclose(LIGHT_ORANGE.enclose(GENERIC_AMOUNT) + " has been taken from your account!"));

    public static final LangString COMMAND_CURRENCY_SET_USAGE = LangString.of("Command.Currency.Set.Usage",
        "<player> <amount> [-s]");

    public static final LangString COMMAND_CURRENCY_SET_DESC  = LangString.of("Command.Currency.Set.Desc",
        "Set player's currency balance.");

    public static final LangText COMMAND_CURRENCY_SET_DONE = LangText.of("Command.Currency.Set.Done",
        LIGHT_YELLOW.enclose("Set " + LIGHT_ORANGE.enclose(PLAYER_NAME) + "'s " + LIGHT_ORANGE.enclose(CURRENCY_NAME) + " balance to " + LIGHT_ORANGE.enclose(GENERIC_BALANCE) + "."));

    public static final LangText COMMAND_CURRENCY_SET_NOTIFY = LangText.of("Command.Currency.Set.Notify",
        LIGHT_YELLOW.enclose("Your " + LIGHT_ORANGE.enclose(CURRENCY_NAME) + " balance has been set to " + LIGHT_ORANGE.enclose(GENERIC_AMOUNT) + "."));

    public static final LangString COMMAND_CURRENCY_SEND_USAGE = LangString.of("Command.Currency.Send.Usage",
        "<player> <amount>");

    public static final LangString COMMAND_CURRENCY_SEND_DESC  = LangString.of("Command.Currency.Send.Desc",
        "Transfer currency to a player.");

    public static final LangText COMMAND_CURRENCY_SEND_ERROR_NOT_ENOUGH = LangText.of("Command.Currency.Send.Error.NotEnough",
        LIGHT_YELLOW.enclose("You don't have enough " + LIGHT_RED.enclose(CURRENCY_NAME) + "!"));

    public static final LangText COMMAND_CURRENCY_SEND_ERROR_TOO_LOW = LangText.of("Command.Currency.Send.Error.TooLow",
        LIGHT_YELLOW.enclose("You can not send smaller than " + LIGHT_RED.enclose(GENERIC_AMOUNT) + "!"));

    public static final LangText COMMAND_CURRENCY_SEND_ERROR_NO_PAYMENTS = LangText.of("Command.Currency.Send.Error.NoPayments",
        LIGHT_YELLOW.enclose(LIGHT_RED.enclose(PLAYER_NAME) + " does not accept " + LIGHT_RED.enclose(CURRENCY_NAME) + "!"));

    public static final LangText COMMAND_CURRENCY_SEND_DONE_SENDER = LangText.of("Command.Currency.Send.Done.Sender",
        LIGHT_YELLOW.enclose("You sent " + LIGHT_ORANGE.enclose(GENERIC_AMOUNT) + " to " + LIGHT_ORANGE.enclose(PLAYER_NAME) + "!"));

    public static final LangText COMMAND_CURRENCY_SEND_DONE_NOTIFY = LangText.of("Command.Currency.Send.Done.Notify",
        LIGHT_YELLOW.enclose("You received " + LIGHT_ORANGE.enclose(GENERIC_AMOUNT) + " from " + LIGHT_ORANGE.enclose(PLAYER_NAME) + "!"));

    public static final LangString COMMAND_CURRENCY_PAYMENTS_USAGE = LangString.of("Command.Currency.Payments.Usage",
        "[player] [-s]");

    public static final LangString COMMAND_CURRENCY_PAYMENTS_DESC  = LangString.of("Command.Currency.Payments.Desc",
        "Toggle payments acception from other players.");

    public static final LangText COMMAND_CURRENCY_PAYMENTS_TOGGLE = LangText.of("Command.Currency.Payments.Toggle",
        LIGHT_YELLOW.enclose(LIGHT_ORANGE.enclose(CURRENCY_NAME) + " payments acception: " + LIGHT_ORANGE.enclose(GENERIC_STATE) + "."));

    public static final LangText COMMAND_CURRENCY_PAYMENTS_TARGET = LangText.of("Command.Currency.Payments.Target",
        LIGHT_YELLOW.enclose(LIGHT_ORANGE.enclose(CURRENCY_NAME) + " payments acception for " + LIGHT_ORANGE.enclose(PLAYER_NAME) + ": " + LIGHT_ORANGE.enclose(GENERIC_STATE) + "."));

    public static final LangString COMMAND_CURRENCY_EXCHANGE_USAGE = LangString.of("Command.Currency.Exchange.Usage",
        "<currency> <amount>");

    public static final LangString COMMAND_CURRENCY_EXCHANGE_DESC  = LangString.of("Command.Currency.Exchange.Desc",
        "Exchange currency.");

    public static final LangString COMMAND_CURRENCY_TOP_USAGE = LangString.of("Command.Currency.Top.Usage",
        "[page]");

    public static final LangString COMMAND_CURRENCY_TOP_DESC  = LangString.of("Command.Currency.Top.Desc",
        "List of players with the most balance.");

    public static final LangText COMMAND_CURRENCY_TOP_LIST = LangText.of("Command.Currency.Top.List",
        TAG_NO_PREFIX,
        CYAN.enclose(BOLD.enclose(CURRENCY_NAME + " Top:")),
        " ",
        GENERIC_ENTRY,
        " ",
        "\n" + LIGHT_GRAY.enclose("Page " + CYAN.enclose(GENERIC_CURRENT) + " of " + CYAN.enclose(GENERIC_MAX) + "."),
        " "
    );

    public static final LangString COMMAND_CURRENCY_TOP_ENTRY = LangString.of("Command.Currency.Top.Entry",
        CYAN.enclose(GENERIC_POS + ". " + GRAY.enclose(PLAYER_NAME + ": ") + GENERIC_BALANCE)
    );

    public static final LangText CURRENCY_BALANCE_DISPLAY_OWN = LangText.of("Currency.Balance.Display.Own",
        LIGHT_YELLOW.enclose("Balance: " + LIGHT_ORANGE.enclose(GENERIC_BALANCE) + "."));

    public static final LangText CURRENCY_BALANCE_DISPLAY_OTHERS = LangText.of("Currency.Balance.Display.Others",
        LIGHT_YELLOW.enclose(LIGHT_ORANGE.enclose(PLAYER_NAME) + "'s balance: " + LIGHT_ORANGE.enclose(GENERIC_BALANCE) + "."));

    public static final LangText CURRENCY_EXCHANGE_ERROR_DISABLED = LangText.of("Currency.Exchange.Error.Disabled",
        LIGHT_YELLOW.enclose(LIGHT_RED.enclose(CURRENCY_NAME) + " can not be exchanged!"));

    public static final LangText CURRENCY_EXCHANGE_ERROR_NO_RATE = LangText.of("Currency.Exchange.Error.NoRate",
        LIGHT_YELLOW.enclose(LIGHT_RED.enclose(CURRENCY_NAME) + " can not be exchanged for " + LIGHT_RED.enclose(GENERIC_NAME) + "!"));

    public static final LangText CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT = LangText.of("Currency.Exchange.Error.LowAmount",
        LIGHT_YELLOW.enclose(LIGHT_RED.enclose(CURRENCY_NAME) + " amount is too low for exchange!"));

    public static final LangText CURRENCY_EXCHANGE_ERROR_LOW_BALANCE = LangText.of("Currency.Exchange.Error.LowBalance",
        LIGHT_YELLOW.enclose("You don't have " + LIGHT_RED.enclose(GENERIC_AMOUNT) + " for exchange!"));

    public static final LangText CURRENCY_EXCHANGE_SUCCESS = LangText.of("Currency.Exchange.Success",
        LIGHT_YELLOW.enclose("You exchanged " + LIGHT_ORANGE.enclose(GENERIC_BALANCE) + " for " + LIGHT_ORANGE.enclose(GENERIC_AMOUNT) + "!"));

    public static final LangText CURRENCY_ERROR_INVALID = LangText.of("Currency.Error.Invalid",
        LIGHT_RED.enclose("Invalid currency!"));
}
