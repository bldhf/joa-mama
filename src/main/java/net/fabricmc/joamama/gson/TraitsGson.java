package net.fabricmc.joamama.gson;

import com.google.common.collect.Table;
import com.google.gson.*;
import net.minecraft.entity.EntityGroup;

import java.lang.reflect.Type;
import java.util.Map;

public class TraitsGson {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Table.class, new TableSerializer())
            .registerTypeAdapter(Map.class, new MapSerializer())
            .registerTypeAdapter(EntityGroup.class, new EntityGroupSerializer())
            .excludeFieldsWithoutExposeAnnotation()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static Gson gson () {
        return gson;
    }

    private static class EntityGroupSerializer implements JsonSerializer<EntityGroup> {
        public JsonElement serialize (EntityGroup source, Type type, JsonSerializationContext context) {
            String str = "NONE";
            if (source == EntityGroup.DEFAULT) str = "DEFAULT";
            else if (source == EntityGroup.UNDEAD) str = "UNDEAD";
            else if (source == EntityGroup.ARTHROPOD) str = "ARTHROPOD";
            else if (source == EntityGroup.ILLAGER) str = "ILLAGER";
            else if (source == EntityGroup.AQUATIC) str = "AQUATIC";
            return context.serialize(str);
        }
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
