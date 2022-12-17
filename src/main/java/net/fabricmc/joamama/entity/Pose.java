package net.fabricmc.joamama.entity;

import net.minecraft.entity.EntityPose;
import net.minecraft.util.StringIdentifiable;

public enum Pose implements StringIdentifiable {
    STANDING ("standing"),
    FALL_FLYING ("fall_flying"),
    SLEEPING ("sleeping"),
    SWIMMING ("swimming"),
    SPIN_ATTACK ("spin_attack"),
    CROUCHING ("crouching"),
    LONG_JUMPING ("long_jumping"),
    DYING ("dying"),
    CROAKING ("croaking"),
    USING_TONGUE ("using_tongue"),
    SITTING ("sitting"),
    ROARING ("roaring"),
    SNIFFING ("sniffing"),
    EMERGING ("emerging"),
    DIGGING ("digging");
    
    private final String name;
    
    Pose (String name) {
        this.name = name;
    }

    public EntityPose toEntityPose () {
        return EntityPose.values()[this.ordinal()];
    }
    
    public String toString () {
        return this.asString();
    }
    
    public String asString () {
        return this.name;
    }
}
