package net.fabricmc.joamama;

import net.minecraft.state.property.Property;

import java.util.*;

public class JoaStateManager {
    private final Set<Property<?>> properties;

    public JoaStateManager (Collection<Property<?>> properties) {
        this.properties = new HashSet<>(properties);
    }

    public Set<JoaState> getStates () {
        Set<JoaState> states = new HashSet<>();
        states.add(new JoaState());
        for (Property<?> property : properties) {
            Set<JoaState> newStates = new HashSet<>();
            for (Comparable<?> value : property.getValues()) {
                states.forEach(state -> newStates.add(new JoaState(state, property, value)));
            }
            states = new HashSet<>(newStates);
        }
        return states;
    }
}
