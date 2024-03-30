package net.fabricmc.joamama.entity;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;

public enum DragonPhase implements StringRepresentable {
    HOLDING_PATTERN("holding_pattern"),
    STRAFE_PLAYER("strafe_player"),
    LANDING_APPROACH("landing_approach"),
    LANDING("landing"),
    TAKEOFF("takeoff"),
    SITTING_FLAMING("sitting_flaming"),
    SITTING_SCANNING("sitting_scanning"),
    SITTING_ATTACKING("sitting_attacking"),
    CHARGING_PLAYER("charging_player"),
    DYING("dying"),
    HOVERING("hovering");

    private final String name;

    DragonPhase(String name) {
        this.name = name;
    }

    public EnderDragonPhase<?> toEnderDragonPhase() {
        return EnderDragonPhase.getById(this.ordinal());
    }

    public String toString() {
        return this.getSerializedName();
    }

    public String getSerializedName() {
        return this.name;
    }
}
