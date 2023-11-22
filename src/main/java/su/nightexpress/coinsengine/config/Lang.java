package su.nightexpress.coinsengine.config;

import su.nexmedia.engine.api.lang.LangKey;
import su.nexmedia.engine.lang.EngineLang;

import static su.nexmedia.engine.utils.Colors.*;
import static su.nightexpress.coinsengine.Placeholders.*;

public class Lang extends EngineLang {

    public static final LangKey COMMAND_MIGRATE_DESC         = LangKey.of("Command.Migrate.Desc", "Migrate data from other plugin(s).");
    public static final LangKey COMMAND_MIGRATE_USAGE        = LangKey.of("Command.Migrate.Usage", "<plugin> <currency>");
    public static final LangKey COMMAND_MIGRATE_ERROR_PLUGIN = LangKey.of("Command.Migrate.Error.Plugin", RED + "Plugin is not supported or installed!");
    public static final LangKey COMMAND_MIGRATE_START        = LangKey.of("Command.Migrate.Start", LIGHT_YELLOW + "Started data migration from the " + ORANGE + GENERIC_NAME + LIGHT_YELLOW + "! This may take a while.");
    public static final LangKey COMMAND_MIGRATE_DONE         = LangKey.of("Command.Migrate.Done", LIGHT_YELLOW + "Migrated data from the " + ORANGE + GENERIC_NAME + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_RESET_DESC  = LangKey.of("Command.Reset.Desc", "Reset player's balances.");
    public static final LangKey COMMAND_RESET_USAGE = LangKey.of("Command.Reset.Usage", "<player>");
    public static final LangKey COMMAND_RESET_DONE  = LangKey.of("Command.Reset.Done", LIGHT_YELLOW + "Reset all currency balances for " + ORANGE + PLAYER_NAME + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_WIPE_DESC   = LangKey.of("Command.Wipe.Desc", "Reset all currencies for all users.");
    public static final LangKey COMMAND_WIPE_USAGE  = LangKey.of("Command.Wipe.Usage", "<currency>");
    public static final LangKey COMMAND_WIPE_START  = LangKey.of("Command.Wipe.Start", LIGHT_YELLOW + "Started currency data wipe for " + ORANGE + CURRENCY_NAME + LIGHT_YELLOW + ". This may take a while...");
    public static final LangKey COMMAND_WIPE_FINISH = LangKey.of("Command.Wipe.Finish", LIGHT_YELLOW + "Finished currency data wipe for " + ORANGE + CURRENCY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_CURRENCY_BALANCE_USAGE = LangKey.of("Command.Currency.Balance.Usage", "[player]");
    public static final LangKey COMMAND_CURRENCY_BALANCE_DESC  = LangKey.of("Command.Currency.Balance.Desc", "Check [player's] currency balance.");

    public static final LangKey COMMAND_CURRENCY_GIVE_USAGE  = LangKey.of("Command.Currency.Give.Usage", "<player> <amount> [-s]");
    public static final LangKey COMMAND_CURRENCY_GIVE_DESC   = LangKey.of("Command.Currency.Give.Desc", "Add currency to a player.");
    public static final LangKey COMMAND_CURRENCY_GIVE_DONE   = LangKey.of("Command.Currency.Give.Done", LIGHT_YELLOW + "Added " + ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " to " + ORANGE + PLAYER_NAME + LIGHT_YELLOW + "'s balance. New balance: " + ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_CURRENCY_GIVE_NOTIFY = LangKey.of("Command.Currency.Give.Notify", ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " has been added to your account!");

    public static final LangKey COMMAND_CURRENCY_TAKE_USAGE  = LangKey.of("Command.Currency.Take.Usage", "<player> <amount> [-s]");
    public static final LangKey COMMAND_CURRENCY_TAKE_DESC   = LangKey.of("Command.Currency.Take.Desc", "Take player's currency.");
    public static final LangKey COMMAND_CURRENCY_TAKE_DONE   = LangKey.of("Command.Currency.Take.Done", LIGHT_YELLOW + "Taken " + ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " from " + ORANGE + PLAYER_NAME + LIGHT_YELLOW + "'s balance. New balance: " + ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_CURRENCY_TAKE_NOTIFY = LangKey.of("Command.Currency.Take.Notify", ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " has been taken from your account!");

    public static final LangKey COMMAND_CURRENCY_SET_USAGE  = LangKey.of("Command.Currency.Set.Usage", "<player> <amount> [-s]");
    public static final LangKey COMMAND_CURRENCY_SET_DESC   = LangKey.of("Command.Currency.Set.Desc", "Set player's currency balance.");
    public static final LangKey COMMAND_CURRENCY_SET_DONE   = LangKey.of("Command.Currency.Set.Done", LIGHT_YELLOW + "Set " + ORANGE + PLAYER_NAME + LIGHT_YELLOW + "'s " + CURRENCY_NAME + " balance to " + ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_CURRENCY_SET_NOTIFY = LangKey.of("Command.Currency.Set.Notify", LIGHT_YELLOW + "Your " + ORANGE + CURRENCY_NAME + LIGHT_YELLOW + " balance has been set to " + ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_CURRENCY_SEND_USAGE             = LangKey.of("Command.Currency.Send.Usage", "<player> <amount>");
    public static final LangKey COMMAND_CURRENCY_SEND_DESC              = LangKey.of("Command.Currency.Send.Desc", "Transfer currency to a player.");
    public static final LangKey COMMAND_CURRENCY_SEND_ERROR_NOT_ENOUGH  = LangKey.of("Command.Currency.Send.Error.NotEnough", LIGHT_YELLOW + "You don't have enought " + RED + CURRENCY_NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_CURRENCY_SEND_ERROR_TOO_LOW     = LangKey.of("Command.Currency.Send.Error.TooLow", LIGHT_YELLOW + "You can not send smaller than " + RED + GENERIC_AMOUNT + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_CURRENCY_SEND_ERROR_NO_PAYMENTS = LangKey.of("Command.Currency.Send.Error.NoPayments", RED + PLAYER_NAME + LIGHT_YELLOW + " does not accept " + RED + CURRENCY_NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_CURRENCY_SEND_DONE_SENDER       = LangKey.of("Command.Currency.Send.Done.Sender", LIGHT_YELLOW + "You sent " + ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " to " + ORANGE + PLAYER_NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_CURRENCY_SEND_DONE_NOTIFY       = LangKey.of("Command.Currency.Send.Done.Notify", LIGHT_YELLOW + "You received " + ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " from " + ORANGE + PLAYER_NAME + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_CURRENCY_PAYMENTS_USAGE  = LangKey.of("Command.Currency.Payments.Usage", "[player] [-s]");
    public static final LangKey COMMAND_CURRENCY_PAYMENTS_DESC   = LangKey.of("Command.Currency.Payments.Desc", "Toggle payments acception from other players.");
    public static final LangKey COMMAND_CURRENCY_PAYMENTS_TOGGLE = LangKey.of("Command.Currency.Payments.Toggle", ORANGE + CURRENCY_NAME + LIGHT_YELLOW + " payments acception: " + ORANGE + GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_CURRENCY_PAYMENTS_TARGET = LangKey.of("Command.Currency.Payments.Target", ORANGE + CURRENCY_NAME + LIGHT_YELLOW + " payments acception for " + ORANGE + PLAYER_NAME + LIGHT_YELLOW + ": " + ORANGE + GENERIC_STATE + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_CURRENCY_EXCHANGE_USAGE             = LangKey.of("Command.Currency.Exchange.Usage", "<currency> <amount>");
    public static final LangKey COMMAND_CURRENCY_EXCHANGE_DESC              = LangKey.of("Command.Currency.Exchange.Desc", "Exchange currency.");

    public static final LangKey COMMAND_CURRENCY_TOP_USAGE = LangKey.of("Command.Currency.Top.Usage", "[page]");
    public static final LangKey COMMAND_CURRENCY_TOP_DESC  = LangKey.of("Command.Currency.Top.Desc", "List of players with the most balance.");
    public static final LangKey COMMAND_CURRENCY_TOP_LIST  = LangKey.of("Command.Currency.Top.List",
        "<! prefix:\"false\" !>" +
            "\n" + CYAN +
            "\n" + CYAN + BOLD + CURRENCY_NAME + " Top:" +
            "\n" + CYAN +
            "\n" + CYAN + GENERIC_POS + ". " + GRAY + PLAYER_NAME + ": " + CYAN + GENERIC_BALANCE +
            "\n" + CYAN +
            "\n" + GRAY + "Page " + CYAN + GENERIC_CURRENT + GRAY + " of " + CYAN + GENERIC_MAX + GRAY + "." +
            "\n" + CYAN);

    public static final LangKey CURRENCY_BALANCE_DISPLAY_OWN    = LangKey.of("Currency.Balance.Display.Own", LIGHT_YELLOW + "Balance: " + ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + ".");
    public static final LangKey CURRENCY_BALANCE_DISPLAY_OTHERS = LangKey.of("Currency.Balance.Display.Others", ORANGE + PLAYER_NAME + LIGHT_YELLOW + "'s balance: " + ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + ".");

    public static final LangKey CURRENCY_EXCHANGE_ERROR_DISABLED   = LangKey.of("Currency.Exchange.Error.Disabled", ORANGE + CURRENCY_NAME + RED + " can not be exchanged!");
    public static final LangKey CURRENCY_EXCHANGE_ERROR_NO_RATE    = LangKey.of("Currency.Exchange.Error.NoRate", ORANGE + CURRENCY_NAME + RED + " can not be exchanged for " + ORANGE + GENERIC_NAME + RED + "!");
    public static final LangKey CURRENCY_EXCHANGE_ERROR_LOW_AMOUNT = LangKey.of("Currency.Exchange.Error.LowAmount", ORANGE + CURRENCY_NAME + RED + " amount is too low for exchange!");
    public static final LangKey CURRENCY_EXCHANGE_ERROR_LOW_BALANCE = LangKey.of("Currency.Exchange.Error.LowBalance", RED + "You don't have " + ORANGE + GENERIC_AMOUNT + RED + " for exchange!");
    public static final LangKey CURRENCY_EXCHANGE_SUCCESS          = LangKey.of("Currency.Exchange.Success", LIGHT_YELLOW + "You exchanged " + ORANGE + GENERIC_BALANCE + LIGHT_YELLOW + " for " + GENERIC_AMOUNT + LIGHT_YELLOW + "!");

    public static final LangKey CURRENCY_ERROR_INVALID = LangKey.of("Currency.Error.Invalid", RED + "Invalid currency!");
}
