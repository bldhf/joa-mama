package net.fabricmc.joamama.entity;

import net.fabricmc.joamama.JoaMama;
import net.fabricmc.joamama.mixin.ShulkerAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public abstract class EntityProperties {
    public static final BooleanProperty IS_BABY = BooleanProperty.create("is_baby");
    public static final BooleanProperty MARKER = BooleanProperty.create("marker");
    public static final EnumProperty<Peek> PEEK = EnumProperty.create("peek", Peek.class);
    public static final EnumProperty<DragonPhase> DRAGON_PHASE = EnumProperty.create("phase", DragonPhase.class);
    public static final EnumProperty<WitherPhase> WITHER_PHASE = EnumProperty.create("phase", WitherPhase.class);
    public static final EnumProperty<Pose> CAMEL_POSE = EnumProperty.create("pose", Pose.class, Pose.STANDING, Pose.SITTING);
    public static final EnumProperty<Pose> GOAT_POSE = EnumProperty.create("pose", Pose.class, Pose.STANDING, Pose.LONG_JUMPING);
    public static final EnumProperty<Pose> PLAYER_POSE = EnumProperty.create("pose", Pose.class, Pose.STANDING, Pose.FALL_FLYING, Pose.SLEEPING, Pose.SWIMMING, Pose.SPIN_ATTACK, Pose.CROUCHING, Pose.DYING);
    public static final EnumProperty<Pose> VILLAGER_POSE = EnumProperty.create("pose", Pose.class, Pose.STANDING, Pose.SLEEPING);
    public static final EnumProperty<Pose> WARDEN_POSE = EnumProperty.create("pose", Pose.class, Pose.STANDING, Pose.ROARING, Pose.SNIFFING, Pose.EMERGING, Pose.DIGGING);
    public static final IntegerProperty PUFF_STATE = IntegerProperty.create("puff_state", 0, 2);
    public static final BooleanProperty HAS_PUMPKIN = BooleanProperty.create("has_pumpkin");
    public static final BooleanProperty SHEARED = BooleanProperty.create("sheared");
    public static final EnumProperty<SlimeSize> SLIME_SIZE = EnumProperty.create("size", SlimeSize.class);
    public static final BooleanProperty SMALL = BooleanProperty.create("small");
    public static final BooleanProperty IS_TAMED = BooleanProperty.create("is_tamed");
    public static final EnumProperty<Boat.Type> BOAT_VARIANT = EnumProperty.create("variant", Boat.Type.class);
    public static final EnumProperty<Panda.Gene> PANDA_VARIANT = EnumProperty.create("variant", Panda.Gene.class);

    public static Entity setArmorStandFlags(EntityState state, Entity entity) {
        if (entity instanceof ArmorStand) {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("Marker", (Boolean) state.get(MARKER));
            nbt.putBoolean("Small", (Boolean) state.get(SMALL));
            ((ArmorStand) entity).readAdditionalSaveData(nbt);
        }
        return entity;
    }

    public static Entity setBaby(EntityState state, Entity entity) {
        if (entity instanceof AgeableMob) {
            ((AgeableMob) entity).setBaby((Boolean) state.get(IS_BABY));
        } else if (entity instanceof Piglin) {
            ((Piglin) entity).setBaby((Boolean) state.get(IS_BABY));
        } else if (entity instanceof Zoglin) {
            ((Zoglin) entity).setBaby((Boolean) state.get(IS_BABY));
        } else if (entity instanceof Zombie) {
            ((Zombie) entity).setBaby((Boolean) state.get(IS_BABY));
        }
        return entity;
    }

    public static Entity setPeek(EntityState state, Entity entity) {
        if (entity instanceof Shulker) {
            int peek = ((Peek) state.get(PEEK)).toInt();
            ((ShulkerAccessor) entity).invokeSetRawPeekAmount(peek);
            ((ShulkerAccessor) entity).setCurrentPeekAmount(peek * 0.01F);
        }
        return entity;
    }

    public static Entity setPhase(EntityState state, Entity entity) {
        if (entity instanceof EnderDragon) {
            ((EnderDragon) entity).getPhaseManager().setPhase(((DragonPhase) state.get(DRAGON_PHASE)).toEnderDragonPhase());
        } else if (entity instanceof WitherBoss) {
            switch ((WitherPhase) state.get(WITHER_PHASE)) {
                case SPAWN -> ((WitherBoss) entity).makeInvulnerable();
                case NORMAL -> {}
                case SHIELD -> ((WitherBoss) entity).setHealth(150);
            }
        }
        return entity;
    }

    public static Entity setPose(EntityState state, Entity entity) {
        if (entity instanceof Camel) {
            entity.setPose(((Pose) state.get(CAMEL_POSE)).toEntityPose());
        } else if (entity instanceof Goat) {
            entity.setPose(((Pose) state.get(GOAT_POSE)).toEntityPose());
        } else if (entity instanceof Player) {
            entity.setPose(((Pose) state.get(PLAYER_POSE)).toEntityPose());
        } else if (entity instanceof Villager) {
            entity.setPose(((Pose) state.get(VILLAGER_POSE)).toEntityPose());
        } else if (entity instanceof Warden) {
            entity.setPose(((Pose) state.get(WARDEN_POSE)).toEntityPose());
        }
        return entity;
    }

    public static Entity setPuffState(EntityState state, Entity entity) {
        if (entity instanceof Pufferfish) {
            ((Pufferfish) entity).setPuffState((Integer) state.get(PUFF_STATE));
        }
        return entity;
    }

    public static Entity setSheared(EntityState state, Entity entity) {
        if (entity instanceof Sheep) {
            ((Sheep) entity).setSheared((Boolean) state.get(SHEARED));
        } else if (entity instanceof SnowGolem) {
            ((SnowGolem) entity).setPumpkin((Boolean) state.get(HAS_PUMPKIN));
        }
        return entity;
    }

    public static Entity setSize(EntityState state, Entity entity) {
        if (entity instanceof Slime) {
            ((Slime) entity).setSize(((SlimeSize) state.get(SLIME_SIZE)).toInt(), true);
        }
        return entity;
    }

    public static Entity setIsTamed(EntityState state, Entity entity) {
        if (entity instanceof AbstractHorse) {
            ((AbstractHorse) entity).setTamed((Boolean) state.get(IS_TAMED));
        } else if (entity instanceof TamableAnimal) {
            ((TamableAnimal) entity).setTame((Boolean) state.get(IS_TAMED));
        }
        return entity;
    }

    public static Entity setVariant(EntityState state, Entity entity) {
        if (entity instanceof Boat) {
            ((Boat) entity).setVariant((Boat.Type) state.get(BOAT_VARIANT));
        } else if (entity instanceof Panda) {
            ((Panda) entity).setMainGene((Panda.Gene) state.get(PANDA_VARIANT));
            ((Panda) entity).setHiddenGene((Panda.Gene) state.get(PANDA_VARIANT));
        }
        return entity;
    }
}
