package net.fabricmc.joamama.entity;

import net.fabricmc.joamama.SimpleStateManager;
import net.minecraft.entity.EntityType;
import net.minecraft.state.property.Property;

import java.util.*;
import java.util.stream.Collectors;

public class EntityStateManager {
    private final EntityType<?> type;
    private final Set<Property<?>> properties;
    private static final Map<EntityType<?>, Set<Property<?>>> entityProperties;

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

    static {
        entityProperties = new HashMap<>();
        entityProperties.put(EntityType.ALLAY, new HashSet<>());
        entityProperties.put(EntityType.AREA_EFFECT_CLOUD, new HashSet<>());
        entityProperties.put(EntityType.ARMOR_STAND, new HashSet<>(Set.of(EntityProperties.MARKER, EntityProperties.SMALL)));
        entityProperties.put(EntityType.ARROW, new HashSet<>());
        entityProperties.put(EntityType.AXOLOTL, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.BAT, new HashSet<>());
        entityProperties.put(EntityType.BEE, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.BLAZE, new HashSet<>());
        entityProperties.put(EntityType.BOAT, new HashSet<>());
        entityProperties.put(EntityType.CHEST_BOAT, new HashSet<>());
        entityProperties.put(EntityType.CAT, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.CAMEL, new HashSet<>(Set.of(EntityProperties.IS_BABY, EntityProperties.CAMEL_POSE)));
        entityProperties.put(EntityType.CAVE_SPIDER, new HashSet<>());
        entityProperties.put(EntityType.CHICKEN, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.COD, new HashSet<>());
        entityProperties.put(EntityType.COW, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.CREEPER, new HashSet<>());
        entityProperties.put(EntityType.DOLPHIN, new HashSet<>());
        entityProperties.put(EntityType.DONKEY, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.DRAGON_FIREBALL, new HashSet<>());
        entityProperties.put(EntityType.DROWNED, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.ELDER_GUARDIAN, new HashSet<>());
        entityProperties.put(EntityType.END_CRYSTAL, new HashSet<>());
        entityProperties.put(EntityType.ENDER_DRAGON, new HashSet<>());
        entityProperties.put(EntityType.ENDERMAN, new HashSet<>());
        entityProperties.put(EntityType.ENDERMITE, new HashSet<>());
        entityProperties.put(EntityType.EVOKER, new HashSet<>());
        entityProperties.put(EntityType.EVOKER_FANGS, new HashSet<>());
        entityProperties.put(EntityType.EXPERIENCE_ORB, new HashSet<>());
        entityProperties.put(EntityType.EYE_OF_ENDER, new HashSet<>());
        entityProperties.put(EntityType.FALLING_BLOCK, new HashSet<>());
        entityProperties.put(EntityType.FIREWORK_ROCKET, new HashSet<>());
        entityProperties.put(EntityType.FOX, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.FROG, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.GHAST, new HashSet<>());
        entityProperties.put(EntityType.GIANT, new HashSet<>());
        entityProperties.put(EntityType.GLOW_ITEM_FRAME, new HashSet<>());
        entityProperties.put(EntityType.GLOW_SQUID, new HashSet<>());
        entityProperties.put(EntityType.GOAT, new HashSet<>(Set.of(EntityProperties.IS_BABY, EntityProperties.GOAT_POSE)));
        entityProperties.put(EntityType.GUARDIAN, new HashSet<>());
        entityProperties.put(EntityType.HOGLIN, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.HORSE, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.HUSK, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.ILLUSIONER, new HashSet<>());
        entityProperties.put(EntityType.IRON_GOLEM, new HashSet<>());
        entityProperties.put(EntityType.ITEM, new HashSet<>());
        entityProperties.put(EntityType.ITEM_FRAME, new HashSet<>());
        entityProperties.put(EntityType.FIREBALL, new HashSet<>());
        entityProperties.put(EntityType.LEASH_KNOT, new HashSet<>());
        entityProperties.put(EntityType.LIGHTNING_BOLT, new HashSet<>());
        entityProperties.put(EntityType.LLAMA, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.LLAMA_SPIT, new HashSet<>());
        entityProperties.put(EntityType.MAGMA_CUBE, new HashSet<>(Set.of(EntityProperties.SLIME_SIZE)));
        entityProperties.put(EntityType.MARKER, new HashSet<>());
        entityProperties.put(EntityType.MINECART, new HashSet<>());
        entityProperties.put(EntityType.CHEST_MINECART, new HashSet<>());
        entityProperties.put(EntityType.COMMAND_BLOCK_MINECART, new HashSet<>());
        entityProperties.put(EntityType.FURNACE_MINECART, new HashSet<>());
        entityProperties.put(EntityType.HOPPER_MINECART, new HashSet<>());
        entityProperties.put(EntityType.SPAWNER_MINECART, new HashSet<>());
        entityProperties.put(EntityType.TNT_MINECART, new HashSet<>());
        entityProperties.put(EntityType.MULE, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.MOOSHROOM, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.OCELOT, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.PAINTING, new HashSet<>());
        entityProperties.put(EntityType.PANDA, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.PARROT, new HashSet<>());
        entityProperties.put(EntityType.PHANTOM, new HashSet<>());
        entityProperties.put(EntityType.PIG, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.PIGLIN, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.PIGLIN_BRUTE, new HashSet<>());
        entityProperties.put(EntityType.PILLAGER, new HashSet<>());
        entityProperties.put(EntityType.POLAR_BEAR, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.TNT, new HashSet<>());
        entityProperties.put(EntityType.PUFFERFISH, new HashSet<>(Set.of(EntityProperties.PUFF_STATE)));
        entityProperties.put(EntityType.RABBIT, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.RAVAGER, new HashSet<>());
        entityProperties.put(EntityType.SALMON, new HashSet<>());
        entityProperties.put(EntityType.SHEEP, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.SHULKER, new HashSet<>());
        entityProperties.put(EntityType.SHULKER_BULLET, new HashSet<>());
        entityProperties.put(EntityType.SILVERFISH, new HashSet<>());
        entityProperties.put(EntityType.SKELETON, new HashSet<>());
        entityProperties.put(EntityType.SKELETON_HORSE, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.SLIME, new HashSet<>(Set.of(EntityProperties.SLIME_SIZE)));
        entityProperties.put(EntityType.SMALL_FIREBALL, new HashSet<>());
        entityProperties.put(EntityType.SNOW_GOLEM, new HashSet<>());
        entityProperties.put(EntityType.SNOWBALL, new HashSet<>());
        entityProperties.put(EntityType.SPECTRAL_ARROW, new HashSet<>());
        entityProperties.put(EntityType.SPIDER, new HashSet<>());
        entityProperties.put(EntityType.SQUID, new HashSet<>());
        entityProperties.put(EntityType.STRAY, new HashSet<>());
        entityProperties.put(EntityType.STRIDER, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.TADPOLE, new HashSet<>());
        entityProperties.put(EntityType.EGG, new HashSet<>());
        entityProperties.put(EntityType.ENDER_PEARL, new HashSet<>());
        entityProperties.put(EntityType.EXPERIENCE_BOTTLE, new HashSet<>());
        entityProperties.put(EntityType.POTION, new HashSet<>());
        entityProperties.put(EntityType.TRIDENT, new HashSet<>());
        entityProperties.put(EntityType.TRADER_LLAMA, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.TROPICAL_FISH, new HashSet<>());
        entityProperties.put(EntityType.TURTLE, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.VEX, new HashSet<>());
        entityProperties.put(EntityType.VILLAGER, new HashSet<>(Set.of(EntityProperties.IS_BABY, EntityProperties.VILLAGER_POSE)));
        entityProperties.put(EntityType.VINDICATOR, new HashSet<>());
        entityProperties.put(EntityType.WANDERING_TRADER, new HashSet<>());
        entityProperties.put(EntityType.WARDEN, new HashSet<>(Set.of(EntityProperties.WARDEN_POSE)));
        entityProperties.put(EntityType.WITCH, new HashSet<>());
        entityProperties.put(EntityType.WITHER, new HashSet<>());
        entityProperties.put(EntityType.WITHER_SKELETON, new HashSet<>());
        entityProperties.put(EntityType.WITHER_SKULL, new HashSet<>());
        entityProperties.put(EntityType.WOLF, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.ZOGLIN, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.ZOMBIE, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.ZOMBIE_HORSE, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.ZOMBIE_VILLAGER, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.ZOMBIFIED_PIGLIN, new HashSet<>(Set.of(EntityProperties.IS_BABY)));
        entityProperties.put(EntityType.PLAYER, new HashSet<>(Set.of(EntityProperties.PLAYER_POSE)));
        entityProperties.put(EntityType.FISHING_BOBBER, new HashSet<>());
    }
}
