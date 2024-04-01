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
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.coinsengine.data.impl.CurrencyData;

import java.util.Set;

public class EffectTakeCurrency extends Effect<NoCompileData> {
    private final CoinsEnginePlugin plugin;

    public EffectTakeCurrency(CoinsEnginePlugin plugin) {
        super("take_currency");
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
            user.getCurrencyData(currency).removeBalance(config.getDoubleFromExpression("amount", player));
            plugin.getUserManager().saveAsync(user);
        });
        return true;
    }

    @NotNull
    @Override
    public ConfigArguments getArguments() {
        ConfigArgumentsBuilder builder = new ConfigArgumentsBuilder();
        builder.require("currency", "You must specify the currency to take!");
        builder.require("amount", "You must specify the taken amount!");
        return builder.build$core();
    }
}
