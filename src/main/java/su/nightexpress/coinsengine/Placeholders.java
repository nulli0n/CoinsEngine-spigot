package su.nightexpress.coinsengine;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.nightcore.util.placeholder.PlaceholderList;

import java.util.function.UnaryOperator;

public class Placeholders extends su.nightexpress.nightcore.util.Placeholders {

    public static final String WIKI_URL          = "https://nightexpressdev.com/coinsengine/";
    public static final String WIKI_PERMISSIONS  = WIKI_URL + "permissions";
    public static final String WIKI_COMMANDS  = WIKI_URL + "commands";
    public static final String WIKI_PLACEHOLDERS = WIKI_URL + "placeholders";
    public static final String WIKI_EXCHANGE     = WIKI_URL + "features/exchange";
    public static final String WIKI_CROSS_SERVER = WIKI_URL + "features/cross-server";
    public static final String WIKI_MIGRATION    = WIKI_URL + "features/migration";
    public static final String WIKI_TOPS         = WIKI_URL + "features/leaderboards";
    public static final String WIKI_PREFIXES     = WIKI_URL + "features/prefix";
    public static final String WIKI_WALLET       = WIKI_URL + "features/wallet";
    public static final String WIKI_VAULT_HOOK = WIKI_URL + "hooks/vault";

    public static final String GENERIC_NAME          = "%name%";
    public static final String GENERIC_BALANCE       = "%balance%";
    public static final String GENERIC_AMOUNT        = "%amount%";
    public static final String GENERIC_CURRENT       = "%current%";
    public static final String GENERIC_MAX           = "%max%";
    public static final String GENERIC_POS           = "%pos%";
    public static final String GENERIC_STATE         = "%state%";
    public static final String GENERIC_ENTRY         = "%entry%";
    public static final String GENERIC_NEXT_PAGE     = "%next_page%";
    public static final String GENERIC_PREVIOUS_PAGE = "%previous_page%";

    public static final String CURRENCY_ID     = "%currency_id%";
    public static final String CURRENCY_NAME   = "%currency_name%";
    public static final String CURRENCY_SYMBOL = "%currency_symbol%";
    public static final String CURRENCY_PREFIX = "%currency_prefix%";
    public static final String CURRENCY_LABEL  = "%currency_label%";

    public static final PlaceholderList<Currency> CURRENCY = PlaceholderList.create(list -> list
        .add(CURRENCY_ID, Currency::getId)
        .add(CURRENCY_NAME, Currency::getName)
        .add(CURRENCY_PREFIX, Currency::getPrefix)
        .add(CURRENCY_SYMBOL, Currency::getSymbol)
        .add(CURRENCY_LABEL, currency -> currency.getCommandAliases()[0])
    );

    @NotNull
    public static UnaryOperator<String> forCurrency(@NotNull Currency currency) {
        return CURRENCY.replacer(currency);
    }
}
