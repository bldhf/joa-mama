package net.fabricmc.joamama;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

public class SimpleTrait<E, P> extends Trait<E, P> {
    private final HashMap<E, P> entries;

    @SuppressWarnings("unused")
    private SimpleTrait () {
        super();
        this.entries = null;
    }

    public SimpleTrait (String id, String name, String desc, Function<E, P> func, Set<E> entries) {
        super(id, name,desc, func);
        this.entries = new HashMap<>();
        entries.forEach(entry -> this.entries.put(entry, this.func.apply(entry)));
    }
}