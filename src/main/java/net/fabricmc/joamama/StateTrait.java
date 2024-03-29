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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StateTrait<O, T> implements Trait {
    private static final Gson GSON = TraitsGson.gson();
    @Expose
    private final String id;
    @Expose
    @SerializedName("property_name")
    private final String name;
    @Expose
    @SerializedName ("property_description")
    private final String desc;
    @Expose
    private final Table<O, SimpleState, T> entries;

    @SuppressWarnings ("unused")
    private StateTrait () {
        this.id = null;
        this.name = null;
        this.desc = null;
        this.entries = null;
    }

    public <S extends StateHolder<O, S>> StateTrait (String id, String name, String desc, BiFunction<O, S, T> func, SetMultimap<O, S> entries) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.entries = HashBasedTable.create();
        entries.forEach((owner, state) -> this.entries.put(owner, new SimpleState(state.getValues()), func.apply(owner, state)));

        this.simplify();
    }

    public StateTrait (String id, String name, String desc, Function<Entity, T> func, SetMultimap<O, EntityState> entries) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.entries = HashBasedTable.create();
        entries.forEach((owner, state) -> this.entries.put(owner, new SimpleState(state.getEntries()), func.apply(state.entity())));

        this.simplify();
    }

    public String toString () {
        return GSON.toJson(this);
    }

    public String getId () {
        return this.id;
    }

    public String getName () {
        return this.name;
    }

    public String getDesc () {
        return this.desc;
    }

    private T getTrait (O owner, SimpleState state) {
        SimpleState newState = new SimpleState(state, this.entries.row(owner).keySet().iterator().next().getEntries());
        return entries.get(owner, newState);
    }

    private void simplify () {
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
                    // TODO: the above comment is inaccurate but i don't feel like fixing it right now
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
