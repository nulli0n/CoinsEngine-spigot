package su.nightexpress.coinsengine.config;

import su.nexmedia.engine.api.lang.LangColors;
import su.nexmedia.engine.api.lang.LangKey;
import su.nexmedia.engine.lang.EngineLang;
import su.nightexpress.coinsengine.Placeholders;

public class Lang extends EngineLang implements LangColors {

    public static final LangKey COMMAND_MIGRATE_DESC         = LangKey.of("Command.Migrate.Desc", "Migrate data from other plugin(s).");
    public static final LangKey COMMAND_MIGRATE_USAGE        = LangKey.of("Command.Migrate.Usage", "<plugin> <currency>");
    public static final LangKey COMMAND_MIGRATE_ERROR_PLUGIN = LangKey.of("Command.Migrate.Error.Plugin", RED + "Plugin is not supported or installed!");
    public static final LangKey COMMAND_MIGRATE_START         = LangKey.of("Command.Migrate.Start", LIGHT_YELLOW + "Started data migration from the " + ORANGE + Placeholders.GENERIC_NAME + LIGHT_YELLOW + "! This may take a while.");
    public static final LangKey COMMAND_MIGRATE_DONE         = LangKey.of("Command.Migrate.Done", LIGHT_YELLOW + "Migrated data from the " + ORANGE + Placeholders.GENERIC_NAME + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_RESET_DESC         = LangKey.of("Command.Reset.Desc", "Reset player's balances.");
    public static final LangKey COMMAND_RESET_USAGE        = LangKey.of("Command.Reset.Usage", "<player>");
    public static final LangKey COMMAND_RESET_DONE         = LangKey.of("Command.Reset.Done", LIGHT_YELLOW + "Reset all currency balances for " + ORANGE + Placeholders.Player.NAME + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_CURRENCY_BALANCE_USAGE = LangKey.of("Command.Currency.Balance.Usage", "[player]");
    public static final LangKey COMMAND_CURRENCY_BALANCE_DESC  = LangKey.of("Command.Currency.Balance.Desc", "Check [player's] currency balance.");
    public static final LangKey COMMAND_CURRENCY_BALANCE_DONE  = LangKey.of("Command.Currency.Balance.Done", GREEN + Placeholders.Player.NAME + GRAY + "'s balance: " + GREEN + Placeholders.GENERIC_BALANCE + GRAY + ".");

    public static final LangKey COMMAND_CURRENCY_GIVE_USAGE = LangKey.of("Command.Currency.Give.Usage", "<player> <amount>");
    public static final LangKey COMMAND_CURRENCY_GIVE_DESC  = LangKey.of("Command.Currency.Give.Desc", "Add currency to a player.");
    public static final LangKey COMMAND_CURRENCY_GIVE_DONE  = LangKey.of("Command.Currency.Give.Done", LIGHT_YELLOW + "Added " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " to " + ORANGE + Placeholders.Player.NAME + LIGHT_YELLOW + "'s balance. New balance: " + ORANGE + Placeholders.GENERIC_BALANCE + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_CURRENCY_TAKE_USAGE = LangKey.of("Command.Currency.Take.Usage", "<player> <amount>");
    public static final LangKey COMMAND_CURRENCY_TAKE_DESC  = LangKey.of("Command.Currency.Take.Desc", "Take player's currency.");
    public static final LangKey COMMAND_CURRENCY_TAKE_DONE  = LangKey.of("Command.Currency.Take.Done", LIGHT_YELLOW + "Taken " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " from " + ORANGE + Placeholders.Player.NAME + LIGHT_YELLOW + "'s balance. New balance: " + ORANGE + Placeholders.GENERIC_BALANCE + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_CURRENCY_SET_USAGE = LangKey.of("Command.Currency.Set.Usage", "<player> <amount>");
    public static final LangKey COMMAND_CURRENCY_SET_DESC  = LangKey.of("Command.Currency.Set.Desc", "Set player's currency balance.");
    public static final LangKey COMMAND_CURRENCY_SET_DONE  = LangKey.of("Command.Currency.Set.Done", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.Player.NAME + LIGHT_YELLOW + "'s " + Placeholders.CURRENCY_NAME + " balance to " + ORANGE + Placeholders.GENERIC_BALANCE + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_CURRENCY_SEND_USAGE            = LangKey.of("Command.Currency.Send.Usage", "<player> <amount>");
    public static final LangKey COMMAND_CURRENCY_SEND_DESC             = LangKey.of("Command.Currency.Send.Desc", "Transfer currency to a player.");
    public static final LangKey COMMAND_CURRENCY_SEND_ERROR_NOT_ENOUGH = LangKey.of("Command.Currency.Send.Error.NotEnough", LIGHT_YELLOW + "You don't have enought " + RED + Placeholders.CURRENCY_NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_CURRENCY_SEND_DONE_SENDER      = LangKey.of("Command.Currency.Send.Done.Sender", LIGHT_YELLOW + "You sent " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " to " + ORANGE + Placeholders.Player.NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_CURRENCY_SEND_DONE_NOTIFY      = LangKey.of("Command.Currency.Send.Done.Notify", LIGHT_YELLOW + "You received " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " from " + ORANGE + Placeholders.Player.NAME + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_CURRENCY_TOP_USAGE = LangKey.of("Command.Currency.Top.Usage", "[page]");
    public static final LangKey COMMAND_CURRENCY_TOP_DESC  = LangKey.of("Command.Currency.Top.Desc", "List of players with the most balance.");
    public static final LangKey COMMAND_CURRENCY_TOP_LIST  = LangKey.of("Command.Currency.Top.List",
        "<! prefix:\"false\" !>" +
            "\n" + CYAN +
            "\n" + CYAN + "&l" + Placeholders.CURRENCY_NAME + " Top:" +
            "\n" + CYAN +
            "\n" + CYAN + Placeholders.GENERIC_POS + ". " + GRAY + Placeholders.Player.NAME + ": " + CYAN + Placeholders.GENERIC_BALANCE +
            "\n" + CYAN +
            "\n" + GRAY + "Page " + CYAN + Placeholders.GENERIC_CURRENT + GRAY + " of " + CYAN + Placeholders.GENERIC_MAX + GRAY + "." +
            "\n" + CYAN);

    public static final LangKey CURRENCY_ERROR_INVALID = LangKey.of("Currency.Error.Invalid", RED + "Invalid currency!");
}
