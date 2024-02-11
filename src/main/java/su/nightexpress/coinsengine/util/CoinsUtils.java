/*
 * Decompiled with CFR 0.151.
 *
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  su.nexmedia.engine.utils.StringUtil
 */
package su.nightexpress.coinsengine.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.config.Config;
import su.nightexpress.coinsengine.currency.AmountShortcut;
import su.nightexpress.nightcore.util.NumberUtil;

public class CoinsUtils {

    public static double getAmountFromInput(@NotNull String input) {
        input = input.toLowerCase();
        double multiplier = 1D;

        for (AmountShortcut shortcut : Config.AMOUNT_SHORTCUTS.get().values()) {
            while (input.endsWith(shortcut.getLiteral())) {
                input = input.substring(0, input.length() - shortcut.getLiteral().length());
                multiplier *= shortcut.getMultiplier();
            }
        }

        double value = NumberUtil.getDouble(input, 0D) * multiplier;
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            value = 0D;
        }
        return value;
    }
}

