package net.fabricmc.joamama;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class SimpleTrait<S, T> extends Trait<S, T> {
    private final Map<S, T> entries;

    @SuppressWarnings("unused")
    private SimpleTrait () {
        super();
        this.entries = null;
    }

    public SimpleTrait (String id, String name, String desc, Function<S, T> func, Set<S> entries) {
        super(id, name,desc, func);
        this.entries = new HashMap<>();
        entries.forEach(entry -> this.entries.put(entry, this.func.apply(entry)));
    }
}