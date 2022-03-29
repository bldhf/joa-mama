package net.fabricmc.joamama;

import net.minecraft.state.property.Property;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SimpleStateManager {
    private final Set<Property<?>> properties;

    public SimpleStateManager (Collection<Property<?>> properties) {
        this.properties = new HashSet<>(properties);
    }

    public Set<SimpleState> getStates () {
        Set<SimpleState> states = new HashSet<>();
        states.add(new SimpleState());
        for (Property<?> property : properties) {
            Set<SimpleState> newStates = new HashSet<>();
            for (Comparable<?> value : property.getValues()) {
                states.forEach(state -> newStates.add(new SimpleState(state, property, value)));
            }
            states = new HashSet<>(newStates);
        }
        return states;
    }
}
