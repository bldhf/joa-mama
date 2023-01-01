package net.fabricmc.joamama.entity;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.fabricmc.joamama.JoaMama;
import net.fabricmc.joamama.StateTrait;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityTraits {
    private static final EntityGroup EntityGroup_NONE = new EntityGroup();
    private static final SetMultimap<EntityType<?>, EntityState> entityStates;

    static {
        entityStates = MultimapBuilder.hashKeys().hashSetValues().build();
    }

    public static void load (Iterable<EntityType<?>> entityTypes) {
        entityTypes.forEach(type -> entityStates.putAll(type, EntityStateManager.getEntityStateManager(type).getStates()));
    }

    private static Object getAttributeValueIfPresent (Entity entity, EntityAttribute attribute) {
        return entity instanceof LivingEntity && ((LivingEntity) entity).getAttributes().hasAttribute(attribute) ? ((LivingEntity) entity).getAttributeValue(attribute) : "N/A";
    }

    public static ArrayList<String> getTheWholeThing () {
        return new ArrayList<>(
                List.of(
                        new StateTrait<>(
                                "width",
                                "Width",
                                "",
                                Entity::getWidth,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "height",
                                "Height",
                                "",
                                Entity::getHeight,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "base_armor",
                                "Base Armor",
                                "The amount of armor points this has without wearing anything.",
                                entity -> entity instanceof LivingEntity ? ((LivingEntity) entity).getArmor() : "N/A",
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "base_attack_damage",
                                "Base Attack Damage",
                                "",
                                entity -> getAttributeValueIfPresent(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "base_attack_knockback",
                                "Base Attack Knockback",
                                "",
                                entity -> getAttributeValueIfPresent(entity, EntityAttributes.GENERIC_ATTACK_KNOCKBACK),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "bucketable",
                                "Bucketable",
                                "",
                                entity -> entity instanceof Bucketable,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "can_be_saddled",
                                "Can Be Saddled",
                                "",
                                entity -> entity instanceof Saddleable && ((Saddleable) entity).canBeSaddled(),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "can_breathe_in_water",
                                "Can Breathe In Water",
                                "",
                                entity -> entity instanceof LivingEntity && ((LivingEntity) entity).canBreatheInWater(),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "can_open_doors",
                                "Can Open Doors",
                                "",
                                entity -> entity instanceof AbstractPiglinEntity
                                        || entity instanceof VillagerEntity
                                        || entity instanceof VindicatorEntity,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "can_ride_boat",
                                "Can Ride Boat",
                                "",
                                entity -> entity.isPushable()
                                        && entity.getWidth() < 1.375
                                        && entity instanceof LivingEntity
                                        && !(entity instanceof WaterCreatureEntity)
                                        && !(entity instanceof PlayerEntity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "can_ride_minecart",
                                "Can Ride Minecart",
                                "",
                                entity -> entity.isPushable()
                                        && !(entity instanceof PlayerEntity)
                                        && !(entity instanceof IronGolemEntity)
                                        && !(entity instanceof AbstractMinecartEntity),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "entity_group",
                                "Entity Group",
                                "",
                                entity -> entity instanceof LivingEntity ? ((LivingEntity) entity).getGroup() : EntityGroup_NONE,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "eye_height",
                                "Eye Height",
                                "",
                                entity -> entity.getEyeHeight(entity.getPose()),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "fire_immune",
                                "Fire Immune",
                                "",
                                Entity::isFireImmune,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "hurt_by_water",
                                "Hurt By Water",
                                "Whether this is hurt by water, rain, and splash/lingering water bottles.",
                                entity -> entity instanceof LivingEntity && ((LivingEntity) entity).hurtByWater(),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "leadable",
                                "Leadable",
                                "",
                                entity -> entity instanceof MobEntity && ((MobEntity) entity).canBeLeashedBy(null),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "living",
                                "Living",
                                "Whether this is a LivingEntity. Includes armor stands.",
                                Entity::isLiving,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "max_health",
                                "Max Health",
                                "",
                                entity -> {
                                    if (entity instanceof LivingEntity) {
                                        if (entity instanceof AbstractDonkeyEntity || entity instanceof HorseEntity) {
                                            return "15.0 - 30.0";
                                        } else if (entity instanceof ZombieEntity) {
                                            return "20.0 and 40.0 - 100.0 (Spawns with 20.0 health due to MC-219981)";
                                        } else return ((LivingEntity) entity).getMaxHealth();
                                    } else return "N/A";
                                },
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "savable",
                                "Savable",
                                "Whether this is saved to the world. Entities that are not savable are lost when unloaded.",
                                entity -> entity.getType().isSaveable(),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "step_height",
                                "Step Height",
                                "",
                                entity -> entity.stepHeight,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "summonable",
                                "Summonable",
                                "",
                                entity -> entity.getType().isSummonable(),
                                entityStates
                        ).toString()
                )
        );
    }
}
