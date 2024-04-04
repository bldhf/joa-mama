package net.fabricmc.joamama;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class TraitCollection<T extends Trait<?>, C> {
    private final Map<String, T> traits;
    private final BiConsumer<T, C> loader; // This should be the load method of the trait.
    private C entries = null;

    public TraitCollection(BiConsumer<T, C> loader) {
        this.traits = new HashMap<>();
        this.loader = loader;
    }

    public void load(C entries) {
        this.entries = entries;
    }

    public void add(T trait) {
        this.traits.put(trait.getId(), trait);
    }

    public T loadTrait(String id) {
        this.loader.accept(this.traits.get(id), entries);
        return this.traits.get(id);
    }

    public T get(String id) {
        return this.traits.get(id);
    }

    public Set<String> getIds() {
        return this.traits.keySet();
    }
}
