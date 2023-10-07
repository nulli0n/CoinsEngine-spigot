package su.nightexpress.coinsengine.util;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.coinsengine.config.Config;

public class CoinsUtils {

    public static double getAmountFromInput(@NotNull String input) {
        input = input.toLowerCase();
        double multiplier = 1D;

        for (var shortcut : Config.AMOUNT_SHORTCUTS.get().values()) {
            while (input.endsWith(shortcut.getLiteral())) {
                input = input.substring(0, input.length() - shortcut.getLiteral().length());
                multiplier *= shortcut.getMultiplier();
            }
        }

        double value = StringUtil.getDouble(input, 0D) * multiplier;
        if (Double.isInfinite(value) || Double.isNaN(value)) value = 0D;

        return value;
    }
}
