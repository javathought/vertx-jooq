package generators;

import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;

public class CustomConverter {

    private CustomConverter() {
        // Hide constructor
    }

    public static BigDecimal getBigDecimalValue(JsonObject json, String field) {
        try {
            return json.getDouble(field) == null ? null : BigDecimal.valueOf(json.getDouble(field));
        } catch (ClassCastException e) {
            return json.getString(field) == null ? null : new BigDecimal(json.getString(field));
        }
    }

}
