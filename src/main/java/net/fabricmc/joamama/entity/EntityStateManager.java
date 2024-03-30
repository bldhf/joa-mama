package net.fabricmc.joamama.entity;

import net.fabricmc.joamama.SimpleStateManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
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

    public Set<EntityState> getStates() {
        return new SimpleStateManager(properties).getStates().stream().map(state -> new EntityState(type, state)).collect(Collectors.toSet());
    }

    public static EntityStateManager getEntityStateManager(EntityType<?> type) {
        return new EntityStateManager(type, entityProperties.get(type));
    }

    public static void load(Level level) {
        entityProperties = new HashMap<>();
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            entityProperties.put(type, new HashSet<>());
            if (type == EntityType.PLAYER) continue;
            Entity entity = type.create(level);
            if (entity instanceof AgeableMob || entity instanceof Piglin || entity instanceof Zoglin || entity instanceof Zombie) {
                entityProperties.get(type).add(EntityProperties.IS_BABY);
            }
            if (entity instanceof Slime) {
                entityProperties.get(type).add(EntityProperties.SLIME_SIZE);
            }
            if (entity instanceof AbstractHorse || entity instanceof TamableAnimal) {
                entityProperties.get(type).add(EntityProperties.IS_TAMED);
            }
        }
        entityProperties.get(EntityType.ARMOR_STAND).addAll(Set.of(EntityProperties.MARKER, EntityProperties.SMALL));
        entityProperties.get(EntityType.BOAT).add(EntityProperties.BOAT_VARIANT);
        entityProperties.get(EntityType.CHEST_BOAT).add(EntityProperties.BOAT_VARIANT);
        entityProperties.get(EntityType.CAMEL).add(EntityProperties.CAMEL_POSE);
        entityProperties.get(EntityType.ENDER_DRAGON).add(EntityProperties.DRAGON_PHASE);
        entityProperties.get(EntityType.GOAT).add(EntityProperties.GOAT_POSE);
        entityProperties.get(EntityType.PANDA).add(EntityProperties.PANDA_VARIANT);
        entityProperties.get(EntityType.PUFFERFISH).add(EntityProperties.PUFF_STATE);
        entityProperties.get(EntityType.SHEEP).add(EntityProperties.SHEARED);
        entityProperties.get(EntityType.SHULKER).add(EntityProperties.PEEK);
        entityProperties.get(EntityType.SNOW_GOLEM).add(EntityProperties.HAS_PUMPKIN);
        entityProperties.get(EntityType.VILLAGER).add(EntityProperties.VILLAGER_POSE);
        entityProperties.get(EntityType.WARDEN).add(EntityProperties.WARDEN_POSE);
        entityProperties.get(EntityType.WITHER).add(EntityProperties.WITHER_PHASE);
        entityProperties.get(EntityType.PLAYER).add(EntityProperties.PLAYER_POSE);
    }
}
