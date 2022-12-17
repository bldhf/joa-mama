package net.fabricmc.joamama.entity;

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
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.StatHandler;
import net.minecraft.state.property.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EntityState {
    private static IntegratedServer server;
    private static ServerWorld world;
    private static MinecraftClient client;
    private static ClientWorld clientWorld;
    private static ClientPlayNetworkHandler networkHandler;
    private static StatHandler stats;
    private static ClientRecipeBook recipeBook;
    private final EntityType<?> type;
    private final Map<Property<?>, Comparable<?>> entries;
    private static final Map<EntityType<?>, Function<EntityState, Entity>> toEntity;

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

    private static void register (EntityType<?> type, Function<EntityState, Entity> func) {
        toEntity.putIfAbsent(type, func);
    }

    public static void load (IntegratedServer server, ServerWorld world, MinecraftClient client, ClientWorld clientWorld, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook) {
        EntityState.server = server;
        EntityState.world = world;
        EntityState.client = client;
        EntityState.clientWorld = clientWorld;
        EntityState.networkHandler = networkHandler;
        EntityState.stats = stats;
        EntityState.recipeBook = recipeBook;
    }

    static {
        toEntity = new HashMap<>();
        register(EntityType.ALLAY, state -> new AllayEntity(EntityType.ALLAY, world));
        register(EntityType.AREA_EFFECT_CLOUD, state -> new AreaEffectCloudEntity(EntityType.AREA_EFFECT_CLOUD, world));
        register(EntityType.ARMOR_STAND, state -> EntityProperties.setArmorStandFlags(state, new ArmorStandEntity(EntityType.ARMOR_STAND, world)));
        register(EntityType.ARROW, state -> new ArrowEntity(EntityType.ARROW, world));
        register(EntityType.AXOLOTL, state -> EntityProperties.setIsBaby(state, new AxolotlEntity(EntityType.AXOLOTL, world)));
        register(EntityType.BAT, state -> new BatEntity(EntityType.BAT, world));
        register(EntityType.BEE, state -> EntityProperties.setIsBaby(state, new BeeEntity(EntityType.BEE, world)));
        register(EntityType.BLAZE, state -> new BlazeEntity(EntityType.BLAZE, world));
        register(EntityType.BOAT, state -> new BoatEntity(EntityType.BOAT, world));
        register(EntityType.CHEST_BOAT, state -> new ChestBoatEntity(EntityType.CHEST_BOAT, world));
        register(EntityType.CAT, state -> EntityProperties.setIsBaby(state, new CatEntity(EntityType.CAT, world)));
        register(EntityType.CAMEL, state -> EntityProperties.setPose(state, EntityProperties.setIsBaby(state, new CamelEntity(EntityType.CAMEL, world))));
        register(EntityType.CAVE_SPIDER, state -> new CaveSpiderEntity(EntityType.CAVE_SPIDER, world));
        register(EntityType.CHICKEN, state -> EntityProperties.setIsBaby(state, new ChickenEntity(EntityType.CHICKEN, world)));
        register(EntityType.COD, state -> new CodEntity(EntityType.COD, world));
        register(EntityType.COW, state -> EntityProperties.setIsBaby(state, new CowEntity(EntityType.COW, world)));
        register(EntityType.CREEPER, state -> new CreeperEntity(EntityType.CREEPER, world));
        register(EntityType.DOLPHIN, state -> new DolphinEntity(EntityType.DOLPHIN, world));
        register(EntityType.DONKEY, state -> EntityProperties.setIsBaby(state, new DonkeyEntity(EntityType.DONKEY, world)));
        register(EntityType.DRAGON_FIREBALL, state -> new DragonFireballEntity(EntityType.DRAGON_FIREBALL, world));
        register(EntityType.DROWNED, state -> EntityProperties.setIsBaby(state, new DrownedEntity(EntityType.DROWNED, world)));
        register(EntityType.ELDER_GUARDIAN, state -> new ElderGuardianEntity(EntityType.ELDER_GUARDIAN, world));
        register(EntityType.END_CRYSTAL, state -> new EndCrystalEntity(EntityType.END_CRYSTAL, world));
        register(EntityType.ENDER_DRAGON, state -> new EnderDragonEntity(EntityType.ENDER_DRAGON, world));
        register(EntityType.ENDERMAN, state -> new EndermanEntity(EntityType.ENDERMAN, world));
        register(EntityType.ENDERMITE, state -> new EndermiteEntity(EntityType.ENDERMITE, world));
        register(EntityType.EVOKER, state -> new EvokerEntity(EntityType.EVOKER, world));
        register(EntityType.EVOKER_FANGS, state -> new EvokerFangsEntity(EntityType.EVOKER_FANGS, world));
        register(EntityType.EXPERIENCE_ORB, state -> new ExperienceOrbEntity(EntityType.EXPERIENCE_ORB, world));
        register(EntityType.EYE_OF_ENDER, state -> new EyeOfEnderEntity(EntityType.EYE_OF_ENDER, world));
        register(EntityType.FALLING_BLOCK, state -> new FallingBlockEntity(EntityType.FALLING_BLOCK, world));
        register(EntityType.FIREWORK_ROCKET, state -> new FireworkRocketEntity(EntityType.FIREWORK_ROCKET, world));
        register(EntityType.FOX, state -> EntityProperties.setIsBaby(state, new FoxEntity(EntityType.FOX, world)));
        register(EntityType.FROG, state -> EntityProperties.setIsBaby(state, new FrogEntity(EntityType.FROG, world)));
        register(EntityType.GHAST, state -> new GhastEntity(EntityType.GHAST, world));
        register(EntityType.GIANT, state -> new GiantEntity(EntityType.GIANT, world));
        register(EntityType.GLOW_ITEM_FRAME, state -> new GlowItemFrameEntity(EntityType.GLOW_ITEM_FRAME, world));
        register(EntityType.GLOW_SQUID, state -> new GlowSquidEntity(EntityType.GLOW_SQUID, world));
        register(EntityType.GOAT, state -> EntityProperties.setPose(state, EntityProperties.setIsBaby(state, new GoatEntity(EntityType.GOAT, world))));
        register(EntityType.GUARDIAN, state -> new GuardianEntity(EntityType.GUARDIAN, world));
        register(EntityType.HOGLIN, state -> EntityProperties.setIsBaby(state, new HoglinEntity(EntityType.HOGLIN, world)));
        register(EntityType.HORSE, state -> EntityProperties.setIsBaby(state, new HorseEntity(EntityType.HORSE, world)));
        register(EntityType.HUSK, state -> EntityProperties.setIsBaby(state, new HuskEntity(EntityType.HUSK, world)));
        register(EntityType.ILLUSIONER, state -> new IllusionerEntity(EntityType.ILLUSIONER, world));
        register(EntityType.IRON_GOLEM, state -> new IronGolemEntity(EntityType.IRON_GOLEM, world));
        register(EntityType.ITEM, state -> new ItemEntity(EntityType.ITEM, world));
        register(EntityType.ITEM_FRAME, state -> new ItemFrameEntity(EntityType.ITEM_FRAME, world));
        register(EntityType.FIREBALL, state -> new FireballEntity(EntityType.FIREBALL, world));
        register(EntityType.LEASH_KNOT, state -> new LeashKnotEntity(EntityType.LEASH_KNOT, world));
        register(EntityType.LIGHTNING_BOLT, state -> new LightningEntity(EntityType.LIGHTNING_BOLT, world));
        register(EntityType.LLAMA, state -> EntityProperties.setIsBaby(state, new LlamaEntity(EntityType.LLAMA, world)));
        register(EntityType.LLAMA_SPIT, state -> new LlamaSpitEntity(EntityType.LLAMA_SPIT, world));
        register(EntityType.MAGMA_CUBE, state -> EntityProperties.setSize(state, new MagmaCubeEntity(EntityType.MAGMA_CUBE, world)));
        register(EntityType.MARKER, state -> new MarkerEntity(EntityType.MARKER, world));
        register(EntityType.MINECART, state -> new MinecartEntity(EntityType.MINECART, world));
        register(EntityType.CHEST_MINECART, state -> new ChestMinecartEntity(EntityType.CHEST_MINECART, world));
        register(EntityType.COMMAND_BLOCK_MINECART, state -> new CommandBlockMinecartEntity(EntityType.COMMAND_BLOCK_MINECART, world));
        register(EntityType.FURNACE_MINECART, state -> new FurnaceMinecartEntity(EntityType.FURNACE_MINECART, world));
        register(EntityType.HOPPER_MINECART, state -> new HopperMinecartEntity(EntityType.HOPPER_MINECART, world));
        register(EntityType.SPAWNER_MINECART, state -> new SpawnerMinecartEntity(EntityType.SPAWNER_MINECART, world));
        register(EntityType.TNT_MINECART, state -> new TntMinecartEntity(EntityType.TNT_MINECART, world));
        register(EntityType.MULE, state -> EntityProperties.setIsBaby(state, new MuleEntity(EntityType.MULE, world)));
        register(EntityType.MOOSHROOM, state -> EntityProperties.setIsBaby(state, new MooshroomEntity(EntityType.MOOSHROOM, world)));
        register(EntityType.OCELOT, state -> EntityProperties.setIsBaby(state, new OcelotEntity(EntityType.OCELOT, world)));
        register(EntityType.PAINTING, state -> new PaintingEntity(EntityType.PAINTING, world));
        register(EntityType.PANDA, state -> EntityProperties.setIsBaby(state, new PandaEntity(EntityType.PANDA, world)));
        register(EntityType.PARROT, state -> new ParrotEntity(EntityType.PARROT, world));
        register(EntityType.PHANTOM, state -> new PhantomEntity(EntityType.PHANTOM, world));
        register(EntityType.PIG, state -> EntityProperties.setIsBaby(state, new PigEntity(EntityType.PIG, world)));
        register(EntityType.PIGLIN, state -> EntityProperties.setIsBaby(state, new PiglinEntity(EntityType.PIGLIN, world)));
        register(EntityType.PIGLIN_BRUTE, state -> new PiglinBruteEntity(EntityType.PIGLIN_BRUTE, world));
        register(EntityType.PILLAGER, state -> new PillagerEntity(EntityType.PILLAGER, world));
        register(EntityType.POLAR_BEAR, state -> EntityProperties.setIsBaby(state, new PolarBearEntity(EntityType.POLAR_BEAR, world)));
        register(EntityType.TNT, state -> new TntEntity(EntityType.TNT, world));
        register(EntityType.PUFFERFISH, state -> EntityProperties.setPuffState(state, new PufferfishEntity(EntityType.PUFFERFISH, world)));
        register(EntityType.RABBIT, state -> EntityProperties.setIsBaby(state, new RabbitEntity(EntityType.RABBIT, world)));
        register(EntityType.RAVAGER, state -> new RavagerEntity(EntityType.RAVAGER, world));
        register(EntityType.SALMON, state -> new SalmonEntity(EntityType.SALMON, world));
        register(EntityType.SHEEP, state -> EntityProperties.setIsBaby(state, new SheepEntity(EntityType.SHEEP, world)));
        register(EntityType.SHULKER, state -> new ShulkerEntity(EntityType.SHULKER, world));
        register(EntityType.SHULKER_BULLET, state -> new ShulkerBulletEntity(EntityType.SHULKER_BULLET, world));
        register(EntityType.SILVERFISH, state -> new SilverfishEntity(EntityType.SILVERFISH, world));
        register(EntityType.SKELETON, state -> new SkeletonEntity(EntityType.SKELETON, world));
        register(EntityType.SKELETON_HORSE, state -> EntityProperties.setIsBaby(state, new SkeletonHorseEntity(EntityType.SKELETON_HORSE, world)));
        register(EntityType.SLIME, state -> EntityProperties.setSize(state, new SlimeEntity(EntityType.SLIME, world)));
        register(EntityType.SMALL_FIREBALL, state -> new SmallFireballEntity(EntityType.SMALL_FIREBALL, world));
        register(EntityType.SNOW_GOLEM, state -> new SnowGolemEntity(EntityType.SNOW_GOLEM, world));
        register(EntityType.SNOWBALL, state -> new SnowballEntity(EntityType.SNOWBALL, world));
        register(EntityType.SPECTRAL_ARROW, state -> new SpectralArrowEntity(EntityType.SPECTRAL_ARROW, world));
        register(EntityType.SPIDER, state -> new SpiderEntity(EntityType.SPIDER, world));
        register(EntityType.SQUID, state -> new SquidEntity(EntityType.SQUID, world));
        register(EntityType.STRAY, state -> new StrayEntity(EntityType.STRAY, world));
        register(EntityType.STRIDER, state -> EntityProperties.setIsBaby(state, new StriderEntity(EntityType.STRIDER, world)));
        register(EntityType.TADPOLE, state -> new TadpoleEntity(EntityType.TADPOLE, world));
        register(EntityType.EGG, state -> new EggEntity(EntityType.EGG, world));
        register(EntityType.ENDER_PEARL, state -> new EnderPearlEntity(EntityType.ENDER_PEARL, world));
        register(EntityType.EXPERIENCE_BOTTLE, state -> new ExperienceBottleEntity(EntityType.EXPERIENCE_BOTTLE, world));
        register(EntityType.POTION, state -> new PotionEntity(EntityType.POTION, world));
        register(EntityType.TRIDENT, state -> new TridentEntity(EntityType.TRIDENT, world));
        register(EntityType.TRADER_LLAMA, state -> EntityProperties.setIsBaby(state, new TraderLlamaEntity(EntityType.TRADER_LLAMA, world)));
        register(EntityType.TROPICAL_FISH, state -> new TropicalFishEntity(EntityType.TROPICAL_FISH, world));
        register(EntityType.TURTLE, state -> EntityProperties.setIsBaby(state, new TurtleEntity(EntityType.TURTLE, world)));
        register(EntityType.VEX, state -> new VexEntity(EntityType.VEX, world));
        register(EntityType.VILLAGER, state -> EntityProperties.setPose(state, EntityProperties.setIsBaby(state, new VillagerEntity(EntityType.VILLAGER, world))));
        register(EntityType.VINDICATOR, state -> new VindicatorEntity(EntityType.VINDICATOR, world));
        register(EntityType.WANDERING_TRADER, state -> new WanderingTraderEntity(EntityType.WANDERING_TRADER, world));
        register(EntityType.WARDEN, state -> EntityProperties.setPose(state, new WardenEntity(EntityType.WARDEN, world)));
        register(EntityType.WITCH, state -> new WitchEntity(EntityType.WITCH, world));
        register(EntityType.WITHER, state -> new WitherEntity(EntityType.WITHER, world));
        register(EntityType.WITHER_SKELETON, state -> new WitherSkeletonEntity(EntityType.WITHER_SKELETON, world));
        register(EntityType.WITHER_SKULL, state -> new WitherSkullEntity(EntityType.WITHER_SKULL, world));
        register(EntityType.WOLF, state -> EntityProperties.setIsBaby(state, new WolfEntity(EntityType.WOLF, world)));
        register(EntityType.ZOGLIN, state -> EntityProperties.setIsBaby(state, new ZoglinEntity(EntityType.ZOGLIN, world)));
        register(EntityType.ZOMBIE, state -> EntityProperties.setIsBaby(state, new ZombieEntity(EntityType.ZOMBIE, world)));
        register(EntityType.ZOMBIE_HORSE, state -> EntityProperties.setIsBaby(state, new ZombieHorseEntity(EntityType.ZOMBIE_HORSE, world)));
        register(EntityType.ZOMBIE_VILLAGER, state -> EntityProperties.setIsBaby(state, new ZombieVillagerEntity(EntityType.ZOMBIE_VILLAGER, world)));
        register(EntityType.ZOMBIFIED_PIGLIN, state -> EntityProperties.setIsBaby(state, new ZombifiedPiglinEntity(EntityType.ZOMBIFIED_PIGLIN, world)));
        register(EntityType.PLAYER, state -> EntityProperties.setPose(state, new ClientPlayerEntity(client, clientWorld, networkHandler, stats, recipeBook, false, false)));
        register(EntityType.FISHING_BOBBER, state -> new FishingBobberEntity(EntityType.FISHING_BOBBER, world));
    }
}
