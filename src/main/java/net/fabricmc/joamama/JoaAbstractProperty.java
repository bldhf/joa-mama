package net.fabricmc.joamama;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.joamama.gson.JoaGson;

import java.util.function.Function;

public class JoaAbstractProperty <T, P> {
    protected static final Gson GSON = JoaGson.gson();
    protected final String id;
    @SerializedName ("property_name")
    protected final String name;
    @SerializedName ("property_description")
    protected final String desc;
    protected final transient Function<T, P> func;

    protected JoaAbstractProperty () {
        this.id = null;
        this.name = null;
        this.desc = null;
        this.func = null;
    }

    protected JoaAbstractProperty (String id, String name, String desc, Function<T, P> func) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.func = func;
    }

    public String toString() {
        return GSON.toJson(this);
    }
}