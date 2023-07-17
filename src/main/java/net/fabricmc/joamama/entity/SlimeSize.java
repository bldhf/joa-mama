package net.fabricmc.joamama.entity;

import net.minecraft.util.StringRepresentable;

public enum SlimeSize implements StringRepresentable {
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
        return this.getSerializedName();
    }

    public String getSerializedName () {
        return Integer.toString(size);
    }
}
