package su.nightexpress.coinsengine.currency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.nightcore.util.LowerCase;

import java.util.*;

public class CurrencyRegistry {

    //private final CoinsEnginePlugin plugin;
    private final Map<String, Currency> currencyMap;

    public CurrencyRegistry() {
        //this.plugin = plugin;
        this.currencyMap = new HashMap<>();
    }

    public void removeAll() {
        this.getCurrencies().forEach(this::remove);
    }

    /*public void unregisterNormal() {
        this.getCurrencies().stream().filter(Predicate.not(Currency::isPrimary)).forEach(this::remove);
    }*/

    public void add(@NotNull Currency currency) {
        this.currencyMap.put(currency.getId(), currency);

        currency.onRegister();
    }

    @Nullable
    public Currency remove(@NotNull Currency currency) {
        return this.remove(currency.getId());
    }

    @Nullable
    public Currency remove(@NotNull String id) {
        Currency currency = this.currencyMap.remove(LowerCase.INTERNAL.apply(id));
        if (currency != null) {
            currency.onUnregister();
        }

        return currency;
    }

    @NotNull
    public Map<String, Currency> getCurrencyMap() {
        return this.currencyMap;
    }

    public boolean hasPrimary() {
        return this.findPrimary().isPresent();
    }

    public boolean isRegistered(@NotNull String id) {
        return this.currencyMap.containsKey(LowerCase.INTERNAL.apply(id));
    }

    @NotNull
    public Optional<Currency> findPrimary() {
        return this.currencyMap.values().stream().filter(Currency::isPrimary).findFirst();
    }

    @Nullable
    public Currency getById(@NotNull String id) {
        return this.currencyMap.get(LowerCase.INTERNAL.apply(id));
    }

    @NotNull
    public Optional<Currency> byId(@NotNull String id) {
        return Optional.ofNullable(this.getById(id));
    }

    @NotNull
    public List<String> getCurrencyIds() {
        return new ArrayList<>(this.currencyMap.keySet());
    }

    @NotNull
    public Collection<Currency> getCurrencies() {
        return new HashSet<>(this.currencyMap.values());
    }
}
