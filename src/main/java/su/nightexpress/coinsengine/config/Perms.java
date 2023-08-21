package su.nightexpress.coinsengine.config;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.coinsengine.Placeholders;

public class Perms {

    private static final String PREFIX = "coinsengine.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final String PREFIX_CURRENCY = PREFIX + "currency.";

    public static final JPermission PLUGIN   = new JPermission(PREFIX + Placeholders.WILDCARD);
    public static final JPermission COMMAND  = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final JPermission CURRENCY = new JPermission(PREFIX_CURRENCY + Placeholders.WILDCARD);

    public static final JPermission COMMAND_CURRENCY_ADD            = new JPermission(PREFIX_COMMAND + "currency.add");
    public static final JPermission COMMAND_CURRENCY_BALANCE        = new JPermission(PREFIX_COMMAND + "currency.balance");
    public static final JPermission COMMAND_CURRENCY_BALANCE_OTHERS = new JPermission(PREFIX_COMMAND + "currency.balance.others");
    public static final JPermission COMMAND_CURRENCY_PAYMENTS        = new JPermission(PREFIX_COMMAND + "currency.payments");
    public static final JPermission COMMAND_CURRENCY_PAYMENTS_OTHERS = new JPermission(PREFIX_COMMAND + "currency.payments.others");
    public static final JPermission COMMAND_CURRENCY_TOP            = new JPermission(PREFIX_COMMAND + "currency.top");
    public static final JPermission COMMAND_CURRENCY_SEND           = new JPermission(PREFIX_COMMAND + "currency.send");
    public static final JPermission COMMAND_CURRENCY_SET            = new JPermission(PREFIX_COMMAND + "currency.set");
    public static final JPermission COMMAND_CURRENCY_TAKE           = new JPermission(PREFIX_COMMAND + "currency.take");

    public static final JPermission COMMAND_RELOAD  = new JPermission(PREFIX_COMMAND + "reload");
    public static final JPermission COMMAND_RESET   = new JPermission(PREFIX_COMMAND + "reset");
    public static final JPermission COMMAND_WIPE = new JPermission(PREFIX_COMMAND + "wipe");
    public static final JPermission COMMAND_MIGRATE = new JPermission(PREFIX_COMMAND + "migrate");

    static {
        PLUGIN.addChildren(COMMAND, CURRENCY);

        COMMAND.addChildren(
            COMMAND_RELOAD, COMMAND_RESET, COMMAND_WIPE, COMMAND_MIGRATE,
            COMMAND_CURRENCY_ADD, COMMAND_CURRENCY_BALANCE, COMMAND_CURRENCY_BALANCE_OTHERS,
            COMMAND_CURRENCY_PAYMENTS, COMMAND_CURRENCY_PAYMENTS_OTHERS,
            COMMAND_CURRENCY_SEND, COMMAND_CURRENCY_SET, COMMAND_CURRENCY_TAKE, COMMAND_CURRENCY_TOP
        );
    }
}
