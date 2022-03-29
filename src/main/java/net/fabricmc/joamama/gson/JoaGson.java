package net.fabricmc.joamama.gson;

import com.google.common.collect.Table;
import com.google.gson.*;

import java.lang.reflect.Type;

public class JoaGson {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Table.class, new TableSerializer())
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
}
