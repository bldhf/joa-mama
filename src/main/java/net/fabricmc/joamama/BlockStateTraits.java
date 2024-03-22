package net.fabricmc.joamama;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.joamama.mixin.FireBlockAccessor;
import net.fabricmc.joamama.mixin.FlowableFluidAccessor;
import net.fabricmc.joamama.mixin.RedStoneWireBlockAccessor;
import net.fabricmc.joamama.mixin.SpreadableBlockAccessor;
import net.minecraft.client.Minecraft;
import net.fabricmc.joamama.mock.MockBlockGetter;
import net.fabricmc.joamama.mock.MockCollisionContext;
import net.fabricmc.joamama.mock.MockLevelReader;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnusedReturnValue")
public abstract class BlockStateTraits {
    private static final Vector<BlockState> blockStates;
//    private static final Map<Material, String> materialMap = new HashMap<>();
    private static final Map<MapColor, String> mapColorMap = new HashMap<>();
    private static final Map<BlockTags, String> blockTags = new HashMap<>();

    static {
        blockStates = new Vector<>();
    }

    public static void load (Iterable<Block> blocks) {
        blocks.forEach(block -> blockStates.addAll(block.getStateDefinition().getPossibleStates()));

        // Fill the lookup maps
//        setupClassNames(Material.class, materialMap);
        setupClassNames(MapColor.class, mapColorMap);
        setupClassNames(BlockTags.class, blockTags, true);
//        System.out.println(materialMap);
        JoaMama.LOGGER.info(mapColorMap.toString());
        JoaMama.LOGGER.info(blockTags.toString());

    }

    public static <T> void setupClassNames(Class<T> clazz, Map<T, String> map) {
        setupClassNames(clazz, map, false);
    }

    public static <T> void setupClassNames(Class<T> clazz, Map<T, String> map, Boolean ignoreClassType) {
        for(Field staticField : clazz.getDeclaredFields()) {
            if((staticField.getType() == clazz || ignoreClassType ) &&
                    Modifier.isStatic(staticField.getModifiers())) {
                try {
                    @SuppressWarnings("unchecked")
                    T value = (T)staticField.get(null);
                    map.put(value, staticField.getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        JoaMama.LOGGER.info(map.toString());
    }

    public static boolean isWater (BlockState state) {
        FluidState fluidState = state.getFluidState();
        return fluidState.is(Fluids.FLOWING_WATER) || fluidState.is(Fluids.WATER);
    }

    public static boolean isLava (BlockState state) {
        FluidState fluidState = state.getFluidState();
        return fluidState.is(Fluids.FLOWING_LAVA) || fluidState.is(Fluids.LAVA);
    }

    public static boolean isWaterSource (BlockState state) {
        FluidState fluidState = state.getFluidState();
        return (fluidState.is(Fluids.FLOWING_WATER) || fluidState.is(Fluids.WATER)) && fluidState.getAmount() == 8;
    }

    public static ArrayList<String> getTheWholeThing () {
        return new ArrayList<>(
                List.of(
                        /*new JoaProperty<>(
                                "hardness",
                                "Hardness",
                                "Determines how fast the block can be mined.",
                                (state) -> state.getDestroySpeed(new MockBlockGetter(state), BlockPos.ZERO),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "blast_resistance",
                                "Blast Resistance",
                                "Determines how likely the block is to break from exposure to an explosion.",
                                (state) -> state.getBlock().getExplosionResistance(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "luminance",
                                "Luminance",
                                "How much light the block emits.",
                                BlockBehaviour.BlockStateBase::getLightEmission,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "opaque",
                                "Opaque",
                                "Whether the block is visually opaque - not used solely in rendering, thank you very much Joa.",
                                BlockBehaviour.BlockStateBase::canOcclude,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "movable",
                                "Movable",
                                "Whether the block can be pushed by a piston, stops the piston from extending, or whether attempting to push it destroys the block.",
                                state -> switch (state.getPistonPushReaction()) {
                                    case NORMAL, PUSH_ONLY -> "Yes";
                                    case BLOCK -> "No";
                                    case DESTROY -> "Breaks";
                                    case IGNORE -> null;
                                },
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "sticky",
                                "Sticky",
                                "Whether the block can be pulled by a sticky piston or an adjacent slime/honey block.</p><p>Slime and honey are listed as 'partially' as they are not sticky when pulled by one another.",
                                state -> switch (state.getPistonPushReaction()) {
                                    case NORMAL -> "Yes";
                                    case PUSH_ONLY, BLOCK, DESTROY -> "No";
                                    case IGNORE -> null;
                                },
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "gets_random_ticked",
                                "Gets Random Ticked",
                                "Whether the block gets affected by random ticks.",
                                BlockBehaviour.BlockStateBase::isRandomlyTicking,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "flammable",
                                "Flammable",
                                "Whether the block can be destroyed by fire.",
                                state -> ((FireBlockAccessor) Blocks.FIRE).invokeGetBurnOdds(state) > 0,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "burn_odds",
                                "Burn Odds",
                                "The higher the burn odds, the quicker a block burns away (when on fire). 0 means it is non-flammable.",
                                ((FireBlockAccessor) Blocks.FIRE)::invokeGetBurnOdds,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "ignite_odds",
                                "Ignite Odds",
                                "The higher the ignite odds, the more likely a block is to catch fire (if it is able to spread there).",
                                ((FireBlockAccessor) Blocks.FIRE)::invokeGetIgniteOdds,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "conductive",
                                "Conductive",
                                "Whether or not a redstone component can be powered through this block.",
                                (state) -> state.isRedstoneConductor(new MockBlockGetter(state), BlockPos.ZERO),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "suffocates_mobs",
                                "Suffocates Mobs",
                                "Whether a mob or player should suffocate in this block.",
                                (state) -> state.isSuffocating(new MockBlockGetter(state), BlockPos.ZERO),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "opacity",
                                "Opacity",
                                "How much light the block...blocks.",
                                (state) -> state.getLightBlock(new MockBlockGetter(state), BlockPos.ZERO),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "is_opaque_full_cube",
                                "Is Opaque Full Cube",
                                "Whether the block is opaque and renders as a full cube. Note that this is not an AND of Opaque and Full Cube,</p><p>as this uses the rendering shape and Full Cube uses the collision shape.",
                                (state) -> state.isSolidRender(new MockBlockGetter(state), BlockPos.ZERO),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "bottom_face_has_full_square",
                                "Bottom Face Has Full Square",
                                "This is true if the bottom face is a full square.",
                                (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockGetter(state), BlockPos.ZERO), Direction.DOWN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "top_face_has_full_square",
                                "Top Face Has Full Square",
                                "This is true if the top face is a full square.",
                                (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockGetter(state), BlockPos.ZERO), Direction.UP),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "side_face_has_full_square",
                                "Side Face Has Full Square (North)",
                                "This is true if the north face has a full, square surface.",
                                (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockGetter(state), BlockPos.ZERO), Direction.NORTH),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "top_face_has_rim",
                                "Top Face Has Rim",
                                "This is true if the top face contains a 2 pixel wide ring going around its edge",
                                (state) -> Block.canSupportRigidBlock(new MockBlockGetter(state), BlockPos.ZERO),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "bottom_face_has_small_square",
                                "Bottom Face Has Small Square",
                                "This is true if the bottom face contains a square of length 2 at its center.",
                                (state) -> Block.canSupportCenter(new MockLevelReader(state), BlockPos.ZERO, Direction.DOWN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "top_face_has_small_square",
                                "Top Face Has Small Square",
                                "This is true if the top face contains a square of length 2 at its center.",
                                (state) -> Block.canSupportCenter(new MockLevelReader(state), BlockPos.ZERO, Direction.UP),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "bottom_face_has_collision",
                                "Bottom Face Has Collision",
                                "This is true if the bottom side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                                (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).getFaceShape(Direction.DOWN).isEmpty(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "top_face_has_collision",
                                "Top Face Has Collision",
                                "This is true if the top side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                                (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).getFaceShape(Direction.UP).isEmpty(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "side_face_has_collision",
                                "Side Face Has Collision (North)",
                                "This is true if the north side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                                (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).getFaceShape(Direction.NORTH).isEmpty(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "has_collision",
                                "Has Collision",
                                "Whether the block has any solid collision box.",
                                (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).isEmpty(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "redirects_redstone",
                                "Redirects Redstone Wire (North)",
                                "This is true if the North side connects to/redirects adjacent redstone dust.",
                                (state) -> RedStoneWireBlockAccessor.invokeShouldConnectTo(state, Direction.SOUTH), // the direction is reversed so the "perspective" makes sense.
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "connects_to_panes",
                                "Connects To Panes (North)",
                                "Whether a glass pane block will connect to this block.",
                                (state) -> ((IronBarsBlock) Blocks.GLASS_PANE).attachsTo(state, state.isFaceSturdy(new MockBlockGetter(state), BlockPos.ZERO, Direction.NORTH)),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "blocks_beacon_beam",
                                "Blocks Beacon Beam",
                                "Whether placing this block above a beacon will prevent its beam from forming, or stop its current one.",
                                // net/minecraft/block/entity/BeaconBlockEntity.java:150
                                (state) -> !( state.getLightBlock(new MockBlockGetter(state), BlockPos.ZERO) < 15 || state.is(Blocks.BEDROCK) ),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "raid_spawnable",
                                "Raid Spawnable",
                                "Whether raids can spawn on this block.",
                                (state) ->
                                        NaturalSpawner.isSpawnPositionOk(
                                                SpawnPlacements.Type.ON_GROUND,
                                                new MockLevelReader(state),
                                                BlockPos.ZERO.above(),
                                                EntityType.RAVAGER
                                        ) || state.is(Blocks.SNOW),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "full_cube",
                                "Full Cube",
                                "Whether the block has a normal cube shape and has full block collision on all sides.",
                                (state) -> state.isCollisionShapeFullBlock(new MockLevelReader(state), BlockPos.ZERO),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "bottom_face_has_full_square",
                                "Bottom Face Has Full Square",
                                "This is true if the bottom face is a full square.",
                                (state) -> state.isFaceSturdy(new MockBlockGetter(state), BlockPos.ZERO, Direction.DOWN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "top_face_has_full_square",
                                "Top Face Has Full Square",
                                "This is true if the top face is a full square.",
                                (state) -> state.isFaceSturdy(new MockBlockGetter(state), BlockPos.ZERO, Direction.UP),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "side_face_has_full_square",
                                "Side Face Has Full Square (North)",
                                "This is true if the north face is a full square.",
                                (state) -> state.isFaceSturdy(new MockBlockGetter(state), BlockPos.ZERO, Direction.NORTH),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "waterloggable",
                                "Waterloggable",
                                "Whether this block can be waterlogged.",
                                (state) -> (
                                    state.getBlock() instanceof SimpleWaterloggedBlock ? true :
                                    isWater(state) ? "Inherent" : false
                                ),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "supports_redstone_dust",
                                "Supports Redstone Dust",
                                "Whether redstone dust (\"wire\") can be placed on top of this block.",
                                (state) -> ((RedStoneWireBlockAccessor) Blocks.REDSTONE_WIRE).invokeCanSurviveOn(new MockLevelReader(state), BlockPos.ZERO, state),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "gets_flushed",
                                "Gets Flushed",
                                "Whether this block will get destroyed by flowing water or lava.",
                                (state) -> ((FlowableFluidAccessor) Fluids.WATER).invokeCanSpreadTo(
                                        new MockLevelReader(state),
                                        BlockPos.ZERO,
                                        Blocks.WATER.defaultBlockState(),
                                        Direction.NORTH,
                                        BlockPos.ZERO.north(),
                                        state,
                                        state.getFluidState(),
                                        Fluids.WATER
                                    ) && !(state.getBlock() instanceof SimpleWaterloggedBlock),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "emits_power",
                                "Emits Power",
                                "Whether this block can emit redstone signals.",
                                BlockBehaviour.BlockStateBase::isSignalSource,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "kills_grass",
                                "Kills Grass",
                                "Whether grass will die when placed underneath this block.",
                                (state) -> !SpreadableBlockAccessor.invokeCanBeGrass(state, new MockLevelReader(state), BlockPos.ZERO),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "exists_as_item",
                                "Exists As Item",
                                "Whether this block has a direct item equivalent.",
                                (state) -> !Objects.equals(state.getBlock().asItem().getDescription().getString(), "Air"),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "block_entity",
                                "Block Entity",
                                "Whether this block has an associated block entity, and whether it's ticking or non-ticking.",
                                state -> {
                                    if(state.getBlock() instanceof EntityBlock) {
                                        try {
                                            return state.getBlock().getClass().getMethod("getTicker", Level.class, BlockState.class, BlockEntityType.class).getDeclaringClass() != EntityBlock.class
                                                    ? "Ticking"
                                                    : "Non-Ticking";
                                        } catch (NoSuchMethodException e) {
                                            return "Goofy one <- this made me laugh, thanks Joa";
                                        }
                                    }
                                    return "No"; // not a block entity
                                },
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "height_all",
                                "Height (All)",
                                "The block's upwards-facing collision surfaces.</p><p>Includes internal collisions.</p><p>Defaults to pixel values, this can be converted in the settings.</p>",
                                (state) -> state.getCollisionShape(
                                        new MockBlockGetter(state),
                                        BlockPos.ZERO,
                                        new MockCollisionContext(false, true, true, true)
                                ).toAabbs().stream().map(box -> box.maxY * 16).distinct().toArray(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "width_all",
                                "Width (All)",
                                "The block's sideways-facing collision surfaces.</p><p>Includes internal collisions.</p><p>Defaults to pixel values, this can be converted in the settings.</p>",
                                (state) -> {
                                    JsonObject obj = new JsonObject();
                                    obj.add(
                                            "North",
                                            jsonArrayFromStream(
                                                    state.getCollisionShape(
                                                            new MockBlockGetter(state),
                                                            BlockPos.ZERO,
                                                            new MockCollisionContext(true, true, true, true)
                                                    ).toAabbs().stream().map(box -> (1 - box.minZ) * 16).distinct()
                                            )
                                    );
                                    obj.add(
                                            "South",
                                            jsonArrayFromStream(
                                                    state.getCollisionShape(
                                                            new MockBlockGetter(state),
                                                            BlockPos.ZERO,
                                                            new MockCollisionContext(true, true, true, true)
                                                    ).toAabbs().stream().map(box -> box.maxZ * 16).distinct()
                                            )
                                    );
                                    obj.add(
                                            "East",
                                            jsonArrayFromStream(
                                                    state.getCollisionShape(
                                                            new MockBlockGetter(state),
                                                            BlockPos.ZERO,
                                                            new MockCollisionContext(true, true, true, true)
                                                    ).toAabbs().stream().map(box -> (1 - box.minX) * 16).distinct()
                                            )
                                    );
                                    obj.add(
                                            "West",
                                            jsonArrayFromStream(
                                                    state.getCollisionShape(
                                                            new MockBlockGetter(state),
                                                            BlockPos.ZERO,
                                                            new MockCollisionContext(true, true, true, true)
                                                    ).toAabbs().stream().map(box -> box.maxX * 16).distinct()
                                            )
                                    );
                                    return obj;
                                },
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "collision_bottom_all",
                                "Collision Bottom (All)",
                                "The block's upwards-facing collision surfaces.</p><p>Includes internal collisions.</p><p>Defaults to pixel values, this can be converted in the settings.</p>",
                                (state) -> state.getCollisionShape(
                                        new MockBlockGetter(state),
                                        BlockPos.ZERO,
                                        new MockCollisionContext(true, true, true, true)
                                    ).toAabbs().stream().map(box -> (1 - box.minY) * 16).distinct().toArray(),
                                blockStates
                        ).toString(),*/

/*                        // None of these are actually reliable ways to find what updates instantly, but it's a nice starting point
                        new JoaProperty<>(
                                "instant_shape_updater",
                                "Instant Shape Updater",
                                "",
                                (state) -> {
                                    try {
                                        Class<?> declaringClass = state.getBlock().getClass().getMethod("getStateForNeighborUpdate", BlockState.class, Direction.class, BlockState.class, LevelAccessor.class, BlockPos.class, BlockPos.class).getDeclaringClass();
                                        if(declaringClass == BlockBehaviour.class) return false;
                                        List<Class<?>> instantDeclaringClasses = List.of(BlockBehaviour.class, BasePressurePlateBlock.class, AmethystClusterBlock.class, AttachedStemBlock.class, BambooSaplingBlock.class, BannerBlock.class, BedBlock.class, BeehiveBlock.class, BellBlock.class, BigDripleafBlock.class, CakeBlock.class, CampfireBlock.class, CandleCakeBlock.class, CarpetBlock.class, ChestBlock.class, CocoaBlock.class, ConcretePowderBlock.class, CoralPlantBlock.class, CoralFanBlock.class, BaseCoralPlantTypeBlock.class, CoralWallFanBlock.class, BaseCoralWallFanBlock.class, DoorBlock.class, FenceBlock.class, FenceGateBlock.class, FireBlock.class, FlowerPotBlock.class, LiquidBlock.class, FrogspawnBlock.class, HangingRootsBlock.class, CeilingHangingSignBlock.class, LadderBlock.class, LanternBlock.class, MultifaceBlock.class, HugeMushroomBlock.class, NetherPortalBlock.class, NoteBlock.class, IronBarsBlock.class, PistonHeadBlock.class, BushBlock.class, MangrovePropaguleBlock.class, RedStoneWireBlock.class, RepeaterBlock.class, SeagrassBlock.class, SeaPickleBlock.class, StandingSignBlock.class, SnowLayerBlock.class, SnowyDirtBlock.class, SoulFireBlock.class, SporeBlossomBlock.class, StairBlock.class, DoublePlantBlock.class, TorchBlock.class, TripWireBlock.class, TripWireHookBlock.class, VineBlock.class, WallBannerBlock.class, WallBlock.class, WallHangingSignBlock.class, FaceAttachedHorizontalDirectionalBlock.class, RedstoneWallTorchBlock.class, WallSignBlock.class, WallTorchBlock.class);
                                        return instantDeclaringClasses.contains(declaringClass);

                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                        return "No such method";
                                    }
                                },
                                blockStates
                        ).toString(),

                        // None of these are actually reliable ways to find what updates instantly, but it's a nice starting point
                        new JoaProperty<>(
                                "instant_block_updater",
                                "Instant Block Updater",
                                "",
                                (state) -> {
                                    try {
                                        Class<?> declaringClass = state.getBlock().getClass().getMethod("neighborUpdate", BlockState.class, Level.class, BlockPos.class, Block.class, BlockPos.class, boolean.class).getDeclaringClass();
                                        if(declaringClass == BlockBehaviour.class) return false;
                                        List<Class<?>> instantDeclaringClasses = List.of(BaseRailBlock.class, DiodeBlock.class, BellBlock.class, BigDripleafBlock.class, DoorBlock.class, FenceGateBlock.class, LiquidBlock.class, FrostedIceBlock.class, HopperBlock.class, NoteBlock.class, PistonBaseBlock.class, PistonHeadBlock.class, RedStoneWireBlock.class, SpongeBlock.class, StructureBlock.class, TntBlock.class, TrapDoorBlock.class, DispenserBlock.class, RedstoneLampBlock.class);
                                        return instantDeclaringClasses.contains(declaringClass);

                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                        return "No such method";
                                    }
                                },
                                blockStates
                        ).toString(),

                        // None of these are actually reliable ways to find what updates instantly, but it's a nice starting point
                        new JoaProperty<>(
                                "instant_updater",
                                "Instant Updater",
                                "",
                                (state) -> {
                                    try {
                                        Class<?> blockDeclaringClass = state.getBlock().getClass().getMethod("neighborUpdate", BlockState.class, Level.class, BlockPos.class, Block.class, BlockPos.class, boolean.class).getDeclaringClass();
                                        List<Class<?>> instantBlockUpdateDeclaringClasses = List.of(BaseRailBlock.class, DiodeBlock.class, BellBlock.class, BigDripleafBlock.class, DoorBlock.class, FenceGateBlock.class, LiquidBlock.class, FrostedIceBlock.class, HopperBlock.class, NoteBlock.class, PistonBaseBlock.class, PistonHeadBlock.class, RedStoneWireBlock.class, SpongeBlock.class, StructureBlock.class, TntBlock.class, TrapDoorBlock.class, DispenserBlock.class, RedstoneLampBlock.class);

                                        Class<?> shapeDeclaringClass = state.getBlock().getClass().getMethod("getStateForNeighborUpdate", BlockState.class, Direction.class, BlockState.class, LevelAccessor.class, BlockPos.class, BlockPos.class).getDeclaringClass();
                                        List<Class<?>> instantShapeUpdateDeclaringClasses = List.of(BasePressurePlateBlock.class, AmethystClusterBlock.class, AttachedStemBlock.class, BambooSaplingBlock.class, BannerBlock.class, BedBlock.class, BeehiveBlock.class, BellBlock.class, BigDripleafBlock.class, CakeBlock.class, CampfireBlock.class, CandleCakeBlock.class, CarpetBlock.class, ChestBlock.class, CocoaBlock.class, ConcretePowderBlock.class, CoralPlantBlock.class, CoralFanBlock.class, BaseCoralPlantTypeBlock.class, CoralWallFanBlock.class, BaseCoralWallFanBlock.class, DoorBlock.class, FenceBlock.class, FenceGateBlock.class, FireBlock.class, FlowerPotBlock.class, LiquidBlock.class, FrogspawnBlock.class, HangingRootsBlock.class, CeilingHangingSignBlock.class, LadderBlock.class, LanternBlock.class, MultifaceBlock.class, HugeMushroomBlock.class, NetherPortalBlock.class, NoteBlock.class, IronBarsBlock.class, PistonHeadBlock.class, BushBlock.class, MangrovePropaguleBlock.class, RedStoneWireBlock.class, RepeaterBlock.class, SeagrassBlock.class, SeaPickleBlock.class, StandingSignBlock.class, SnowLayerBlock.class, SnowyDirtBlock.class, SoulFireBlock.class, SporeBlossomBlock.class, StairBlock.class, DoublePlantBlock.class, TorchBlock.class, TripWireBlock.class, TripWireHookBlock.class, VineBlock.class, WallBannerBlock.class, WallBlock.class, WallHangingSignBlock.class, FaceAttachedHorizontalDirectionalBlock.class, RedstoneWallTorchBlock.class, WallSignBlock.class, WallTorchBlock.class);

                                        if(blockDeclaringClass == BlockBehaviour.class && shapeDeclaringClass == BlockBehaviour.class) return false;
                                        return instantBlockUpdateDeclaringClasses.contains(blockDeclaringClass) || instantShapeUpdateDeclaringClasses.contains(shapeDeclaringClass);

                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                        return "No such method";
                                    }
                                },
                                blockStates
                        ).toString(),*//*

                        new JoaProperty<>(
                                "block_render_type",
                                "Block Render Type",
                                "TODO",
                                (state) -> ItemBlockRenderTypes.getChunkRenderType(state).toString(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "fluid_render_type",
                                "Fluid Render Type",
                                "TODO",
                                (state) -> ItemBlockRenderTypes.getRenderLayer(state.getFluidState()).toString(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "blocks_skylight",
                                "Blocks Skylight",
                                "Whether this block...blocks...skylight.",
                                (state) -> !state.propagatesSkylightDown(new MockBlockGetter(state), BlockPos.ZERO),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "instrument",
                                "Instrument",
                                "Which instrument a note block will play if placed above this block. This does not include mob heads.",
                                (state) -> state.instrument().toString(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "solid",
                                "(Legacy) Solid",
                                "Not fully accurate to its name; remains from when materials were still used.</p><p>Marked as deprecated in the code but still used extensively.",
                                BlockBehaviour.BlockStateBase::isSolid,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "blocks_motion",
                                "(Legacy) Blocks Motion",
                                "Not fully accurate to its name; remains from when materials were still used.</p><p>Marked as deprecated in the code but still used extensively.",
                                BlockBehaviour.BlockStateBase::blocksMotion,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "liquid",
                                "(Legacy) Liquid",
                                "Not fully accurate to its name; remains from when materials were still used.</p><p>Marked as deprecated in the code but still used extensively.",
                                BlockBehaviour.BlockStateBase::liquid,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "ignited_by_lava",
                                "Ignited By Lava",
                                "Whether lava can set this block on fire.",
                                BlockBehaviour.BlockStateBase::ignitedByLava,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "replaceable",
                                "Replaceable",
                                "Determines whether a block placed or falling on this block will replace it rather than being placed against or on it.",
                                BlockBehaviour.BlockStateBase::canBeReplaced,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "obstructs_cactus",
                                "Obstructs Cactus",
                                "Whether a cactus will be destroyed when adjacent to this block.",
                                (state) -> state.isSolid() || isLava(state)),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "get_map_color",
                                "Map Color",
                                "The map color of this block. Note that waterlogged blocks will have the map color of water.",
                                (state) -> "{{mapColor|" + mapColorMap.get(state.getMapColor(new MockBlockView(state), BlockPos.ZERO)) + "}}",
                                blockStates
                        ).toString(),*/
                        
                        new JoaProperty<>(
                               "falling_block",
                               "Falling Block (unfinished: scaffolding and dripstone wrong)",
                               "",
                               (state) -> state.getBlock() instanceof Fallable,
                               blockStates
                       ).toString(),

                       new JoaProperty<>(
                               "unknown",
                               "Unknown",
                               "",
                               BlockBehaviour.BlockStateBase::hasAnalogOutputSignal,
                               blockStates
                       ).toString()/*,

                       new JoaProperty<>(
                               "water_forms_source_above",
                               "Water Forms Sources Above",
                               "Whether water can form a source above this block.</p><p>If infinite lava is on, it will form sources above the same blocks, but above lava and not above water.",
                               (state) -> state.isSolid() || isWaterSource(state),
                               blockStates
                       ).toString(),

                        new JoaProperty<>(
                                "spawnable",
                                "Spawnable On",
                                "Whether mobs can spawn on this block.",
                                (state) -> (
                                    state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.PLAYER) ? "Yes" :
                                    state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.OCELOT) ? "Ocelots and Parrots Only" :
                                    state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.POLAR_BEAR) ? "Polar Bear Only" :
                                    state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.BLAZE) ? "Fire-Immune Mobs Only" : "No"
                                    //                                                                       O
                                    //                                                                       B
                                ),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "spawnable_in",
                                "Spawnable In",
                                "Whether mobs can spawn in this block.",
                                (state) -> state.getMapColor(new MockBlockGetter(state), BlockPos.ZERO).toString(),
                                blockStates
                        ).toString()*/
                )
        );
    }

    public static <T> void addBlockTagProperties(ArrayList<String> arr, Class<T> clazz) {
//        for (Field staticField : clazz.getDeclaredFields()) {
//            if (Modifier.isStatic(staticField.getModifiers())) {
//                try {
//                    @SuppressWarnings("unchecked")
//                    TagKey<Block> tag = (TagKey<Block>) staticField.get(null);
//                    arr.add(new JoaProperty<>(
//                            "tag_" + staticField.getName().toLowerCase(),
//                            "Tag: " + staticField.getName(),
//                            "" + staticField.getName(),
//                            (state) -> state.is(tag),
//                            // (state) -> state.isIn(BlockTags.NEEDS_IRON_TOOL),
//                            blockStates
//                    ).toString());
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

//        arr.add(
//                new JoaProperty<>(
//                        "dragon_immune",
//                        "Dragon Immune",
//                        "Whether this block is immune to The Ender Dragon flying through it.",
//                        (state) -> state.is(BlockTags.DRAGON_IMMUNE) || state.is(BlockTags.DRAGON_TRANSPARENT),
//                        blockStates
//                ).toString());
//        arr.add(
//                new JoaProperty<>(
//                        "wither_blockbreak_immune",
//                        "Wither Block Break Immune",
//                        "Whether this block is immune to the wither's block breaking attack.",
//                        (state) -> state.is(BlockTags.WITHER_IMMUNE) || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.BUBBLE_COLUMN,
//                        blockStates
//                ).toString());
//        arr.add(
//                new JoaProperty<>(
//                        "wither_immune",
//                        "Wither Skull Immune",
//                        "Whether this block is immune to the wither's skull attack.",
//                        (state) -> {
//                            float expRes = Math.max(state.getBlock().getExplosionResistance(), state.getFluidState().getExplosionResistance());
//                            // net.minecraft.world.entity.projectile.WitherSkull.getBlockExplosionResistance
//                            if (1.3 < 0.3*(0.3+expRes)) {
//                                if (WitherBoss.canDestroy(state)) {
//                                    return "Only to black skulls";
//                                }
//                                return "Yes";
//                            }
//                            return "No";
//                        },
//                        blockStates
//                ).toString());

//        var manager = Minecraft.getInstance().getSingleplayerServer().getStructureManager();
//        var blocksInStructures = manager.listTemplates()
//                .map(rl -> manager.get(rl).orElseThrow())
//                .flatMap(template -> {
//                    try {
//                        var palettesField = template.getClass().getDeclaredField("palettes");
//                        palettesField.setAccessible(true);
//                        var palettes = (List<StructureTemplate.Palette>) palettesField.get(template);
//                        return palettes.stream()
//                                .map(StructureTemplate.Palette::blocks)
//                                .flatMap(blockInfos -> blockInfos
//                                        .stream()
//                                        .map(StructureTemplate.StructureBlockInfo::state)
//                                        .map(BlockState::getBlock)
//                                );
//                    } catch (NoSuchFieldException e) {
//                        System.out.println("Generates in structures: Fuck 1");
//                    } catch (IllegalAccessException e) {
//                        System.out.println("Generates in structures: Fuck 2");
//                    }
//                    return Stream.of();
//                })
//                .collect(Collectors.toUnmodifiableSet());
//        arr.add(
//                new JoaProperty<>(
//                        "generates_in_structures",
//                        "Generates in Structures",
//                        "Based off of what blocks show up in the standard structure palletes. Does not include all complex structures.",
//                        state -> blocksInStructures.contains(state.getBlock()),
//                        blockStates
//                ).toString()
//        );
    }

    public static JsonArray jsonArrayFromStream(Stream<Double> arr) {
        JsonArray jsonArray = new JsonArray();
        arr.forEach(jsonArray::add);
        return jsonArray;
    }
}
