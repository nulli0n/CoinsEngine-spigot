package su.nightexpress.coinsengine.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CoinsUser;
import su.nightexpress.nightcore.db.AbstractUserManager;

import java.util.UUID;

public class UserManager extends AbstractUserManager<CoinsEnginePlugin, CoinsUser> {

    public UserManager(@NotNull CoinsEnginePlugin plugin, @NotNull DataHandler dataHandler) {
        super(plugin, dataHandler);
    }

    @Override
    @NotNull
    public CoinsUser create(@NotNull UUID uuid, @NotNull String name) {
        return CoinsUser.create(uuid, name);
    }

    public void synchronize() {
        // Do not synchronize data if operations are disabled to prevent data loss/clash.
        if (!this.plugin.getCurrencyManager().canPerformOperations()) return;

        this.getLoaded().forEach(this::handleSynchronization);
    }

    public void handleSynchronization(@NotNull CoinsUser user) {
        if (user.isAutoSavePlanned() || !user.isAutoSyncReady()) return;

        CoinsUser fresh = this.getFromDatabase(user.getId());
        if (fresh == null) return;

        for (Currency currency : this.plugin.getCurrencyManager().getCurrencies()) {
            if (!currency.isSynchronizable()) continue;

            double balance = fresh.getBalance(currency);
            user.getBalance().set(currency, balance); // Bypass balance event call.
        }
    }
}
