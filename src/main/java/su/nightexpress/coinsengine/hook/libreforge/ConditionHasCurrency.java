package su.nightexpress.coinsengine.hook.libreforge;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.libreforge.*;
import com.willfp.libreforge.conditions.Condition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CurrencyData;

public class ConditionHasCurrency extends Condition<NoCompileData> {
    private final CoinsEnginePlugin plugin;

    public ConditionHasCurrency(CoinsEnginePlugin plugin) {
        super("has_currency");
        this.plugin = plugin;
    }

    @Override
    public boolean isMet(@NotNull Dispatcher<?> dispatcher, @NotNull Config config, @NotNull ProvidedHolder holder, NoCompileData compileData) {
        if (!(dispatcher.getDispatcher() instanceof Player player))
            return false;
        Currency currency = plugin.getCurrencyManager().getCurrency(config.getString("currency"));
        if (currency == null)
            return false;
        return plugin.getUserManager().getUserData(player).getBalance(currency) >= config.getDoubleFromExpression("amount", player);
    }

    @NotNull
    @Override
    public ConfigArguments getArguments() {
        ConfigArgumentsBuilder builder = new ConfigArgumentsBuilder();
        builder.require("currency", "You must specify the currency to check!");
        builder.require("amount", "You must specify the amount!");
        return builder.build$core();
    }
}
