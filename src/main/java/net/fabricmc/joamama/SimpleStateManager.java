package net.fabricmc.joamama;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.level.block.state.properties.Property;

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
            for (Comparable<?> value : property.getPossibleValues()) {
                states.forEach(state -> newStates.add(new SimpleState(state, property, value)));
            }
            states = new HashSet<>(newStates);
        }
        return states;
    }
}
