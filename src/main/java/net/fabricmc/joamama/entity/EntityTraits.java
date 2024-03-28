package net.fabricmc.joamama.entity;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.fabricmc.joamama.JoaMama;
import net.fabricmc.joamama.StateTrait;
import net.fabricmc.joamama.mixin.EntityTypeAccessor;
import net.minecraft.core.Registry;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class EntityTraits {
    private static final SetMultimap<EntityType<?>, EntityState> entityStates;
    private static Registry<MobEffect> effects;

    static {
        entityStates = MultimapBuilder.hashKeys().hashSetValues().build();
    }

    public static void load (Iterable<EntityType<?>> entityTypes) {
        entityTypes.forEach(type -> entityStates.putAll(type, EntityStateManager.getEntityStateManager(type).getStates()));
    }

    public static void load_effect (Registry<MobEffect> effects) {
        EntityTraits.effects = effects;
    }

    private static Object getAttributeValueIfPresent (Entity entity, Attribute attribute) {
        return entity instanceof LivingEntity && ((LivingEntity) entity).getAttributes().hasAttribute(attribute) ? ((LivingEntity) entity).getAttributeValue(attribute) : "N/A";
    }

    public static void getTheWholeThing (List<StateTrait<EntityType<?>, ?>> arr) {
        arr.add(new StateTrait<>(
                "width",
                "Width",
                "The width of this entity. All entity hitboxes except for item frames and paintings have square horizontal cross-sections, so there is no distinction between length and width.",
                Entity::getBbWidth,
                entityStates));
        arr.add(new StateTrait<>(
                "height",
                "Height",
                "The height of this entity.",
                Entity::getBbHeight,
                entityStates));
        arr.add(new StateTrait<>(
                "spawns_inside_hazards",
                "Spawns Inside Hazards",
                "Normally dangerous blocks that this entity can spawn inside.\nThis only includes wither roses, sweet berry bushes, and powder snow.\nSee Fire Immune for entities that can spawn inside of fire.",
                entity -> ((EntityTypeAccessor) entity.getType()).getImmuneTo().stream().map(Block::toString).collect(Collectors.toSet()),
                entityStates));
        arr.add(new StateTrait<>(
                "base_armor",
                "Base Armor",
                "The amount of armor points this entity has without wearing anything.",
                entity -> entity instanceof LivingEntity ? ((LivingEntity) entity).getArmorValue() : "N/A",
                entityStates));
        arr.add(new StateTrait<>(
                "base_attack_damage",
                "Base Attack Damage",
                "The base attack damage of this entity.",
                entity -> getAttributeValueIfPresent(entity, Attributes.ATTACK_DAMAGE),
                entityStates));
        arr.add(new StateTrait<>(
                "base_attack_knockback",
                "Base Attack Knockback",
                "The base attack knockback of this entity",
                entity -> getAttributeValueIfPresent(entity, Attributes.ATTACK_KNOCKBACK),
                entityStates));
        arr.add(new StateTrait<>(
                "bucketable",
                "Bucketable",
                "Determines whether buckets can be used on this entity.",
                entity -> entity instanceof Bucketable,
                entityStates));
        arr.add(new StateTrait<>(
                "can_be_ridden_in_water",
                "Can Be Ridden In Water",
                "Determines whether this entity will dismount its rider if it becomes completely submerged.",
                entity -> !entity.dismountsUnderwater(),
                entityStates));
        arr.add(new StateTrait<>(
                "can_be_saddled",
                "Can Be Saddled",
                "Determines whether this entity can equip saddles.",
                entity -> entity instanceof Saddleable && ((Saddleable) entity).isSaddleable(),
                entityStates));
        arr.add(new StateTrait<>(
                "can_breathe_in_water",
                "Can Breathe Underwater",
                "Determintes whether this entity can breathe underwater.",
                entity -> entity instanceof LivingEntity && ((LivingEntity) entity).canBreatheUnderwater(),
                entityStates));
        arr.add(new StateTrait<>(
                "can_open_doors",
                "Can Open Doors",
                "Whether this entity can open doors.",
                entity -> entity instanceof AbstractPiglin
                        || entity instanceof Villager
                        || entity instanceof Vindicator,
                entityStates));
        arr.add(new StateTrait<>(
                "can_ride_boat",
                "Can Ride Boat",
                "Whether this entity will ride boats when pushed by them.",
                entity -> entity.isPushable()
                        && entity.getBbWidth() < EntityType.BOAT.getWidth()
                        && entity instanceof LivingEntity
                        && !(entity instanceof WaterAnimal)
                        && !(entity instanceof Player),
                entityStates));
        arr.add(new StateTrait<>(
                "can_ride_minecart",
                "Can Ride Minecart",
                "Whether this entity will ride minecarts when pushed by them.",
                entity -> entity.isPushable()
                        && !(entity instanceof Player)
                        && !(entity instanceof IronGolem)
                        && !(entity instanceof AbstractMinecart),
                entityStates));
        arr.add(new StateTrait<>(
                "entity_group",
                "Mob Type",
                "Determines various mob behaviors, including but not limited to enchantment damage bonuses and most undead mob behaviors.</p><p>Unfortunately for drowned, mobs can only have one mob type.",
                entity -> {
                    if (entity instanceof LivingEntity) {
                        MobType mobType = ((LivingEntity) entity).getMobType();
                        if (mobType == MobType.UNDEFINED) return "DEFAULT";
                        else if (mobType == MobType.UNDEAD) return "UNDEAD";
                        else if (mobType == MobType.ARTHROPOD) return "ARTHROPOD";
                        else if (mobType == MobType.ILLAGER) return "ILLAGER";
                        else if (mobType == MobType.WATER) return "AQUATIC";
                    }
                    return "NONE";
                },
                entityStates));
        arr.add(new StateTrait<>(
                "eye_height",
                "Eye Height",
                "",
                entity -> entity.getEyeHeight(entity.getPose()),
                entityStates));
        arr.add(new StateTrait<>(
                "fire_immune",
                "Fire Immune",
                "Determines whether this entity is immune to fire damage. These entities will still ignite.",
                Entity::fireImmune,
                entityStates));
        arr.add(new StateTrait<>(
                "height_offset",
                "Height Offset",
                "The height offset this entity adds to itself when riding a vehicle.",
                entity -> {
                    if (entity instanceof Shulker) {
                        return "1.875 - vehicle's mounted height offset";
                    } else return entity.getMyRidingOffset();
                },
                entityStates));
        arr.add(new StateTrait<>(
                "hurt_by_water",
                "Hurt By Water",
                "Whether this is hurt by water, rain, and splash/lingering water bottles.",
                entity -> entity instanceof LivingEntity && ((LivingEntity) entity).isSensitiveToWater(),
                entityStates));
        arr.add(new StateTrait<>(
                "leadable",
                "Leadable",
                "Determines whether leads can be used on this entity.",
                entity -> entity instanceof Mob && ((Mob) entity).canBeLeashed(null),
                entityStates));
        arr.add(new StateTrait<>(
                "living",
                "Living",
                "Whether this is a LivingEntity. Includes armor stands.",
                Entity::showVehicleHealth,
                entityStates));
        arr.add(new StateTrait<>(
                "max_health",
                "Max Health",
                "The max health of this entity.",
                entity -> {
                    if (entity instanceof LivingEntity) {
                        if (entity instanceof AbstractChestedHorse || entity instanceof Horse) {
                            return "15.0 - 30.0";
                        } else if (entity instanceof Zombie) {
                            return "20.0 and 40.0 - 100.0 (Spawns with 20.0 health due to MC-219981)";
                        } else return ((LivingEntity) entity).getMaxHealth();
                    } else return "N/A";
                },
                entityStates));
        arr.add(new StateTrait<>(
                "mounted_height_offset",
                "Mounted Height Offset",
                "The height offset this entity adds to its passengers.",
                entity -> {
                    if (entity instanceof Camel) {
                        return "TODO";
                    } else if (entity instanceof Strider) {
                        return entity.getPassengersRidingOffset() + " ± 0.06 if its limbs are moving";
                    } else return entity.getPassengersRidingOffset();
                },
                entityStates));
        arr.add(new StateTrait<>(
                "savable",
                "Savable",
                "Whether this is saved to the world. Entities that are not savable are lost when unloaded.",
                entity -> entity.getType().canSerialize(),
                entityStates));
        arr.add(new StateTrait<>(
                "shearable",
                "Shearable",
                "Determines whether shears can be used on this entity.",
                entity -> entity instanceof Shearable && ((Shearable) entity).readyForShearing(),
                entityStates));
        arr.add(new StateTrait<>(
                "step_height",
                "Step Height",
                "Determines how high this entity can step up when walking into a block, boat, or shulker.",
                Entity::maxUpStep,
                entityStates));
        arr.add(new StateTrait<>(
                "summonable",
                "Summonable",
                "Determines whether this entity can be summoned with the /summon command.",
                entity -> entity.getType().canSummon(),
                entityStates));
        for (MobEffect effect : effects) {
            arr.add(new StateTrait<>(
                "immune_to_" + effects.getKey(effect).getPath(), // e.g. for Bad Luck, this is "immune_to_unluck"
                "Immune to " + effect.getDisplayName().getString(),
                "Whether this entity is immune to " + effect.getDisplayName().getString() + ".",
                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(effect, 600, 0, false, false), entity),
                entityStates));}
        /*arr.add(new StateTrait<>(
                "immune_to_fire",
                "Immune to Fire",
                "",
                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_FIRE),
                entityStates));
        arr.add(new StateTrait<>(
                "immune_to_projectiles",
                "Immune to Projectiles",
                "",
                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_PROJECTILE),
                entityStates));
        arr.add(new StateTrait<>(
                "immune_to_explosions",
                "Immune to Explosions",
                "",
                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_EXPLOSION),
                entityStates));
        arr.add(new StateTrait<>(
                "immune_to_falls",
                "Immune to Falls",
                "",
                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_FALL),
                entityStates));
        arr.add(new StateTrait<>(
                "immune_to_drowning",
                "Immune to Drowning",
                "",
                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_DROWNING),
                entityStates));
        arr.add(new StateTrait<>(
                "immune_to_freezing",
                "Immune to Freezing",
                "",
                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_FREEZING),
                entityStates));
        arr.add(new StateTrait<>(
                "immune_to_lightning",
                "Immune to Lightning",
                "",
                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_LIGHTNING),
                entityStates));*/
        arr.add(new StateTrait<>(
                "player_controllable",
                "Player Controllable",
                "TODO",
                entity -> {
                    try {
                        return entity instanceof LivingEntity && entity.getClass().getMethod("getRiddenInput", Player.class, Vec3.class).getDeclaringClass() != LivingEntity.class;
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    return "err";
                },
                entityStates));
        arr.add(new StateTrait<>(
            "raider",
            "Raider",
            "Determines if this entity can join raids.",
            entity -> entity instanceof Raider,
            entityStates));
        arr.add(new StateTrait<>(
            "category",
            "Mob Category",
            "Determines what mob cap this entity uses when spawning.",
            entity -> entity.getType().getCategory(),
            entityStates));
        arr.add(new StateTrait<>(
            "zombie",
            "Zombie",
            "Determines whether this mob will trample turtle eggs and spawn zombie reinforcements.</p><p>Reinforcements will be regular zombies, <i>not</i> the same type as the one who called them.",
            entity -> entity instanceof Zombie,
            entityStates));
    }
}
