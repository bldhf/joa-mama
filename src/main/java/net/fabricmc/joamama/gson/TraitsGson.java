package net.fabricmc.joamama.gson;

import com.google.common.collect.Table;
import com.google.gson.*;
import org.joml.RoundingMode;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;

public class TraitsGson {
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(Table.class, new TableSerializer())
        .registerTypeAdapter(Map.class, new MapSerializer())
        // If you need to get exact numbers, comment these lines out.
        // It also breaks things that use the magic horse width.
        // Why the hell is the magic horse width such a specific number?
        // Like, 7 goddamn decimal digits. Seriously!?
        .registerTypeAdapter(Float.class, new NumberSerializer())
        .registerTypeAdapter(Double.class, new NumberSerializer())
        .excludeFieldsWithoutExposeAnnotation()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();

    public static Gson gson() {
        return gson;
    }

    @SuppressWarnings("rawtypes")
    private static class TableSerializer implements JsonSerializer<Table> {
        public JsonElement serialize(Table source, Type type, JsonSerializationContext context) {
            return context.serialize(source.rowMap());
        }
    }

    @SuppressWarnings("rawtypes")
    private static class MapSerializer implements JsonSerializer<Map> {
        public JsonElement serialize(Map source, Type type, JsonSerializationContext context) {
            if (source.size() == 1) {
                return context.serialize(source.values().iterator().next());
            } else {
                return gson.toJsonTree(source);
            }
        }
    }

    private static class NumberSerializer implements JsonSerializer<Number> {
        public JsonElement serialize(Number source, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(BigDecimal.valueOf(source.doubleValue()).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros());
        }
    }
}
