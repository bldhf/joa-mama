package net.fabricmc.joamama.entity;

import net.minecraft.util.StringRepresentable;

public enum Pose implements StringRepresentable {
    STANDING("standing"),
    FALL_FLYING("fall_flying"),
    SLEEPING("sleeping"),
    SWIMMING("swimming"),
    SPIN_ATTACK("spin_attack"),
    CROUCHING("crouching"),
    LONG_JUMPING("long_jumping"),
    DYING("dying"),
    CROAKING("croaking"),
    USING_TONGUE("using_tongue"),
    SITTING("sitting"),
    ROARING("roaring"),
    SNIFFING("sniffing"),
    EMERGING("emerging"),
    DIGGING("digging");
    
    private final String name;
    
    Pose(String name) {
        this.name = name;
    }

    public net.minecraft.world.entity.Pose toEntityPose() {
        return net.minecraft.world.entity.Pose.values()[this.ordinal()];
    }
    
    public String toString() {
        return this.getSerializedName();
    }
    
    public String getSerializedName() {
        return this.name;
    }
}
