package su.nightexpress.coinsengine.data.serialize;

import com.google.gson.*;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.data.impl.CurrencyData;

import java.lang.reflect.Type;

public class CurrencyDataSerializer implements JsonSerializer<CurrencyData>, JsonDeserializer<CurrencyData> {

    @Override
    public CurrencyData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        String currencyId = object.get("currencyId").getAsString();
        Currency currency = CoinsEngineAPI.getCurrency(currencyId);
        if (currency == null) return null;

        double balance = object.get("balance").getAsDouble();
        boolean paymentsEnabled = object.get("paymentsEnabled").getAsBoolean();

        return new CurrencyData(currency, balance, paymentsEnabled);
    }

    @Override
    public JsonElement serialize(CurrencyData data, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("currencyId", data.getCurrency().getId());
        object.addProperty("balance", data.getBalance());
        object.addProperty("paymentsEnabled", data.isPaymentsEnabled());
        return object;
    }
}
