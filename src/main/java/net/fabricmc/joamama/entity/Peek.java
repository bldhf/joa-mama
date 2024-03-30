package net.fabricmc.joamama.entity;

import net.minecraft.util.StringRepresentable;

public enum Peek implements StringRepresentable {
    CLOSED("closed", 0),
    OPEN("open", 100);

    private final String name;
    private final int amount;

    Peek(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public int toInt() {
        return this.amount;
    }

    public String toString() {
        return this.getSerializedName();
    }

    public String getSerializedName() {
        return this.name;
    }
}
