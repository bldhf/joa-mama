package net.fabricmc.joamama;

import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.joamama.entity.EntityState;
import net.fabricmc.joamama.gson.TraitsGson;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class StateTrait<O, T> implements Trait<T> {
    private static final Gson GSON = TraitsGson.gson();
    private final String id;
    @Expose
    @SerializedName("property_name")
    private final String name;
    @Expose
    @SerializedName("property_description")
    private final String desc;
    @Expose
    @SerializedName("default_value")
    protected T def;
    @Expose
    protected final Table<O, SimpleState, T> entries;

    private StateTrait () {
        this.id = null;
        this.name = null;
        this.desc = null;
        this.entries = null;
    }

    protected StateTrait (String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.entries = HashBasedTable.create();
    }

    public String toString() {
        return GSON.toJson(this);
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

    public T getDefault() {
        return this.def;
    }

    private T getTrait(O owner, SimpleState state) {
        SimpleState newState = new SimpleState(state, this.entries.row(owner).keySet().iterator().next().getEntries());
        return entries.get(owner, newState);
    }

    protected void simplify() {
        SetMultimap<O, Property<?>> notRedundant = MultimapBuilder.hashKeys().hashSetValues().build();
        // Loop through every state owner (eg. Block)
        for (O owner : this.entries.rowKeySet()) {
            // Create a table containing test values.
            Map<SimpleState, T> test = new HashMap<>();
            // Loop through every state that exists for this block.
            for (Map.Entry<SimpleState, T> rowEntry : this.entries.row(owner).entrySet()) {
                SimpleState state = rowEntry.getKey();
                T trait = rowEntry.getValue();
                // Loop through every property-value pair of this state.
                for (Property<?> property : state.getEntries().keySet()) {
                    SimpleState testState = state.without(property);
                    // Check if a state has been found with the given property-value pair.
                    // TODO | a long time ago | the above comment is inaccurate but I don't feel like fixing it right now
                    // Otherwise, add it to the test table.
                    if (test.containsKey(testState)) {
                        // If the test output is not equal to the actual output, the property is not redundant.
                        if (!trait.equals(test.get(testState))) {
                            notRedundant.put(owner, property);
                        }
                    } else {
                        test.put(testState, trait);
                    }
                }
            }
        }

        // Create state managers to get new sets of states with redundant properties removed.
        Map<O, SimpleStateManager> managers = new HashMap<>();
        this.entries.rowKeySet().forEach(
                owner -> managers.put(owner, new SimpleStateManager(notRedundant.get(owner)))
        );
        Table<O, SimpleState, T> newEntries = HashBasedTable.create();
        managers.forEach(
                (owner, manager) -> manager.getStates().forEach(
                        state -> newEntries.put(owner, state, this.getTrait(owner, state))
                )
        );
        this.entries.clear();
        this.entries.putAll(newEntries);
    }

    // TODO | 5/30/2024 | See if setDefault can be merged into simplify to save processing time.
    protected void setDefault() {
        Multiset<T> counts = HashMultiset.create();
        for (Map<SimpleState, T> map : entries.rowMap().values()) {
            if (map.size() == 1) {
                counts.add(map.values().iterator().next());
            }
        }
        Iterator<Multiset.Entry<T>> it = counts.entrySet().iterator();
        Multiset.Entry<T> max = it.next();
        while (it.hasNext()) {
            Multiset.Entry<T> next = it.next();
            if (next.getCount() > max.getCount()) {
                max = next;
            }
        }
        def = max.getElement();

        Set<O> owners = new HashSet<>(entries.rowKeySet());
        for (O owner : owners) {
            Map<SimpleState, T> map = entries.row(owner);
            if (map.size() == 1 && map.values().iterator().next() == def) entries.remove(owner, map.keySet().iterator().next());
        }
    }
}
