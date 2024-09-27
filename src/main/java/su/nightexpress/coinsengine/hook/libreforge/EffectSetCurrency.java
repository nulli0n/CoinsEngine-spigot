package su.nightexpress.coinsengine.hook.libreforge;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.libreforge.ConfigArguments;
import com.willfp.libreforge.ConfigArgumentsBuilder;
import com.willfp.libreforge.NoCompileData;
import com.willfp.libreforge.effects.Effect;
import com.willfp.libreforge.triggers.TriggerData;
import com.willfp.libreforge.triggers.TriggerParameter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.Set;

public class EffectSetCurrency extends Effect<NoCompileData> {
    private final CoinsEnginePlugin plugin;

    public EffectSetCurrency(CoinsEnginePlugin plugin) {
        super("set_currency");
        this.plugin = plugin;
    }

    @NotNull
    @Override
    protected Set<TriggerParameter> getParameters() {
        return Set.of(TriggerParameter.PLAYER);
    }

    @Override
    protected boolean onTrigger(@NotNull Config config, @NotNull TriggerData data, NoCompileData compileData) {
        Player player = data.getPlayer();
        if (player == null)
            return false;
        Currency currency = plugin.getCurrencyManager().getCurrency(config.getString("currency"));
        if (currency == null)
            return false;
        plugin.getUserManager().getUserDataAndPerform(player.getUniqueId(), user -> {
            user.setBalance(currency, config.getDoubleFromExpression("amount", player));
            plugin.getUserManager().saveAsync(user);
        });
        return true;
    }

    @NotNull
    @Override
    public ConfigArguments getArguments() {
        ConfigArgumentsBuilder builder = new ConfigArgumentsBuilder();
        builder.require("currency", "You must specify the currency to set!");
        builder.require("amount", "You must specify the set amount!");
        return builder.build$core();
    }
}
