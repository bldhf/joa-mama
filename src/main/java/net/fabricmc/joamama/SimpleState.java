package net.fabricmc.joamama;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.state.properties.Property;

public class SimpleState {
    private final Map<Property<?>, Comparable<?>> entries;

    public SimpleState () {
        this.entries = new HashMap<>();
    }

    public SimpleState (SimpleState other) {
        this.entries = other.getEntries();
    }

    public SimpleState (Map<Property<?>, Comparable<?>> entries) {
        this.entries = new HashMap<>(entries);
    }

    public SimpleState (SimpleState source, Property<?> property, Comparable<?> value) {
        this.entries = new HashMap<>(source.getEntries());
        this.entries.putIfAbsent(property, value);
    }

    public SimpleState (SimpleState source, Map<Property<?>, Comparable<?>> entries) {
        this.entries = new HashMap<>(source.getEntries());
        entries.forEach(this.entries::putIfAbsent);
    }

    public boolean equals (Object object) {
        if (object == null) return false;
        if (object == this) return true;
        if (object.getClass() != this.getClass()) return false;
        SimpleState other = (SimpleState) object;
        return new EqualsBuilder()
                .append(this.entries, other.entries)
                .isEquals();
    }

    public int hashCode () {
        return new HashCodeBuilder(11, 47)
                .append(entries)
                .toHashCode();
    }

    public String toString () {
        return this.entries
                .entrySet()
                .stream()
                .map(entry -> entry.getKey().getName() + ": " + entry.getValue().toString())
                .collect(Collectors.joining(", "));
    }

    public void remove (Property<?> property) {
        entries.remove(property);
    }

    public Map<Property<?>, Comparable<?>> getEntries () {
        return new HashMap<>(this.entries);
    }

    public Set<Map.Entry<Property<?>, Comparable<?>>> entrySet () {
        return this.entries.entrySet();
    }

    public SimpleState without (Property<?> property) {
        SimpleState state = new SimpleState(this);
        state.remove(property);
        return state;
    }
}