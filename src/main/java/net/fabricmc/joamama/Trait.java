package net.fabricmc.joamama;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.joamama.gson.TraitsGson;

import java.util.function.Function;

public class Trait<T, P> {
    protected static final Gson GSON = TraitsGson.gson();
    protected final String id;
    @SerializedName ("property_name")
    protected final String name;
    @SerializedName ("property_description")
    protected final String desc;
    protected final transient Function<T, P> func;

    protected Trait () {
        this.id = null;
        this.name = null;
        this.desc = null;
        this.func = null;
    }

    protected Trait (String id, String name, String desc, Function<T, P> func) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.func = func;
    }

    public String toString () {
        return GSON.toJson(this);
    }
}