package net.fabricmc.joamama.gson;

import com.google.common.collect.Table;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.world.entity.MobType;

public class TraitsGson {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Table.class, new TableSerializer())
            .registerTypeAdapter(Map.class, new MapSerializer())
            .excludeFieldsWithoutExposeAnnotation()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static Gson gson () {
        return gson;
    }

    @SuppressWarnings("rawtypes")
    private static class TableSerializer implements JsonSerializer<Table> {
        public JsonElement serialize (Table source, Type type, JsonSerializationContext context) {
            return context.serialize(source.rowMap());
        }
    }

    @SuppressWarnings("rawtypes")
    private static class MapSerializer implements JsonSerializer<Map> {
        private static final Gson mapGson = new GsonBuilder()
                .registerTypeAdapter(Table.class, new TableSerializer())
                .excludeFieldsWithoutExposeAnnotation()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        public JsonElement serialize (Map source, Type type, JsonSerializationContext context) {
            if (source.size() == 1) {
                return context.serialize(source.values().iterator().next());
            } else {
                return mapGson.toJsonTree(source);
            }
        }
    }
}
