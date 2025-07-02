package su.nightexpress.coinsengine.config;

import org.bukkit.permissions.PermissionDefault;
import su.nightexpress.coinsengine.Placeholders;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

public class Perms {

    public static final String PREFIX          = "coinsengine.";
    public static final String PREFIX_COMMAND  = PREFIX + "command.";
    public static final String PREFIX_CURRENCY = PREFIX + "currency.";

    public static final UniPermission PLUGIN   = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND  = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission CURRENCY = new UniPermission(PREFIX_CURRENCY + Placeholders.WILDCARD);

    public static final UniPermission HIDE_FROM_TOPS = new UniPermission(PREFIX + "hidefromtops", PermissionDefault.FALSE);

    public static final UniPermission COMMAND_CURRENCY_BALANCE         = new UniPermission(PREFIX_COMMAND + "currency.balance");
    public static final UniPermission COMMAND_CURRENCY_BALANCE_OTHERS  = new UniPermission(PREFIX_COMMAND + "currency.balance.others");
    public static final UniPermission COMMAND_CURRENCY_EXCHANGE        = new UniPermission(PREFIX_COMMAND + "currency.exchange");
    public static final UniPermission COMMAND_CURRENCY_GIVE            = new UniPermission(PREFIX_COMMAND + "currency.add");
    public static final UniPermission COMMAND_CURRENCY_GIVE_ALL        = new UniPermission(PREFIX_COMMAND + "currency.addall");
    public static final UniPermission COMMAND_CURRENCY_PAYMENTS        = new UniPermission(PREFIX_COMMAND + "currency.payments");
    public static final UniPermission COMMAND_CURRENCY_PAYMENTS_OTHERS = new UniPermission(PREFIX_COMMAND + "currency.payments.others");
    public static final UniPermission COMMAND_CURRENCY_TOP             = new UniPermission(PREFIX_COMMAND + "currency.top");
    public static final UniPermission COMMAND_CURRENCY_SEND            = new UniPermission(PREFIX_COMMAND + "currency.send");
    public static final UniPermission COMMAND_CURRENCY_SET             = new UniPermission(PREFIX_COMMAND + "currency.set");
    public static final UniPermission COMMAND_CURRENCY_TAKE            = new UniPermission(PREFIX_COMMAND + "currency.take");

    public static final UniPermission COMMAND_RELOAD        = new UniPermission(PREFIX_COMMAND + "reload");
    public static final UniPermission COMMAND_CREATE        = new UniPermission(PREFIX_COMMAND + "create");
    public static final UniPermission COMMAND_RESET         = new UniPermission(PREFIX_COMMAND + "reset");
    public static final UniPermission COMMAND_RESET_ALL     = new UniPermission(PREFIX_COMMAND + "resetall");
    public static final UniPermission COMMAND_MIGRATE       = new UniPermission(PREFIX_COMMAND + "migrate");
    public static final UniPermission COMMAND_WALLET        = new UniPermission(PREFIX_COMMAND + "wallet");
    public static final UniPermission COMMAND_WALLET_OTHERS = new UniPermission(PREFIX_COMMAND + "wallet.others");

    static {
        PLUGIN.addChildren(
            COMMAND,
            CURRENCY,
            HIDE_FROM_TOPS
        );

        COMMAND.addChildren(
            COMMAND_RELOAD,
            COMMAND_CREATE,
            COMMAND_RESET,
            COMMAND_RESET_ALL,
            COMMAND_MIGRATE,
            COMMAND_WALLET,
            COMMAND_WALLET_OTHERS,

            COMMAND_CURRENCY_GIVE,
            COMMAND_CURRENCY_GIVE_ALL,
            COMMAND_CURRENCY_BALANCE,
            COMMAND_CURRENCY_BALANCE_OTHERS,
            COMMAND_CURRENCY_PAYMENTS,
            COMMAND_CURRENCY_PAYMENTS_OTHERS,
            COMMAND_CURRENCY_EXCHANGE,
            COMMAND_CURRENCY_SEND,
            COMMAND_CURRENCY_SET,
            COMMAND_CURRENCY_TAKE,
            COMMAND_CURRENCY_TOP
        );
    }
}
