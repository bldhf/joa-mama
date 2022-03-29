package net.fabricmc.joamama;

import net.minecraft.state.property.Property;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class JoaState {
    private final Map<Property<?>, Comparable<?>> entries;

    public JoaState () {
        this.entries = new HashMap<>();
    }

    public JoaState (JoaState other) {
        this.entries = other.getEntries();
    }

    public JoaState (Map<Property<?>, Comparable<?>> entries) {
        this.entries = new HashMap<>(entries);
    }

    public JoaState (JoaState source, Property<?> property, Comparable<?> value) {
        this.entries = new HashMap<>(source.getEntries());
        this.entries.putIfAbsent(property, value);
    }

    public JoaState (JoaState source, Map<Property<?>, Comparable<?>> entries) {
        this.entries = new HashMap<>(source.getEntries());
        entries.forEach(this.entries::putIfAbsent);
    }

    public boolean equals (Object object) {
        if (object == null) return false;
        if (object == this) return true;
        if (object.getClass() != this.getClass()) return false;
        JoaState other = (JoaState) object;
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

    public JoaState without (Property<?> property) {
        JoaState state = new JoaState(this);
        state.remove(property);
        return state;
    }
}