package net.fabricmc.joamama.gson;

import com.google.common.collect.Table;
import com.google.gson.*;
import net.fabricmc.joamama.JoaMama;
import net.fabricmc.joamama.SimpleState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TraitsGson {
    private static final Gson gsonRow = new GsonBuilder()
        .registerTypeAdapter(Table.class, new TableSerializerRow())
        .registerTypeAdapter(Map.class, new MapSerializerRow())
        .registerTypeAdapter(EntityType.class, new EntityTypeSerializer())
        .excludeFieldsWithoutExposeAnnotation()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();
    private static final Gson gsonCell = new GsonBuilder()
        .registerTypeAdapter(Table.class, new TableSerializerCell())
        .registerTypeAdapter(Map.class, new MapSerializerCell())
        .registerTypeAdapter(EntityType.class, new EntityTypeSerializer())
        .excludeFieldsWithoutExposeAnnotation()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();

    public static Gson gsonRow() {
        return gsonRow;
    }

    public static Gson gsonCell() {
        return gsonCell;
    }

    private static class TableSerializerRow implements JsonSerializer<Table> {
        public JsonElement serialize(Table source, Type type, JsonSerializationContext context) {
            return context.serialize(source.rowMap());
        }
    }


    private static class TableSerializerCell implements JsonSerializer<Table> {
        public JsonElement serialize(Table source, Type type, JsonSerializationContext context) {
            return context.serialize(source.cellSet().stream().collect(Collectors.toMap((Table.Cell cell) -> JoaMama.toString(cell.getRowKey()) + addBraces(JoaMama.toString(cell.getColumnKey())), Table.Cell::getValue)));

        }

        private String addBraces(String string) {
            return string.length() > 0 ? "[" + string + "]" : "";
        }
    }

    @SuppressWarnings("rawtypes")
    private static class MapSerializerRow implements JsonSerializer<Map> {
        private static final Gson mapGson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        public JsonElement serialize(Map source, Type type, JsonSerializationContext context) {
            if (source.size() == 1) {
                return context.serialize(source.values().iterator().next());
            } else {
                return mapGson.toJsonTree(source);
            }
        }
    }

    private static class MapSerializerCell implements JsonSerializer<Map> {
        private static final Gson mapGson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

        public JsonElement serialize(Map source, Type type, JsonSerializationContext context) {
            if (source.size() == 1) {
                return context.serialize(source.values().iterator().next());
            } else {
                return mapGson.toJsonTree(source);
            }
        }
    }

    private static class EntityTypeSerializer implements JsonSerializer<EntityType> {
        public JsonElement serialize(EntityType source, Type type, JsonSerializationContext context) {
            return context.serialize(BuiltInRegistries.ENTITY_TYPE.getKey(source).getPath());
        }
    }
}
