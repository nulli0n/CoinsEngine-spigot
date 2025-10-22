package su.nightexpress.coinsengine.command.currency.provider.type;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.command.currency.provider.CommandProvider;

public abstract class AbstractCommandProvider implements CommandProvider {

    protected final CoinsEnginePlugin plugin;
    protected final String            name;

    public AbstractCommandProvider(@NotNull CoinsEnginePlugin plugin, @NotNull String name) {
        this.plugin = plugin;
        this.name = name;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }
}
