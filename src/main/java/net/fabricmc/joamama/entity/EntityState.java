package net.fabricmc.joamama.entity;

import net.fabricmc.joamama.JoaMama;
import net.fabricmc.joamama.SimpleState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.*;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.*;
import net.minecraft.entity.vehicle.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.StatHandler;
import net.minecraft.state.property.Property;
import net.minecraft.world.World;

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

    public static void load (IntegratedServer server, ServerWorld world, MinecraftClient client, ClientWorld clientWorld, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook) {
        toEntity = new HashMap<>();
        for (EntityType<?> type : Registries.ENTITY_TYPE) {
            if (type == EntityType.PLAYER) {
                registerType(EntityType.PLAYER, state -> new ClientPlayerEntity(client, clientWorld, networkHandler, stats, recipeBook, false, false));
            } else {
                registerType(type, state -> type.create(world));
                Entity entity = type.create(world);
                if (entity instanceof PassiveEntity || entity instanceof PiglinEntity || entity instanceof ZoglinEntity || entity instanceof ZombieEntity) {
                    registerRule(type, EntityProperties::setIsBaby);
                }
                if (entity instanceof SlimeEntity) {
                    registerRule(type, EntityProperties::setSize);
                }
                if (entity instanceof AbstractHorseEntity || entity instanceof TameableEntity) {
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
