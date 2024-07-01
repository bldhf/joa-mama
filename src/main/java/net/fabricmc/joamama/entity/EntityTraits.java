package net.fabricmc.joamama.entity;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.fabricmc.joamama.EntityStateTrait;
import net.fabricmc.joamama.JoaMama;
import net.fabricmc.joamama.TraitCollection;
import net.fabricmc.joamama.mixin.EntityTypeAccessor;
import net.fabricmc.joamama.mock.MockLevelReader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class EntityTraits {
    private static final SetMultimap<EntityType<?>, EntityState> entityStates;
    private static Registry<MobEffect> effects;
    private static ServerLevel level;

    static {
        entityStates = MultimapBuilder.hashKeys().hashSetValues().build();
    }

    public static void load(TraitCollection<EntityStateTrait<?>, SetMultimap<EntityType<?>, EntityState>> traits, Iterable<EntityType<?>> entityTypes, Registry<MobEffect> effects, ServerLevel level) {
        entityTypes.forEach(type -> EntityTraits.entityStates.putAll(type, EntityStateManager.getEntityStateManager(type).getStates()));
        traits.load(EntityTraits.entityStates);
        EntityTraits.effects = effects;
        EntityTraits.level = level;
    }

    private static Object getAttributeValueIfPresent(Entity entity, Holder<Attribute> holder) {
        return entity instanceof LivingEntity && ((LivingEntity) entity).getAttributes().hasAttribute(holder) ? ((LivingEntity) entity).getAttributeValue(holder) : "N/A";
    }

    public static void getTheWholeThing(TraitCollection<EntityStateTrait<?>, SetMultimap<EntityType<?>, EntityState>> traits) {
        traits.add(new EntityStateTrait<>(
            "width",
            "Width",
            "The width of this entity. All entity hitboxes except for item frames, paintings, and shulkers have square horizontal cross-sections, so there is no distinction between length and width.",
            "",
            Entity::getBbWidth
        ));
        traits.add(new EntityStateTrait<>(
            "height",
            "Height",
            "The height of this entity.",
            "",
            Entity::getBbHeight
        ));
        traits.add(new EntityStateTrait<>(
            "spawns_inside_hazards",
            "Spawns Inside Hazards",
            "Normally dangerous blocks that this entity can spawn inside.\nThis only includes wither roses, sweet berry bushes, and powder snow.\nSee Fire Immune for entities that can spawn inside of fire.",
            "",
            entity -> ((EntityTypeAccessor) entity.getType()).getImmuneTo().stream().map(Block::toString).collect(Collectors.toSet())
        ));
        traits.add(new EntityStateTrait<>(
            "base_armor",
            "Base Armor",
            "The amount of armor points this entity has without wearing anything.",
            "",
            entity -> entity instanceof LivingEntity ? ((LivingEntity) entity).getArmorValue() : "N/A"
        ));
        traits.add(new EntityStateTrait<>(
            "base_attack_damage",
            "Base Attack Damage",
            "The base attack damage of this entity.",
            "",
            entity -> getAttributeValueIfPresent(entity, Attributes.ATTACK_DAMAGE)
        ));
        traits.add(new EntityStateTrait<>(
            "base_attack_knockback",
            "Base Attack Knockback",
            "The base attack knockback of this entity",
            "",
            entity -> getAttributeValueIfPresent(entity, Attributes.ATTACK_KNOCKBACK)
        ));
        traits.add(new EntityStateTrait<>(
            "bucketable",
            "Bucketable",
            "Determines whether buckets can be used on this entity.",
            "",
            entity -> entity instanceof Bucketable
        ));
        traits.add(new EntityStateTrait<>(
            "can_be_ridden_in_water",
            "Can Be Ridden In Water",
            "Determines whether this entity will dismount its rider if it becomes completely submerged.",
            "",
            entity -> !entity.dismountsUnderwater()
        ));
        traits.add(new EntityStateTrait<>(
            "can_be_saddled",
            "Can Be Saddled",
            "Determines whether this entity can equip saddles.",
            "",
            entity -> entity instanceof Saddleable && ((Saddleable) entity).isSaddleable()
        ));
        traits.add(new EntityStateTrait<>(
            "can_breathe_in_water",
            "Can Breathe Underwater",
            "Determines whether this entity can breathe underwater.",
            "",
            entity -> entity instanceof LivingEntity && ((LivingEntity) entity).canBreatheUnderwater()
        ));
        traits.add(new EntityStateTrait<>(
            "can_open_doors",
            "Can Open Doors",
            "Whether this entity can open doors.",
            "",
            entity -> entity instanceof AbstractPiglin
                    || entity instanceof Villager
                    || entity instanceof Vindicator
        ));
        traits.add(new EntityStateTrait<>(
            "can_ride_boat",
            "Can Ride Boats",
            "Whether this entity will ride boats when pushed by them.",
            "",
            entity -> entity.isPushable()
                    && entity.getBbWidth() < EntityType.BOAT.getWidth()
                    && entity instanceof LivingEntity
                    && !(entity instanceof WaterAnimal)
                    && !(entity instanceof Player)
        ));
        traits.add(new EntityStateTrait<>(
            "can_ride_minecart",
            "Can Ride Minecarts",
            "Whether this entity will ride minecarts when pushed by them.",
            "",
            entity -> entity.isPushable()
                    && !(entity instanceof Player)
                    && !(entity instanceof IronGolem)
                    && !(entity instanceof AbstractMinecart)
        ));
        traits.add(new EntityStateTrait<>(
            "entity_group",
            "Entity Group",
            "Determines various mob behaviors, including but not limited to enchantment damage bonuses and most undead mob behaviors.\nUnfortunately for drowned, mobs can only have one mob type.",
            "",
            entity -> {
                if (entity instanceof LivingEntity) {
                    EntityType<?> mobType = entity.getType();
                    if (mobType.is(EntityTypeTags.UNDEAD)) return "UNDEAD";
                    else if (mobType.is(EntityTypeTags.ARTHROPOD)) return "ARTHROPOD";
                    else if (mobType.is(EntityTypeTags.ILLAGER)) return "ILLAGER";
                    else if (mobType.is(EntityTypeTags.AQUATIC)) return "AQUATIC";
                }
                return "NONE";
            }
        ));
        traits.add(new EntityStateTrait<>(
            "eye_height",
            "Eye Height",
            "", // TODO | 4/1/2024 | Missing description
            "",
            entity -> entity.getEyeHeight(entity.getPose())
        ));
        traits.add(new EntityStateTrait<>(
            "fire_immune",
            "Fire Immune",
            "Determines whether this entity is immune to fire damage. These entities will still ignite.",
            "",
            Entity::fireImmune
        ));
        traits.add(new EntityStateTrait<>(
            "height_offset",
            "Riding Offset (Self)",
            "The height offset this entity adds to itself when riding a vehicle.",
            "",
            entity -> {
                if (entity instanceof Shulker) {
                    return "1.875 - (vehicle's mounted height offset)"; // TODO | 4/1/2024 | Needs extra code digging
                } else return entity.getMyRidingOffset(); // TODO | 7/1/2024 | 1.21 port: couldn't figure this one out
            }
        ));
        traits.add(new EntityStateTrait<>(
            "hurt_by_water",
            "Hurt By Water",
            "Whether this is hurt by water, rain, and splash/lingering water bottles.",
            "",
            entity -> entity instanceof LivingEntity && ((LivingEntity) entity).isSensitiveToWater()
        ));
        traits.add(new EntityStateTrait<>(
            "leadable",
            "Leadable",
            "Determines whether leads can be used on this entity.",
            "",
            entity -> entity instanceof Mob && ((Mob) entity).canBeLeashed()
        ));
        traits.add(new EntityStateTrait<>(
            "living",
            "Living",
            "Whether this is a LivingEntity. Includes armor stands.",
            "",
            entity -> entity instanceof LivingEntity
        ));
        traits.add(new EntityStateTrait<>(
            "max_health",
            "Max Health",
            "The max health of this entity.",
            "",
            entity -> {
                if (entity instanceof LivingEntity) {
                    if (entity instanceof AbstractChestedHorse || entity instanceof Horse) {
                        return "15.0 - 30.0";
                    } else if (entity instanceof Zombie) {
                        return "20.0 and 40.0 - 100.0 (Spawns with 20.0 health due to MC-219981)";
                    } else return ((LivingEntity) entity).getMaxHealth();
                } else return "N/A";
            }
        ));
        traits.add(new EntityStateTrait<>(
            "mounted_height_offset",
            "Riding Offset (Passenger)",
            "The height offset this entity adds to its passenger(s).",
            "",
            entity -> {
                if (entity instanceof Camel) {
                    return "TODO"; // TODO | 4/1/2024 | Needs extra code digging
                } else if (entity instanceof Strider) {
                    return entity.getPassengersRidingOffset() + " ± 0.06 if its limbs are moving"; // TODO | 7/1/2024 | 1.21 port: couldn't figure this one out
                } else return entity.getPassengersRidingOffset();
            }
        ));
        traits.add(new EntityStateTrait<>(
            "savable",
            "Savable",
            "Whether this is saved to the world. Entities that are not savable are lost when unloaded.",
            "",
            entity -> entity.getType().canSerialize()
        ));
        traits.add(new EntityStateTrait<>(
            "shearable",
            "Shearable",
            "Determines whether shears can be used on this entity.",
            "",
            entity -> entity instanceof Shearable && ((Shearable) entity).readyForShearing()
        ));
        traits.add(new EntityStateTrait<>(
            "step_height",
            "Step Height",
            "Determines how high this entity can step up when walking into a block, boat, or shulker.",
            "",
            Entity::maxUpStep
        ));
        traits.add(new EntityStateTrait<>(
            "summonable",
            "Summonable",
            "Determines whether this entity can be summoned with the /summon command.",
            "",
            entity -> entity.getType().canSummon()
        ));
        for (MobEffect effect : effects) {
            traits.add(new EntityStateTrait<>(
                "immune_to_" + effects.getKey(effect).getPath(), // e.g. for Bad Luck, this is "immune_to_unluck"
                "Immune to " + effect.getDisplayName().getString(),
                "Whether this entity is immune to " + effect.getDisplayName().getString() + ".",
                "",
                entity -> entity instanceof LivingEntity && !((LivingEntity)entity).addEffect(new MobEffectInstance(Holder.direct(effect), 600, 0, false, false), entity)
        ));}
        traits.add(new EntityStateTrait<>(
            "player_controllable",
            "Player Controllable",
            "", // TODO | 4/1/2024 | Missing description
            "",
            entity -> {
                try {
                    return entity instanceof LivingEntity && entity.getClass().getMethod("getRiddenInput", Player.class, Vec3.class).getDeclaringClass() != LivingEntity.class;
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return "err";
            }
        ));
        traits.add(new EntityStateTrait<>(
            "raider",
            "Raider",
            "Determines if this entity can join raids.",
            "",
            entity -> entity instanceof Raider
        ));
        traits.add(new EntityStateTrait<>(
            "category",
            "Mob Category",
            "Determines what mob cap this entity uses when spawning.",
            "",
            entity -> entity.getType().getCategory()
        ));
        traits.add(new EntityStateTrait<>(
            "zombie",
            "Zombie",
            "Determines whether this mob will trample turtle eggs and spawn zombie reinforcements.\nReinforcements will be regular zombies, <i>not</i> the same type as the one who called them.",
            "",
            entity -> entity instanceof Zombie
        ));
        traits.add(new EntityStateTrait<>(
            "xp",
            "Experience Dropped",
            "How much experience this entity drops when killed.",
            "",
            entity -> {
                if (entity instanceof LivingEntity) {
                    Set<Integer> range = JoaMama.testRandom(() -> ((LivingEntity) entity).getExperienceReward(), 1984).keySet(); // TODO | 7/1/2024 | 1.21 port: couldn't figure this one out
                    if (entity instanceof Zombie) {
                        JoaMama.LOGGER.error(entity.getType().toShortString());
                        for (int val : range.stream().sorted().toList()) JoaMama.LOGGER.info(String.valueOf(val));
                    }
                    int min = Collections.min(range);
                    int max = Collections.max(range);
                    return (min == max ? min : "(" + min + " - " + max + ")") + (entity instanceof Mob ? " + (1 - 3) per equipment" : "");
                } else return 0;
            }
        ));
    }

    public static void getDamageImmunities(TraitCollection<EntityStateTrait<?>, SetMultimap<EntityType<?>, EntityState>> traits) {
        // FIXME | 3/30/2024 | Damage immunity properties are broken for players because they require a ServerPlayer rather than a LocalPlayer
        Arrow arrow = EntityType.ARROW.create(level);

        traits.add(new EntityStateTrait<>(
            "immune_to_arrows",
            "Immune to Arrows and Tridents",
            "Whether this entity is immune to arrows and tridents. These entities will deflect arrows and tridents that hit them. Endermen will only do this if they fail to teleport when hit.",
            "",
            entity -> entity instanceof EnderMan || !entity.hurt(arrow.damageSources().arrow(arrow, arrow), 1984)
        ));
    }
}
