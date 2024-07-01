package net.fabricmc.joamama;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.joamama.gson.TraitsGson;

import java.util.HashMap;
import java.util.function.Function;

public class SimpleTrait<O, T> implements Trait<T> {
    private static final Gson gson;
    @Expose
    private final String id;
    @Expose
    @SerializedName("property_name")
    private final String name;
    @Expose
    @SerializedName("property_description")
    private final String desc;
    private final String definition;
    private final Function<O, T> func;
    private final Function<O, String> toString;
    @Expose
    private final HashMap<String, T> entries;

    static {
        gson = TraitsGson.gson();
    }

    private SimpleTrait() {
        this.id = this.name = this.desc = null;
        this.func = null;
        this.definition = null;
        this.toString = null;
        this.entries = null;
    }

    public SimpleTrait(String id, String name, String desc, String definition, Function<O, T> func) {
        this(id, name, desc, definition, func, Object::toString);
    }

    public SimpleTrait(String id, String name, String desc, String definition, Function<O, T> func, Function<O, String> toString) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.definition = definition;
        this.func = func;
        this.toString = toString;
        this.entries = new HashMap<>();
    }

    public void load(Iterable<O> entries) {
        entries.forEach(entry -> this.entries.put(toString.apply(entry), func.apply(entry)));
    }

    public String toString() {
        return gson.toJson(this);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }
}