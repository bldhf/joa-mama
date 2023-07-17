package net.fabricmc.joamama.entity;

import net.fabricmc.joamama.JoaMama;
import net.fabricmc.joamama.SimpleState;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.block.state.properties.Property;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EntityState {
    private final EntityType<?> type;
    private final Map<Property<?>, Comparable<?>> entries;
    private static Map<EntityType<?>, Function<EntityState, Entity>> toEntity;

    public EntityState (EntityType<?> type, SimpleState state) {
        this.type = type;
        this.entries = state.getEntries();
    }

    public EntityType<?> getType () {
        return this.type;
    }

    public Comparable<?> get (Property<?> property) {
        return this.entries.get(property);
    }

    public Map<Property<?>, Comparable<?>> getEntries () {
        return new HashMap<>(this.entries);
    }

    public Entity entity () {
        return toEntity.get(this.type).apply(this);
    }

    private static void registerType (EntityType<?> type, Function<EntityState, Entity> create) {
        toEntity.put(type, create);
    }

    private static void registerRule (EntityType<?> type, BiFunction<EntityState, Entity, Entity> rule) {
        Function<EntityState, Entity> create = toEntity.get(type);
        toEntity.put(type, state -> rule.apply(state, create.apply(state)));
    }

    public static void load (IntegratedServer server, ServerLevel world, Minecraft client, ClientLevel clientWorld, ClientPacketListener networkHandler, StatsCounter stats, ClientRecipeBook recipeBook) {
        toEntity = new HashMap<>();
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            if (type == EntityType.PLAYER) {
                registerType(EntityType.PLAYER, state -> new LocalPlayer(client, clientWorld, networkHandler, stats, recipeBook, false, false));
            } else {
                registerType(type, state -> type.create(world));
                Entity entity = type.create(world);
                if (entity instanceof AgeableMob || entity instanceof Piglin || entity instanceof Zoglin || entity instanceof Zombie) {
                    registerRule(type, EntityProperties::setIsBaby);
                }
                if (entity instanceof Slime) {
                    registerRule(type, EntityProperties::setSize);
                }
                if (entity instanceof AbstractHorse || entity instanceof TamableAnimal) {
                    registerRule(type, EntityProperties::setIsTamed);
                }
            }
        }
        registerRule(EntityType.ARMOR_STAND, EntityProperties::setArmorStandFlags);
        registerRule(EntityType.BOAT, EntityProperties::setVariant);
        registerRule(EntityType.CHEST_BOAT, EntityProperties::setVariant);
        registerRule(EntityType.CAMEL, EntityProperties::setPose);
        registerRule(EntityType.GOAT, EntityProperties::setPose);
        registerRule(EntityType.PANDA, EntityProperties::setVariant);
        registerRule(EntityType.PUFFERFISH, EntityProperties::setPuffState);
        registerRule(EntityType.SHEEP, EntityProperties::setSheared);
        registerRule(EntityType.SNOW_GOLEM, EntityProperties::setSheared);
        registerRule(EntityType.VILLAGER, EntityProperties::setPose);
        registerRule(EntityType.WARDEN, EntityProperties::setPose);
        registerRule(EntityType.PLAYER, EntityProperties::setPose);
    }
}
