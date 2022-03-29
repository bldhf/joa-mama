package net.fabricmc.joamama;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

public class JoaStateProperty <O, S extends State<O, S>, P> extends JoaAbstractProperty<S, P>  {
    private final Table<O, JoaState, P> entries;

    @SuppressWarnings ("unused")
    private JoaStateProperty () {
        super();
        this.entries = null;
    }

    public JoaStateProperty (String id, String name, String desc, Function<S, P> func, SetMultimap<O, S> entries) {
        super(id, name, desc, func);

        this.entries = HashBasedTable.create();
        entries.forEach((owner, state) -> this.entries.put(owner, new JoaState(state.getEntries()), this.func.apply(state)));
    }

    private P getEntry (O owner, JoaState state) {
        JoaState newState = new JoaState(state, this.entries.row(owner).keySet().iterator().next().getEntries());
        return entries.get(owner, newState);
    }

    public void simplify () {
        SetMultimap<O, Property<?>> notRedundant = MultimapBuilder.hashKeys().hashSetValues().build();
        // Loop through every state owner (eg. Block)
        for (O owner : this.entries.rowKeySet()) {
            // Create a table containing test values.
            Map<JoaState, P> test = new HashMap<>();
            // Loop through every state that exists for this block.
            for (Map.Entry<JoaState, P> rowEntry : this.entries.row(owner).entrySet()) {
                JoaState state = rowEntry.getKey();
                P output = rowEntry.getValue();
                // Loop through every property-value pair of this state.
                for (Map.Entry<Property<?>, Comparable<?>> stateEntry : state.entrySet()) {
                    Property<?> property = stateEntry.getKey();
                    Comparable<?> value = stateEntry.getValue();
                    JoaState testState = state.without(property);
                    // Check if a state has been found with the given property-value pair.
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
        Map<O, JoaStateManager> managers = new HashMap<>();
        this.entries.rowKeySet().forEach(
                owner -> managers.put(owner, new JoaStateManager(notRedundant.get(owner)))
        );
        Table<O, JoaState, P> newEntries = HashBasedTable.create();
        managers.forEach(
                (owner, manager) -> manager.getStates().forEach(
                        state -> newEntries.put(owner, state, this.getEntry(owner, state))
                )
        );
        this.entries.clear();
        this.entries.putAll(newEntries);
    }
}