package net.fabricmc.joamama;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.joamama.gson.TraitsGson;

import java.util.HashMap;
import java.util.function.Function;

public class SimpleTrait<T, P> implements Trait {
    private static final Gson gson;
    @Expose
    private final String id;
    @Expose
    @SerializedName("property_name")
    private final String name;
    @Expose
    @SerializedName("property_description")
    private final String desc;
    @Expose
    private final HashMap<String, P> entries;

    static {
        gson = TraitsGson.gson();
    }

    private SimpleTrait() {
        this.id = this.name = this.desc = null;
        this.entries = null;
    }

    public SimpleTrait(String id, String name, String desc, Function<T, P> func, Iterable<T> entries) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.entries = new HashMap<>();
        entries.forEach(entry -> this.entries.put(entry.toString(), func.apply(entry)));
    }

    public SimpleTrait(String id, String name, String desc, Function<T, P> func, Function<T, String> toString, Iterable<T> entries) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.entries = new HashMap<>();
        entries.forEach(entry -> this.entries.put(toString.apply(entry), func.apply(entry)));
    }

    public String toString () {
        return gson.toJson(this);
    }

    public String getId () {
        return this.id;
    }

    public String getName () {
        return this.name;
    }

    public String getDesc () {
        return this.desc;
    }
}