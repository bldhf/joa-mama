package net.fabricmc.joamama;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class SimpleTrait<S, T> extends Trait<S, T> {
    private final Function<S, T> func;
    private final transient Map<S, T> entries;

    @SuppressWarnings("unused")
    private SimpleTrait () {
        super();
        this.func = null;
        this.entries = null;
    }

    public SimpleTrait (String id, String name, String desc, Function<S, T> func, Set<S> entries) {
        super(id, name,desc);
        this.func = func;
        this.entries = new HashMap<>();
        entries.forEach(entry -> this.entries.put(entry, this.func.apply(entry)));
    }
}