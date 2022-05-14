package net.fabricmc.joamama;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.joamama.gson.TraitsGson;

import java.util.HashMap;
import java.util.function.Function;

public class JoaProperty <T, P> {
    private static final Gson gson;
    @Expose
    private final String id;
    @Expose
    @SerializedName("property_name")
    private final String name;
    @Expose
    @SerializedName("property_description")
    private final String desc;
    private final transient Function<T, P> func;
    @Expose
    private final HashMap<T, P> entries;

    static {
        gson = TraitsGson.gson();
    }

    private JoaProperty () {
        this.id = this.name = this.desc = null;
        this.func = (t -> null);
        this.entries = null;
    }

    public JoaProperty (String id, String name, String desc, Function<T, P> func, Iterable<T> entries) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.func = func;
        this.entries = new HashMap<>();
        entries.forEach(entry -> this.entries.put(entry, this.func.apply(entry)));
    }

    public String toString () {
        return gson.toJson(this);
    }
}