package su.nightexpress.coinsengine.config;

import su.nightexpress.nightcore.language.legacy.LegacyLangString;
import su.nightexpress.nightcore.language.legacy.LegacyLangText;

import static su.nightexpress.nightcore.util.Colors.*;
import static su.nightexpress.coinsengine.Placeholders.*;

public class Lang {

    public static final LegacyLangString COMMAND_MIGRATE_DESC         = LegacyLangString.of("Command.Migrate.Desc", "Migrate data from other plugin(s).");
    public static final LegacyLangString COMMAND_MIGRATE_USAGE        = LegacyLangString.of("Command.Migrate.Usage", "<plugin> <currency>");
    public static final LegacyLangText   COMMAND_MIGRATE_ERROR_PLUGIN = LegacyLangText.of("Command.Migrate.Error.Plugin", RED + "Plugin is not supported or installed!");
    public static final LegacyLangText   COMMAND_MIGRATE_START        = LegacyLangText.of("Command.Migrate.Start", LIGHT_YELLOW + "Started data migration from the " + LIGHT_ORANGE + GENERIC_NAME + LIGHT_YELLOW + "! This may take a while.");
    public static final LegacyLangText   COMMAND_MIGRATE_DONE         = LegacyLangText.of("Command.Migrate.Done", LIGHT_YELLOW + "Migrated data from the " + LIGHT_ORANGE + GENERIC_NAME + LIGHT_YELLOW + "!");

    public static final LegacyLangString COMMAND_RESET_DESC  = LegacyLangString.of("Command.Reset.Desc", "Reset player's balances.");
    public static final LegacyLangString COMMAND_RESET_USAGE = LegacyLangString.of("Command.Reset.Usage", "<player>");
    public static final LegacyLangText   COMMAND_RESET_DONE  = LegacyLangText.of("Command.Reset.Done", LIGHT_YELLOW + "Reset all currency balances for " + LIGHT_ORANGE + PLAYER_NAME + LIGHT_YELLOW + "!");

    public static final LegacyLangString COMMAND_WIPE_DESC   = LegacyLangString.of("Command.Wipe.Desc", "Reset all currencies for all users.");
    public static final LegacyLangString COMMAND_WIPE_USAGE  = LegacyLangString.of("Command.Wipe.Usage", "<currency>");
    public static final LegacyLangText   COMMAND_WIPE_START  = LegacyLangText.of("Command.Wipe.Start", LIGHT_YELLOW + "Started currency data wipe for " + LIGHT_ORANGE + CURRENCY_NAME + LIGHT_YELLOW + ". This may take a while...");
    public static final LegacyLangText   COMMAND_WIPE_FINISH = LegacyLangText.of("Command.Wipe.Finish", LIGHT_YELLOW + "Finished currency data wipe for " + LIGHT_ORANGE + CURRENCY_NAME + LIGHT_YELLOW + ".");

    public static final LegacyLangString COMMAND_CURRENCY_BALANCE_USAGE = LegacyLangString.of("Command.Currency.Balance.Usage", "[player]");
    public static final LegacyLangString COMMAND_CURRENCY_BALANCE_DESC  = LegacyLangString.of("Command.Currency.Balance.Desc", "Check [player's] currency balance.");

    public static final LegacyLangString COMMAND_CURRENCY_GIVE_USAGE  = LegacyLangString.of("Command.Currency.Give.Usage", "<player> <amount> [-s]");
    public static final LegacyLangString COMMAND_CURRENCY_GIVE_DESC   = LegacyLangString.of("Command.Currency.Give.Desc", "Add currency to a player.");
    public static final LegacyLangText   COMMAND_CURRENCY_GIVE_DONE   = LegacyLangText.of("Command.Currency.Give.Done", LIGHT_YELLOW + "Added " + LIGHT_ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " to " + LIGHT_ORANGE + PLAYER_NAME + LIGHT_YELLOW + "'s balance. New balance: " + LIGHT_ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + ".");
    public static final LegacyLangText   COMMAND_CURRENCY_GIVE_NOTIFY = LegacyLangText.of("Command.Currency.Give.Notify", LIGHT_ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " has been added to your account!");

    public static final LegacyLangString COMMAND_CURRENCY_TAKE_USAGE  = LegacyLangString.of("Command.Currency.Take.Usage", "<player> <amount> [-s]");
    public static final LegacyLangString COMMAND_CURRENCY_TAKE_DESC   = LegacyLangString.of("Command.Currency.Take.Desc", "Take player's currency.");
    public static final LegacyLangText   COMMAND_CURRENCY_TAKE_DONE   = LegacyLangText.of("Command.Currency.Take.Done", LIGHT_YELLOW + "Taken " + LIGHT_ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " from " + LIGHT_ORANGE + PLAYER_NAME + LIGHT_YELLOW + "'s balance. New balance: " + LIGHT_ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + ".");
    public static final LegacyLangText   COMMAND_CURRENCY_TAKE_NOTIFY = LegacyLangText.of("Command.Currency.Take.Notify", LIGHT_ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " has been taken from your account!");

    public static final LegacyLangString COMMAND_CURRENCY_SET_USAGE  = LegacyLangString.of("Command.Currency.Set.Usage", "<player> <amount> [-s]");
    public static final LegacyLangString COMMAND_CURRENCY_SET_DESC   = LegacyLangString.of("Command.Currency.Set.Desc", "Set player's currency balance.");
    public static final LegacyLangText   COMMAND_CURRENCY_SET_DONE   = LegacyLangText.of("Command.Currency.Set.Done", LIGHT_YELLOW + "Set " + LIGHT_ORANGE + PLAYER_NAME + LIGHT_YELLOW + "'s " + CURRENCY_NAME + " balance to " + LIGHT_ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + ".");
    public static final LegacyLangText   COMMAND_CURRENCY_SET_NOTIFY = LegacyLangText.of("Command.Currency.Set.Notify", LIGHT_YELLOW + "Your " + LIGHT_ORANGE + CURRENCY_NAME + LIGHT_YELLOW + " balance has been set to " + LIGHT_ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + ".");

    public static final LegacyLangString COMMAND_CURRENCY_SEND_USAGE             = LegacyLangString.of("Command.Currency.Send.Usage", "<player> <amount>");
    public static final LegacyLangString COMMAND_CURRENCY_SEND_DESC              = LegacyLangString.of("Command.Currency.Send.Desc", "Transfer currency to a player.");
    public static final LegacyLangText   COMMAND_CURRENCY_SEND_ERROR_NOT_ENOUGH  = LegacyLangText.of("Command.Currency.Send.Error.NotEnough", LIGHT_YELLOW + "You don't have enought " + RED + CURRENCY_NAME + LIGHT_YELLOW + "!");
    public static final LegacyLangText   COMMAND_CURRENCY_SEND_ERROR_TOO_LOW     = LegacyLangText.of("Command.Currency.Send.Error.TooLow", LIGHT_YELLOW + "You can not send smaller than " + RED + GENERIC_AMOUNT + LIGHT_YELLOW + "!");
    public static final LegacyLangText   COMMAND_CURRENCY_SEND_ERROR_NO_PAYMENTS = LegacyLangText.of("Command.Currency.Send.Error.NoPayments", RED + PLAYER_NAME + LIGHT_YELLOW + " does not accept " + RED + CURRENCY_NAME + LIGHT_YELLOW + "!");
    public static final LegacyLangText   COMMAND_CURRENCY_SEND_DONE_SENDER       = LegacyLangText.of("Command.Currency.Send.Done.Sender", LIGHT_YELLOW + "You sent " + LIGHT_ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " to " + LIGHT_ORANGE + PLAYER_NAME + LIGHT_YELLOW + "!");
    public static final LegacyLangText   COMMAND_CURRENCY_SEND_DONE_NOTIFY       = LegacyLangText.of("Command.Currency.Send.Done.Notify", LIGHT_YELLOW + "You received " + LIGHT_ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " from " + LIGHT_ORANGE + PLAYER_NAME + LIGHT_YELLOW + "!");

    public static final LegacyLangString COMMAND_CURRENCY_PAYMENTS_USAGE  = LegacyLangString.of("Command.Currency.Payments.Usage", "[player] [-s]");
    public static final LegacyLangString COMMAND_CURRENCY_PAYMENTS_DESC   = LegacyLangString.of("Command.Currency.Payments.Desc", "Toggle payments acception from other players.");
    public static final LegacyLangText   COMMAND_CURRENCY_PAYMENTS_TOGGLE = LegacyLangText.of("Command.Currency.Payments.Toggle", LIGHT_ORANGE + CURRENCY_NAME + LIGHT_YELLOW + " payments acception: " + LIGHT_ORANGE + GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LegacyLangText   COMMAND_CURRENCY_PAYMENTS_TARGET = LegacyLangText.of("Command.Currency.Payments.Target", LIGHT_ORANGE + CURRENCY_NAME + LIGHT_YELLOW + " payments acception for " + LIGHT_ORANGE + PLAYER_NAME + LIGHT_YELLOW + ": " + LIGHT_ORANGE + GENERIC_STATE + LIGHT_YELLOW + ".");

    public static final LegacyLangString COMMAND_CURRENCY_EXCHANGE_USAGE = LegacyLangString.of("Command.Currency.Exchange.Usage", "<currency> <amount>");
    public static final LegacyLangString COMMAND_CURRENCY_EXCHANGE_DESC  = LegacyLangString.of("Command.Currency.Exchange.Desc", "Exchange currency.");

    public static final LegacyLangString COMMAND_CURRENCY_TOP_USAGE = LegacyLangString.of("Command.Currency.Top.Usage", "[page]");
    public static final LegacyLangString COMMAND_CURRENCY_TOP_DESC = LegacyLangString.of("Command.Currency.Top.Desc", "List of players with the most balance.");
    public static final LegacyLangText   COMMAND_CURRENCY_TOP_LIST = LegacyLangText.of("Command.Currency.Top.List",
        "<! prefix:\"false\" !>" +
            "\n" + CYAN +
            "\n" + CYAN + BOLD + CURRENCY_NAME + " Top:" +
            "\n" + CYAN +
            "\n" + CYAN + GENERIC_POS + ". " + GRAY + PLAYER_NAME + ": " + CYAN + GENERIC_BALANCE +
            "\n" + CYAN +
            "\n" + GRAY + "Page " + CYAN + GENERIC_CURRENT + GRAY + " of " + CYAN + GENERIC_MAX + GRAY + "." +
            "\n" + CYAN);

    public static final LegacyLangText CURRENCY_BALANCE_DISPLAY_OWN    = LegacyLangText.of("Currency.Balance.Display.Own", LIGHT_YELLOW + "Balance: " + LIGHT_ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + ".");
    public static final LegacyLangText CURRENCY_BALANCE_DISPLAY_OTHERS = LegacyLangText.of("Currency.Balance.Display.Others", LIGHT_ORANGE + PLAYER_NAME + LIGHT_YELLOW + "'s balance: " + LIGHT_ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + ".");

    public static final LegacyLangText CURRENCY_EXCHANGE_ERROR_DISABLED    = LegacyLangText.of("Currency.Exchange.Error.Disabled", LIGHT_ORANGE + CURRENCY_NAME + RED + " can not be exchanged!");
    public static final LegacyLangText CURRENCY_EXCHANGE_ERROR_NO_RATE     = LegacyLangText.of("Currency.Exchange.Error.NoRate", LIGHT_ORANGE + CURRENCY_NAME + RED + " can not be exchanged for " + LIGHT_ORANGE + GENERIC_NAME + RED + "!");
    public static final LegacyLangText CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT  = LegacyLangText.of("Currency.Exchange.Error.LowAmount", LIGHT_ORANGE + CURRENCY_NAME + RED + " amount is too low for exchange!");
    public static final LegacyLangText CURRENCY_EXCHANGE_ERROR_LOW_BALANCE = LegacyLangText.of("Currency.Exchange.Error.LowBalance", RED + "You don't have " + LIGHT_ORANGE + GENERIC_AMOUNT + RED + " for exchange!");
    public static final LegacyLangText CURRENCY_EXCHANGE_SUCCESS           = LegacyLangText.of("Currency.Exchange.Success", LIGHT_YELLOW + "You exchanged " + LIGHT_ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + " for " + GENERIC_AMOUNT + LIGHT_YELLOW + "!");

    public static final LegacyLangText CURRENCY_ERROR_INVALID = LegacyLangText.of("Currency.Error.Invalid", RED + "Invalid currency!");
}
