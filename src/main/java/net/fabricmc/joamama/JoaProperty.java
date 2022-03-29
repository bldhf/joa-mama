package net.fabricmc.joamama;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

public class JoaProperty <E, P> extends JoaAbstractProperty <E, P> {
    private final HashMap<E, P> entries;

    @SuppressWarnings("unused")
    private JoaProperty () {
        super();
        this.entries = null;
    }

    public JoaProperty (String id, String name, String desc, Function<E, P> func, Set<E> entries) {
        super(id, name,desc, func);
        this.entries = new HashMap<>();
        entries.forEach(entry -> this.entries.put(entry, this.func.apply(entry)));
    }
}