package net.fabricmc.joamama.entity;

import net.fabricmc.joamama.SimpleStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class EntityStateManager {
    private final EntityType<?> type;
    private final Set<Property<?>> properties;
    private static Map<EntityType<?>, Set<Property<?>>> entityProperties;

    public EntityStateManager(EntityType<?> type, Collection<Property<?>> properties) {
        this.type = type;
        this.properties = new HashSet<>(properties);
    }

    public Set<EntityState> getStates () {
        return new SimpleStateManager(properties).getStates().stream().map(state -> new EntityState(type, state)).collect(Collectors.toSet());
    }

    public static EntityStateManager getEntityStateManager (EntityType<?> type) {
        return new EntityStateManager(type, entityProperties.get(type));
    }

    public static void load (World world) {
        entityProperties = new HashMap<>();
        for (EntityType<?> type : Registries.ENTITY_TYPE) {
            entityProperties.put(type, new HashSet<>());
            if (type == EntityType.PLAYER) continue;
            Entity entity = type.create(world);
            if (entity instanceof PassiveEntity || entity instanceof PiglinEntity || entity instanceof ZoglinEntity || entity instanceof ZombieEntity) {
                entityProperties.get(type).add(EntityProperties.IS_BABY);
            }
            if (entity instanceof SlimeEntity) {
                entityProperties.get(type).add(EntityProperties.SLIME_SIZE);
            }
            if (entity instanceof AbstractHorseEntity || entity instanceof TameableEntity) {
                entityProperties.get(type).add(EntityProperties.IS_TAMED);
            }
        }
        entityProperties.get(EntityType.ARMOR_STAND).addAll(Set.of(EntityProperties.MARKER, EntityProperties.SMALL));
        entityProperties.get(EntityType.BOAT).add(EntityProperties.BOAT_VARIANT);
        entityProperties.get(EntityType.CHEST_BOAT).add(EntityProperties.BOAT_VARIANT);
        entityProperties.get(EntityType.CAMEL).add(EntityProperties.CAMEL_POSE);
        entityProperties.get(EntityType.GOAT).add(EntityProperties.GOAT_POSE);
        entityProperties.get(EntityType.PANDA).add(EntityProperties.PANDA_VARIANT);
        entityProperties.get(EntityType.PUFFERFISH).add(EntityProperties.PUFF_STATE);
        entityProperties.get(EntityType.SHEEP).add(EntityProperties.SHEARED);
        entityProperties.get(EntityType.SNOW_GOLEM).add(EntityProperties.HAS_PUMPKIN);
        entityProperties.get(EntityType.VILLAGER).add(EntityProperties.VILLAGER_POSE);
        entityProperties.get(EntityType.WARDEN).add(EntityProperties.WARDEN_POSE);
        entityProperties.get(EntityType.PLAYER).add(EntityProperties.PLAYER_POSE);
    }
}
