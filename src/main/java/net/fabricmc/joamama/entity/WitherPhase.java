package net.fabricmc.joamama.entity;

import net.minecraft.util.StringRepresentable;

public enum WitherPhase implements StringRepresentable {
    SPAWN("spawn"),
    NORMAL("normal"),
    SHIELD("shield");

    private final String name;

    WitherPhase(String name) {
        this.name = name;
    }

    public String toString() {
        return this.getSerializedName();
    }

    public String getSerializedName() {
        return this.name;
    }
}
