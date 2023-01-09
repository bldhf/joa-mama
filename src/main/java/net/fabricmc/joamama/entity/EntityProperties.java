package net.fabricmc.joamama.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;

public abstract class EntityProperties {
    public static final BooleanProperty IS_BABY = BooleanProperty.of("is_baby");
    public static final BooleanProperty MARKER = BooleanProperty.of("marker");
    public static final EnumProperty<Pose> CAMEL_POSE = EnumProperty.of("pose", Pose.class, Pose.STANDING, Pose.SITTING);
    public static final EnumProperty<Pose> GOAT_POSE = EnumProperty.of("pose", Pose.class, Pose.STANDING, Pose.LONG_JUMPING);
    public static final EnumProperty<Pose> PLAYER_POSE = EnumProperty.of("pose", Pose.class, Pose.STANDING, Pose.FALL_FLYING, Pose.SLEEPING, Pose.SWIMMING, Pose.SPIN_ATTACK, Pose.CROUCHING, Pose.DYING);
    public static final EnumProperty<Pose> VILLAGER_POSE = EnumProperty.of("pose", Pose.class, Pose.STANDING, Pose.SLEEPING);
    public static final EnumProperty<Pose> WARDEN_POSE = EnumProperty.of("pose", Pose.class, Pose.STANDING, Pose.ROARING, Pose.SNIFFING, Pose.EMERGING, Pose.DIGGING);
    public static final IntProperty PUFF_STATE = IntProperty.of("puff_state", 0, 2);
    public static final BooleanProperty HAS_PUMPKIN = BooleanProperty.of("has_pumpkin");
    public static final BooleanProperty SHEARED = BooleanProperty.of("sheared");
    public static final EnumProperty<SlimeSize> SLIME_SIZE = EnumProperty.of("size", SlimeSize.class);
    public static final BooleanProperty SMALL = BooleanProperty.of("small");
    public static final BooleanProperty IS_TAMED = BooleanProperty.of("is_tamed");
    public static final EnumProperty<BoatEntity.Type> BOAT_VARIANT = EnumProperty.of("variant", BoatEntity.Type.class);
    public static final EnumProperty<PandaEntity.Gene> PANDA_VARIANT = EnumProperty.of("variant", PandaEntity.Gene.class);

    public static Entity setArmorStandFlags (EntityState state, Entity entity) {
        if (entity instanceof ArmorStandEntity) {
            NbtCompound nbt = new NbtCompound();
            nbt.putBoolean("Marker", (Boolean) state.get(MARKER));
            nbt.putBoolean("Small", (Boolean) state.get(SMALL));
            ((ArmorStandEntity) entity).readCustomDataFromNbt(nbt);
        }
        return entity;
    }

    public static Entity setIsBaby (EntityState state, Entity entity) {
        if (entity instanceof PassiveEntity) {
            ((PassiveEntity) entity).setBaby((Boolean) state.get(IS_BABY));
        } else if (entity instanceof PiglinEntity) {
            ((PiglinEntity) entity).setBaby((Boolean) state.get(IS_BABY));
        } else if (entity instanceof ZoglinEntity) {
            ((ZoglinEntity) entity).setBaby((Boolean) state.get(IS_BABY));
        } else if (entity instanceof ZombieEntity) {
            ((ZombieEntity) entity).setBaby((Boolean) state.get(IS_BABY));
        }
        return entity;
    }

    public static Entity setPose (EntityState state, Entity entity) {
        if (entity instanceof CamelEntity) {
            entity.setPose(((Pose) state.get(CAMEL_POSE)).toEntityPose());
        } else if (entity instanceof GoatEntity) {
            entity.setPose(((Pose) state.get(GOAT_POSE)).toEntityPose());
        } else if (entity instanceof PlayerEntity) {
            entity.setPose(((Pose) state.get(PLAYER_POSE)).toEntityPose());
        } else if (entity instanceof VillagerEntity) {
            entity.setPose(((Pose) state.get(VILLAGER_POSE)).toEntityPose());
        } else if (entity instanceof WardenEntity) {
            entity.setPose(((Pose) state.get(WARDEN_POSE)).toEntityPose());
        }
        return entity;
    }

    public static Entity setPuffState (EntityState state, Entity entity) {
        if (entity instanceof PufferfishEntity) {
            ((PufferfishEntity) entity).setPuffState((Integer) state.get(PUFF_STATE));
        }
        return entity;
    }

    public static Entity setSheared (EntityState state, Entity entity) {
        if (entity instanceof SheepEntity) {
            ((SheepEntity) entity).setSheared((Boolean) state.get(SHEARED));
        } else if (entity instanceof SnowGolemEntity) {
            ((SnowGolemEntity) entity).setHasPumpkin((Boolean) state.get(HAS_PUMPKIN));
        }
        return entity;
    }

    public static Entity setSize (EntityState state, Entity entity) {
        if (entity instanceof SlimeEntity) {
            ((SlimeEntity) entity).setSize(((SlimeSize) state.get(SLIME_SIZE)).toInt(), true);
        }
        return entity;
    }

    public static Entity setIsTamed (EntityState state, Entity entity) {
        if (entity instanceof AbstractHorseEntity) {
            ((AbstractHorseEntity) entity).setTame((Boolean) state.get(IS_TAMED));
        } else if (entity instanceof TameableEntity) {
            ((TameableEntity) entity).setTamed((Boolean) state.get(IS_TAMED));
        }
        return entity;
    }

    public static Entity setVariant (EntityState state, Entity entity) {
        if (entity instanceof BoatEntity) {
            ((BoatEntity) entity).setVariant((BoatEntity.Type) state.get(BOAT_VARIANT));
        } else if (entity instanceof PandaEntity) {
            ((PandaEntity) entity).setMainGene((PandaEntity.Gene) state.get(PANDA_VARIANT));
            ((PandaEntity) entity).setHiddenGene((PandaEntity.Gene) state.get(PANDA_VARIANT));
        }
        return entity;
    }
}
