package su.nightexpress.coinsengine.data.serialize;

import com.google.gson.*;
import su.nightexpress.coinsengine.data.impl.CurrencySettings;

import java.lang.reflect.Type;

public class CurrencySettingsSerializer implements JsonSerializer<CurrencySettings>, JsonDeserializer<CurrencySettings> {

    @Override
    public CurrencySettings deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        boolean paymentsEnabled = object.get("paymentsEnabled").getAsBoolean();

        return new CurrencySettings(paymentsEnabled);
    }

    @Override
    public JsonElement serialize(CurrencySettings data, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("paymentsEnabled", data.isPaymentsEnabled());
        return object;
    }
}
