package net.fabricmc.joamama;

import net.fabricmc.joamama.gson.TraitsGson;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class TraitCollection<O, S> {
    private final Map<String, StateTrait<O, S, ?>> traits;
    private final Map<O, SimpleStateManager> entries;
    private final BiFunction<O, SimpleState, S> factory;
    private final boolean simplify;
    private final boolean cell;

    public TraitCollection(BiFunction<O, SimpleState, S> factory, boolean simplify, boolean cell) {
        this.traits = new HashMap<>();
        this.entries = new HashMap<>();
        this.factory = factory;
        this.simplify = simplify;
        this.cell = cell;
    }
    public void load(Map<O, SimpleStateManager> entries) {
        this.entries.putAll(entries);
    }

    public List<StateTrait<O, S, ?>> loadTraits(Iterable<String> ids) {
        List<StateTrait<O, S, ?>> traits = new ArrayList<>();
        for (String id : ids) {
            StateTrait<O, S, ?> trait = this.traits.get(id);
            trait.load(this.entries, this.factory, this.simplify, this.cell);
            traits.add(trait);
        }
        return traits;
    }

    public void add(StateTrait<O, S, ?> trait) {
        this.traits.put(trait.getId(), trait);
    }

    public StateTrait<O, S, ?> get(String id) {
        return this.traits.get(id);
    }

    public Set<String> getIds() {
        return this.traits.keySet();
    }
}
