package su.nightexpress.coinsengine.util;

import org.jetbrains.annotations.NotNull;

public record TopEntry(int position, double balance, @NotNull String name) {

}
