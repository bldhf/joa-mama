package net.fabricmc.joamama;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

import java.util.*;
import java.util.function.Function;

public class StateTrait<O, S extends State<O, S>, P> extends Trait<S, P> {
    private final Table<O, SimpleState, P> entries;

    @SuppressWarnings ("unused")
    private StateTrait () {
        super();
        this.entries = null;
    }

    public StateTrait (String id, String name, String desc, Function<S, P> func, SetMultimap<O, S> entries) {
        super(id, name, desc, func);

        this.entries = HashBasedTable.create();
        entries.forEach((owner, state) -> this.entries.put(owner, new SimpleState(state.getEntries()), this.func.apply(state)));
    }

    private P getEntry (O owner, SimpleState state) {
        SimpleState newState = new SimpleState(state, this.entries.row(owner).keySet().iterator().next().getEntries());
        return entries.get(owner, newState);
    }

    public void simplify () {
        SetMultimap<O, Property<?>> notRedundant = MultimapBuilder.hashKeys().hashSetValues().build();
        // Loop through every state owner (eg. Block)
        for (O owner : this.entries.rowKeySet()) {
            // Create a table containing test values.
            Map<SimpleState, P> test = new HashMap<>();
            // Loop through every state that exists for this block.
            for (Map.Entry<SimpleState, P> rowEntry : this.entries.row(owner).entrySet()) {
                SimpleState state = rowEntry.getKey();
                P output = rowEntry.getValue();
                // Loop through every property-value pair of this state.
                for (Map.Entry<Property<?>, Comparable<?>> stateEntry : state.entrySet()) {
                    Property<?> property = stateEntry.getKey();
                    Comparable<?> value = stateEntry.getValue();
                    SimpleState testState = state.without(property);
                    // Check if a state has been found with the given property-value pair.
                    // TODO: the above comment is inaccurate but i don't feel like fixing it right now
                    // Otherwise, add it to the test table.
                    if (test.containsKey(testState)) {
                        // If the test output is not equal to the actual output, the property is not redundant.
                        if (!output.equals(test.get(testState))) {
                            notRedundant.put(owner, property);
                        }
                    } else {
                        test.put(testState, output);
                    }
                }
            }
        }

        // Create state managers to get new sets of states with redundant properties removed.
        Map<O, SimpleStateManager> managers = new HashMap<>();
        this.entries.rowKeySet().forEach(
                owner -> managers.put(owner, new SimpleStateManager(notRedundant.get(owner)))
        );
        Table<O, SimpleState, P> newEntries = HashBasedTable.create();
        managers.forEach(
                (owner, manager) -> manager.getStates().forEach(
                        state -> newEntries.put(owner, state, this.getEntry(owner, state))
                )
        );
        this.entries.clear();
        this.entries.putAll(newEntries);
    }
}
