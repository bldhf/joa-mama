package net.fabricmc.joamama.entity;

import net.minecraft.util.StringIdentifiable;

public enum SlimeSize implements StringIdentifiable {
    SMALL (1),
    MEDIUM (2),
    BIG (4);

    private final int size;

    SlimeSize (int size) {
        this.size = size;
    }

    public int toInt () {
        return this.size;
    }

    public String toString () {
        return this.asString();
    }

    public String asString () {
        return Integer.toString(size);
    }
}
