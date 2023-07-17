package net.fabricmc.joamama.entity;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.fabricmc.joamama.JoaMama;
import net.fabricmc.joamama.StateTrait;
import net.fabricmc.joamama.mixin.EntityTypeAccessor;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class EntityTraits {
    private static final MobType EntityGroup_NONE = new MobType();
    private static final SetMultimap<EntityType<?>, EntityState> entityStates;

    static {
        entityStates = MultimapBuilder.hashKeys().hashSetValues().build();
    }

    public static void load (Iterable<EntityType<?>> entityTypes) {
        entityTypes.forEach(type -> entityStates.putAll(type, EntityStateManager.getEntityStateManager(type).getStates()));
    }

    private static Object getAttributeValueIfPresent (Entity entity, Attribute attribute) {
        return entity instanceof LivingEntity && ((LivingEntity) entity).getAttributes().hasAttribute(attribute) ? ((LivingEntity) entity).getAttributeValue(attribute) : "N/A";
    }

    public static ArrayList<String> getTheWholeThing () {
        return new ArrayList<>(
                List.of(
//                        new StateTrait<>(
//                                "width",
//                                "Width",
//                                "",
//                                Entity::getBbWidth,
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "height",
//                                "Height",
//                                "",
//                                Entity::getBbHeight,
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "spawns_inside_hazards",
//                                "Spawns Inside Hazards",
//                                "Normally dangerous blocks that this entity can spawn inside.\nThis only includes wither roses, sweet berry bushes, and powder snow.\nSee Fire Immune for entities that can spawn inside of fire.",
//                                entity -> ((EntityTypeAccessor) entity.getType()).getImmuneTo().stream().map(Block::toString).collect(Collectors.toSet()),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "base_armor",
//                                "Base Armor",
//                                "The amount of armor points this has without wearing anything.",
//                                entity -> entity instanceof LivingEntity ? ((LivingEntity) entity).getArmorValue() : "N/A",
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "base_attack_damage",
//                                "Base Attack Damage",
//                                "",
//                                entity -> getAttributeValueIfPresent(entity, Attributes.ATTACK_DAMAGE),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "base_attack_knockback",
//                                "Base Attack Knockback",
//                                "",
//                                entity -> getAttributeValueIfPresent(entity, Attributes.ATTACK_KNOCKBACK),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "bucketable",
//                                "Bucketable",
//                                "",
//                                entity -> entity instanceof Bucketable,
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "can_be_ridden_in_water",
//                                "Can Be Ridden In Water",
//                                "",
//                                entity -> !entity.dismountsUnderwater(),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "can_be_saddled",
//                                "Can Be Saddled",
//                                "",
//                                entity -> entity instanceof Saddleable && ((Saddleable) entity).isSaddleable(),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "can_breathe_in_water",
//                                "Can Breathe In Water",
//                                "",
//                                entity -> entity instanceof LivingEntity && ((LivingEntity) entity).canBreatheUnderwater(),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "can_open_doors",
//                                "Can Open Doors",
//                                "",
//                                entity -> entity instanceof AbstractPiglin
//                                        || entity instanceof Villager
//                                        || entity instanceof Vindicator,
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "can_ride_boat",
//                                "Can Ride Boat",
//                                "",
//                                entity -> entity.isPushable()
//                                        && entity.getBbWidth() < EntityType.BOAT.getWidth()
//                                        && entity instanceof LivingEntity
//                                        && !(entity instanceof WaterAnimal)
//                                        && !(entity instanceof Player),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "can_ride_minecart",
//                                "Can Ride Minecart",
//                                "",
//                                entity -> entity.isPushable()
//                                        && !(entity instanceof Player)
//                                        && !(entity instanceof IronGolem)
//                                        && !(entity instanceof AbstractMinecart),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "entity_group",
//                                "Entity Group",
//                                "",
//                                entity -> entity instanceof LivingEntity ? ((LivingEntity) entity).getMobType() : EntityGroup_NONE,
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "eye_height",
//                                "Eye Height",
//                                "",
//                                entity -> entity.getEyeHeight(entity.getPose()),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "fire_immune",
//                                "Fire Immune",
//                                "",
//                                Entity::fireImmune,
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "height_offset",
//                                "Height Offset",
//                                "The height offset this entity adds to itself when riding a vehicle.",
//                                entity -> {
//                                    if (entity instanceof Shulker) {
//                                        return "1.875 - vehicle's mounted height offset";
//                                    } else return entity.getMyRidingOffset();
//                                },
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "hurt_by_water",
//                                "Hurt By Water",
//                                "Whether this is hurt by water, rain, and splash/lingering water bottles.",
//                                entity -> entity instanceof LivingEntity && ((LivingEntity) entity).isSensitiveToWater(),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "leadable",
//                                "Leadable",
//                                "",
//                                entity -> entity instanceof Mob && ((Mob) entity).canBeLeashed(null),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "living",
//                                "Living",
//                                "Whether this is a LivingEntity. Includes armor stands.",
//                                Entity::showVehicleHealth,
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "max_health",
//                                "Max Health",
//                                "",
//                                entity -> {
//                                    if (entity instanceof LivingEntity) {
//                                        if (entity instanceof AbstractChestedHorse || entity instanceof Horse) {
//                                            return "15.0 - 30.0";
//                                        } else if (entity instanceof Zombie) {
//                                            return "20.0 and 40.0 - 100.0 (Spawns with 20.0 health due to MC-219981)";
//                                        } else return ((LivingEntity) entity).getMaxHealth();
//                                    } else return "N/A";
//                                },
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "mounted_height_offset",
//                                "Mounted Height Offset",
//                                "The height offset this entity adds to its passengers.",
//                                entity -> {
//                                    if (entity instanceof Camel) {
//                                        return "TODO";
//                                    } else if (entity instanceof Strider) {
//                                        return entity.getPassengersRidingOffset() + " ± 0.06 if its limbs are moving";
//                                    } else return entity.getPassengersRidingOffset();
//                                },
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "savable",
//                                "Savable",
//                                "Whether this is saved to the world. Entities that are not savable are lost when unloaded.",
//                                entity -> entity.getType().canSerialize(),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "shearable",
//                                "Shearable",
//                                "",
//                                entity -> entity instanceof Shearable && ((Shearable) entity).readyForShearing(),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "step_height",
//                                "Step Height",
//                                "",
//                                Entity::maxUpStep,
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "summonable",
//                                "Summonable",
//                                "",
//                                entity -> entity.getType().canSummon(),
//                                entityStates
//                        ).toString(),
                        new StateTrait<>(
                                "immune_to_speed",
                                "Immune to Speed",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_slowness",
                                "Immune to Slowness",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_haste",
                                "Immune to Haste",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_mining_fatigue",
                                "Immune to Mining Fatigue",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_strength",
                                "Immune to Strength",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_instant_health",
                                "Immune to Instant Health",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.HEAL, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_instant_damage",
                                "Immune to Instant Damage",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.HARM, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_jump_boost",
                                "Immune to Jump Boost",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_nausea",
                                "Immune to Nausea",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_regeneration",
                                "Immune to Regeneration",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_resistance",
                                "Immune to Resistance",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_fire_resistance",
                                "Immune to Fire Resistance",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_water_breathing",
                                "Immune to Water Breathing",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_invisibility",
                                "Immune to Invisibility",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_blindness",
                                "Immune to Blindness",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_night_vision",
                                "Immune to Night Vision",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_hunger",
                                "Immune to Hunger",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_weakness",
                                "Immune to Weakness",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_poison",
                                "Immune to Poison",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.POISON, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_wither",
                                "Immune to Wither",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WITHER, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_health_boost",
                                "Immune to Health Boost",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_absorption",
                                "Immune to Absorption",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_saturation",
                                "Immune to Saturation",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.SATURATION, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_glowing",
                                "Immune to Glowing",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.GLOWING, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_levitation",
                                "Immune to Levitation",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_luck",
                                "Immune to Luck",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.LUCK, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_unluck",
                                "Immune to Unluck",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.UNLUCK, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_slow_falling",
                                "Immune to Slow Falling",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_conduit_power",
                                "Immune to Conduit Power",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_dolphins_grace",
                                "Immune to Dolphins Grace",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_bad_omen",
                                "Immune to Bad Omen",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_hero_of_the_village",
                                "Immune to Hero Of The Village",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "immune_to_darkness",
                                "Immune to Darkness",
                                "",
                                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.DARKNESS, 600, 0, false, false), entity),
                                entityStates
                        ).toString(),
//                        new StateTrait<>(
//                                "immune_to_fire",
//                                "Immune to Fire",
//                                "",
//                                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_FIRE),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "immune_to_projectiles",
//                                "Immune to Projectiles",
//                                "",
//                                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_PROJECTILE),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "immune_to_explosions",
//                                "Immune to Explosions",
//                                "",
//                                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_EXPLOSION),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "immune_to_falls",
//                                "Immune to Falls",
//                                "",
//                                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_FALL),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "immune_to_drowning",
//                                "Immune to Drowning",
//                                "",
//                                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_DROWNING),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "immune_to_freezing",
//                                "Immune to Freezing",
//                                "",
//                                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_FREEZING),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "immune_to_lightning",
//                                "Immune to Lightning",
//                                "",
//                                entity -> entity.isInvulnerableTo(DamageTypeTags.IS_LIGHTNING),
//                                entityStates
//                        ).toString(),
//                        new StateTrait<>(
//                                "immune_to_lightning",
//                                "Immune to Lightning",
//                                "",
//                                entity -> entity.isInvulnerableTo(FREEZE),
//                                entityStates
//                        ).toString()
                        new StateTrait<>(
                                "player_controllable",
                                "Player Controllable",
                                "",
                                entity -> {
                                    try {
                                        return entity instanceof LivingEntity && entity.getClass().getMethod("getRiddenInput", Player.class, Vec3.class).getDeclaringClass() != LivingEntity.class;
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    }
                                    return "err";
                                },
                                entityStates
                        ).toString()
                )
        );
    }
}
