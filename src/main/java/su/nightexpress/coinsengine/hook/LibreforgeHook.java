package su.nightexpress.coinsengine.hook;

import com.willfp.libreforge.conditions.Conditions;
import com.willfp.libreforge.effects.Effects;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.hook.libreforge.ConditionHasCurrency;
import su.nightexpress.coinsengine.hook.libreforge.EffectGiveCurrency;
import su.nightexpress.coinsengine.hook.libreforge.EffectTakeCurrency;

public class LibreforgeHook {

    public static void setup(@NotNull CoinsEnginePlugin plugin) {
        Effects.INSTANCE.register(new EffectGiveCurrency(plugin));
        Effects.INSTANCE.register(new EffectTakeCurrency(plugin));
        Conditions.INSTANCE.register(new ConditionHasCurrency(plugin));
        plugin.info("Hooked into eco (libreforge)!");
    }
}
