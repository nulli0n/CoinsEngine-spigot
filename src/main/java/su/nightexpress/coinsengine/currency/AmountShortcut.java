package su.nightexpress.coinsengine.currency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;

public class AmountShortcut {

    private final String literal;
    private final double multiplier;

    public AmountShortcut(@NotNull String literal, double multiplier) {
        this.literal = literal.toLowerCase();
        this.multiplier = multiplier;
    }

    @Nullable
    public static AmountShortcut read(@NotNull FileConfig cfg, @NotNull String path) {
        String literal = cfg.getString(path + ".Literal");
        if (literal == null) return null;

        double multiplier = cfg.getDouble(path + ".Multiplier");

        return new AmountShortcut(literal, multiplier);
    }

    public void write(@NotNull FileConfig cfg, @NotNull String path) {
        cfg.set(path + ".Literal", this.getLiteral());
        cfg.set(path + ".Multiplier", this.getMultiplier());
    }

    @NotNull
    public String getLiteral() {
        return literal;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
