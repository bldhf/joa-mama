package net.fabricmc.joamama;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.joamama.entity.EntityState;
import net.fabricmc.joamama.gson.TraitsGson;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StateTrait<O, S, T>{
    private Gson gson;
    @Expose
    private final String id;
    @Expose
    @SerializedName("property_name")
    private final String name;
    @Expose
    @SerializedName ("property_description")
    private final String description;
    @Expose
    protected final Table<O, SimpleState, T> entries;
    private final BiFunction<O, S, T> function;

    private StateTrait() {
        this.gson = null;
        this.id = null;
        this.name = null;
        this.description = null;
        this.entries = null;
        this.function = null;
    }

    private StateTrait(String id, String name, String description, BiFunction<O, S, T> function) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.entries = HashBasedTable.create();
        this.function = function;
    }

    public static <O, S, T> StateTrait<O, S, T> create(String id, String name, String description, BiFunction<O, S, T> function) {
        return new StateTrait<>(id, name, description, function);
    }

    public static <O, T> StateTrait<O, O, T> create(String id, String name, String description, Function<O, T> function) {
        return new StateTrait<>(id, name, description, (owner, state) -> function.apply(owner));
    }

    public void load(Map<O, SimpleStateManager> entries, BiFunction<O, SimpleState, S> factory, boolean simplify, boolean cell) {
        if (cell) gson = TraitsGson.gsonCell();
        else gson = TraitsGson.gsonRow();
        entries.forEach((owner, manager) -> manager.getStates().forEach(state -> this.entries.put(owner, state, this.function.apply(owner, factory.apply(owner, state)))));
        if (simplify) this.simplify();
    }

    public void load(Iterable<O> entries, Function<O, S> factory) {
        gson = TraitsGson.gsonRow();
        entries.forEach(owner -> this.entries.put(owner, new SimpleState(), this.function.apply(owner, factory.apply(owner))));
    }

    public String toString() {
        return gson.toJson(this);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
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
}
