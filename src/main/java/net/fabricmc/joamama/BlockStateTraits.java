package net.fabricmc.joamama;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.joamama.mixin.FireBlockAccessor;
import net.fabricmc.joamama.mixin.FlowableFluidAccessor;
import net.fabricmc.joamama.mixin.RedStoneWireBlockAccessor;
import net.fabricmc.joamama.mixin.SpreadableBlockAccessor;
import net.fabricmc.joamama.mock.MockBlockView;
import net.fabricmc.joamama.mock.MockShapeContext;
import net.fabricmc.joamama.mock.MockWorldView;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("UnusedReturnValue")
public abstract class BlockStateTraits {
    private static final Vector<BlockState> blockStates;
//    private static final Map<Material, String> materialMap = new HashMap<>();
//    private static final Map<MaterialColor, String> mapColorMap = new HashMap<>();
    private static final Map<BlockTags, String> blockTags = new HashMap<>();

    static {
        blockStates = new Vector<>();
    }

    public static void load (Iterable<Block> blocks) {
        blocks.forEach(block -> blockStates.addAll(block.getStateDefinition().getPossibleStates()));

        // Fill the lookup maps
//        setupClassNames(Material.class, materialMap);
//        setupClassNames(MaterialColor.class, mapColorMap);
        setupClassNames(BlockTags.class, blockTags, true);
//        System.out.println(materialMap);
//        System.out.println(mapColorMap);
        System.out.println(blockTags);

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
    }

    public static ArrayList<String> getTheWholeThing () {

        return new ArrayList<>(
                List.of(
//                        new JoaProperty<>(
//                                "hardness",
//                                "Hardness",
//                                "Determines how fast the block can be mined",
//                                (state) -> state.getDestroySpeed(new MockBlockView(state), BlockPos.ZERO),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "blast_resistance",
//                                "Blast Resistance",
//                                "Determines how likely this block is to break from exposure to an explosion",
//                                (state) -> state.getBlock().getExplosionResistance(),
//                                // (state) -> state.isAir() && state.getFluidState().isEmpty() ? Optional.empty() : Optional.of(Math.max(state.getBlock().getBlastResistance(), state.getFluidState().getBlastResistance())),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "luminance",
//                                "Luminance",
//                                "",
//                                BlockBehaviour.BlockStateBase::getLightEmission,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "opaque",
//                                "Opaque",
//                                "Whether the block is visually opaque. Used solely in rendering.",
//                                BlockBehaviour.BlockStateBase::canOcclude,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "movable",
//                                "Movable",
//                                "Whether it can be pushed by a piston, stops the piston from extending, or whether attempting to push it destroys the block.",
//                                state -> switch (state.getPistonPushReaction()) {
//                                    case NORMAL, PUSH_ONLY -> "Yes";
//                                    case BLOCK -> "No";
//                                    case DESTROY -> "Breaks";
//                                    case IGNORE -> null;
//                                },
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "sticky",
//                                "Sticky",
//                                "If the block can be pulled by a sticky piston or an adjacent slime/honey block.</p><p>Slime and honey are listed as 'partially' as they are not sticky when pulled by one another.",
//                                state -> switch (state.getPistonPushReaction()) {
//                                    case NORMAL -> "Yes";
//                                    case BLOCK, DESTROY, PUSH_ONLY -> "No";
//                                    case IGNORE -> null;
//                                },
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "gets_random_ticked",
//                                "Gets Random Ticked",
//                                "Whether the block gets affected by random ticks.",
//                                BlockBehaviour.BlockStateBase::isRandomlyTicking,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "flammable",
//                                "Flammable",
//                                "",
//                                state -> ((FireBlockAccessor) Blocks.FIRE).invokeGetBurnOdds(state) > 0,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "burn_odds",
//                                "Burn Odds",
//                                "The higher the burn odds, the quicker a block burns away (when on fire). 0 means it is non-flammable.",
//                                ((FireBlockAccessor) Blocks.FIRE)::invokeGetBurnOdds,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "ignite_odds",
//                                "Ignite Odds",
//                                "The higher the ignite odds, the more likely a block is to catch fire (if it is able to spread there).",
//                                ((FireBlockAccessor) Blocks.FIRE)::invokeGetIgniteOdds,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "gets_random_ticked",
//                                "Gets Random Ticked",
//                                "",
//                                BlockBehaviour.BlockStateBase::isRandomlyTicking,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "conductive",
//                                "Conductive",
//                                "Whether or not a redstone component can be powered through this block.",
//                                (state) -> state.isRedstoneConductor(new MockBlockView(state), BlockPos.ZERO),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "suffocates_mobs",
//                                "Suffocates Mobs",
//                                "Whether a mob or player should suffocate in this block.",
//                                (state) -> state.isSuffocating(new MockBlockView(state), BlockPos.ZERO),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "opacity",
//                                "Opacity",
//                                "How much light the block... blocks.",
//                                (state) -> state.getLightBlock(new MockBlockView(state), BlockPos.ZERO),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "is_opaque_full_cube",
//                                "Is Opaque Full Cube",
//                                "",
//                                (state) -> state.isSolidRender(new MockBlockView(state), BlockPos.ZERO),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "bottom_face_has_full_square",
//                                "Bottom Face Has Full Square",
//                                "This is true if the bottom face is a full square.",
//                                (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockView(state), BlockPos.ZERO), Direction.DOWN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "top_face_has_full_square",
//                                "Top Face Has Full Square",
//                                "This is true if the top face is a full square.",
//                                (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockView(state), BlockPos.ZERO), Direction.UP),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "side_face_has_full_square",
//                                "Side Face Has Full Square (Notrh)",
//                                "This is true if the north face has a full, square surface.",
//                                (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockView(state), BlockPos.ZERO), Direction.NORTH),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "top_face_has_rim",
//                                "Top Face Has Rim",
//                                "This is true if the top face contains a 2 pixel wide ring going around its edge",
//                                (state) -> Block.canSupportRigidBlock(new MockBlockView(state), BlockPos.ZERO),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "bottom_face_has_small_square",
//                                "Bottom Face Has Small Square",
//                                "This is true if the bottom face contains a square of length 2 at its center.",
//                                (state) -> Block.canSupportCenter(new MockWorldView(state), BlockPos.ZERO, Direction.DOWN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "top_face_has_small_square",
//                                "Top Face Has Small Square",
//                                "This is true if the top face contains a square of length 2 at its center.",
//                                (state) -> Block.canSupportCenter(new MockWorldView(state), BlockPos.ZERO, Direction.UP),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "bottom_face_has_collision",
//                                "Bottom Face Has Collision",
//                                "This is true if the bottom side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
//                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ZERO).getFaceShape(Direction.DOWN).isEmpty(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "top_face_has_collision",
//                                "Top Face Has Collision",
//                                "This is true if the top side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
//                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ZERO).getFaceShape(Direction.UP).isEmpty(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "side_face_has_collision",
//                                "Side Face Has Collision (North)",
//                                "This is true if the north side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
//                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ZERO).getFaceShape(Direction.NORTH).isEmpty(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "has_collision",
//                                "Has Collision",
//                                "Whether the block has any solid collision box.",
//                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ZERO).isEmpty(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "redirects_redstone",
//                                "Redirects Redstone Wire (North)",
//                                "This is true if the North side connects to/redirects adjacent redstone dust.",
//                                (state) -> RedStoneWireBlockAccessor.invokeShouldConnectTo(state, Direction.SOUTH), // the direction is reversed so the "perspective" makes sense.
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "connects_to_panes",
//                                "Connects To Panes (North)",
//                                "Whether a glass pane block will connect to this block.",
//                                (state) -> ((IronBarsBlock) Blocks.GLASS_PANE).attachsTo(state, state.isFaceSturdy(new MockBlockView(state), BlockPos.ZERO, Direction.NORTH)),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "blocks_beacon_beam",
//                                "Blocks Beacon Beam",
//                                "Whether placing this block above a beacon will prevent its beam from forming, or stop its current one.",
//                                // net/minecraft/block/entity/BeaconBlockEntity.java:150
//                                (state) -> !( state.getLightBlock(new MockBlockView(state), BlockPos.ZERO) < 15 || state.is(Blocks.BEDROCK) ),
//                                blockStates
//                        ).toString(),
//
////                        new JoaProperty<>(
////                                "raid_spawnable",
////                                "Raid Spawnable",
////                                "Whether raids can spawn on this block.",
////                                // net/minecraft/village/raid/Raid.java:573
////                                (state) ->
////                                    SpawnHelper.canSpawn(
////                                        SpawnRestriction.Location.ON_GROUND,
////                                        new MockWorldView(state),
////                                        BlockPos.ORIGIN.up(),
////                                        EntityType.RAVAGER
////                                    ) || state.isOf(Blocks.SNOW),
////                                blockStates
////                        ).toString(),
//
//                        new JoaProperty<>(
//                                "full_cube",
//                                "Full Cube",
//                                "Whether the block has a normal cube shape and has full block collision on all sides.",
//                                (state) -> state.isCollisionShapeFullBlock(new MockWorldView(state), BlockPos.ZERO),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "bottom_face_has_full_square",
//                                "Bottom Face Has Full Square",
//                                "This is true if the bottom face is a full square.",
//                                (state) -> state.isFaceSturdy(new MockBlockView(state), BlockPos.ZERO, Direction.DOWN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "top_face_has_full_square",
//                                "Top Face Has Full Square",
//                                "This is true if the top face is a full square.",
//                                (state) -> state.isFaceSturdy(new MockBlockView(state), BlockPos.ZERO, Direction.UP),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "side_face_has_full_square",
//                                "Side Face Has Full Square (North)",
//                                "This is true if the north face is a full square.",
//                                (state) -> state.isFaceSturdy(new MockBlockView(state), BlockPos.ZERO, Direction.NORTH),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "waterloggable",
//                                "Waterloggable",
//                                "Whether this block can be waterlogged.",
//                                (state) -> state.getBlock() instanceof SimpleWaterloggedBlock ? true : Arrays.<Fluid>asList(Fluids.WATER, Fluids.FLOWING_WATER).contains(state.getFluidState().getType()) ? "Inherent" : false,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "supports_redstone_dust",
//                                "Supports Redstone Dust",
//                                "Whether redstone dust (\"wire\") can be placed on top of this block.",
//                                (state) -> ((RedStoneWireBlockAccessor) Blocks.REDSTONE_WIRE).invokeCanSurviveOn(new MockWorldView(state), BlockPos.ZERO, state),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "gets_flushed",
//                                "Gets Flushed",
//                                "Whether this block will get destroyed by flowing water or lava.",
//                                (state) -> ((FlowableFluidAccessor) Fluids.WATER).invokeCanSpreadTo(
//                                        new MockWorldView(state),
//                                        BlockPos.ZERO,
//                                        Blocks.WATER.defaultBlockState(),
//                                        Direction.NORTH,
//                                        BlockPos.ZERO.north(),
//                                        state,
//                                        state.getFluidState(),
//                                        Fluids.WATER
//                                    ) && !(state.getBlock() instanceof SimpleWaterloggedBlock),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "emits_power",
//                                "Emits Power",
//                                "Whether this block can emit redstone signals.",
//                                BlockBehaviour.BlockStateBase::isSignalSource,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "kills_grass",
//                                "Kills Grass",
//                                "Whether grass will die when placed underneath.",
//                                (state) -> !SpreadableBlockAccessor.invokeCanBeGrass(state, new MockWorldView(state), BlockPos.ZERO),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "exists_as_item",
//                                "Exists As Item",
//                                "Whether this block has a direct item-equivalent.",
//                                (state) -> !Objects.equals(state.getBlock().asItem().getDescription().getString(), "Air"),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "block_entity",
//                                "Block Entity",
//                                "",
//                                state -> {
//                                    if(state.getBlock() instanceof EntityBlock) {
//                                        try {
//                                            return state.getBlock().getClass().getMethod("getTicker", Level.class, BlockState.class, BlockEntityType.class).getDeclaringClass() != EntityBlock.class
//                                                    ? "Ticking"
//                                                    : "Non-Ticking";
//                                        } catch (NoSuchMethodException e) {
//                                            return "Goofy one";
//                                        }
//                                    }
//                                    return "No"; // not a block entity
//                                },
//                                blockStates
//                        ).toString(),
//
////                        new JoaProperty<>(
////                                "block_entity",
////                                "Block Entity",
////                                "",
////                                BlockBehaviour.BlockStateBase::hasBlockEntity,
////                                blockStates
////                        ).toString(),
//
//                        new JoaProperty<>(
//                                "height_all",
//                                "Height (All)",
//                                "The block's upwards-facing collision surfaces.</p><p>Includes internal collisions.</p><p>Defaults to pixel values, this can be converted in the settings.</p>",
//                                (state) -> state.getCollisionShape(
//                                        new MockBlockView(state),
//                                        BlockPos.ZERO,
//                                        new MockShapeContext(false, true, true, true)
//                                ).toAabbs().stream().map(box -> box.maxY * 16).distinct().toArray(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "width_all",
//                                "Width (All)",
//                                "The block's sideways-facing collision surfaces.</p><p>Includes internal collisions.</p><p>Defaults to pixel values, this can be converted in the settings.</p>",
//                                (state) -> {
//                                    JsonObject obj = new JsonObject();
//                                    obj.add(
//                                            "North",
//                                            jsonArrayFromStream(
//                                                    state.getCollisionShape(
//                                                            new MockBlockView(state),
//                                                            BlockPos.ZERO,
//                                                            new MockShapeContext(true, true, true, true)
//                                                    ).toAabbs().stream().map(box -> (1 - box.minZ) * 16).distinct()
//                                            )
//                                    );
//                                    obj.add(
//                                            "South",
//                                            jsonArrayFromStream(
//                                                    state.getCollisionShape(
//                                                            new MockBlockView(state),
//                                                            BlockPos.ZERO,
//                                                            new MockShapeContext(true, true, true, true)
//                                                    ).toAabbs().stream().map(box -> box.maxZ * 16).distinct()
//                                            )
//                                    );
//                                    obj.add(
//                                            "East",
//                                            jsonArrayFromStream(
//                                                    state.getCollisionShape(
//                                                            new MockBlockView(state),
//                                                            BlockPos.ZERO,
//                                                            new MockShapeContext(true, true, true, true)
//                                                    ).toAabbs().stream().map(box -> (1 - box.minX) * 16).distinct()
//                                            )
//                                    );
//                                    obj.add(
//                                            "West",
//                                            jsonArrayFromStream(
//                                                    state.getCollisionShape(
//                                                            new MockBlockView(state),
//                                                            BlockPos.ZERO,
//                                                            new MockShapeContext(true, true, true, true)
//                                                    ).toAabbs().stream().map(box -> box.maxX * 16).distinct()
//                                            )
//                                    );
//                                    return obj;
//                                },
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "collision_bottom_all",
//                                "Collision Bottom (All)",
//                                "The block's upwards-facing collision surfaces.</p><p>Includes internal collisions.</p><p>Defaults to pixel values, this can be converted in the settings.</p>",
//                                (state) -> state.getCollisionShape(
//                                        new MockBlockView(state),
//                                        BlockPos.ZERO,
//                                        new MockShapeContext(true, true, true, true)
//                                    ).toAabbs().stream().map(box -> (1 - box.minY) * 16).distinct().toArray(),
//                                blockStates
//                        ).toString(),
//
////                        // None of these are actually reliable ways to find what updates instantly, but it's a nice starting point
////                        new JoaProperty<>(
////                                "instant_shape_updater",
////                                "Instant Shape Updater",
////                                "",
////                                (state) -> {
////                                    try {
////                                        Class<?> declaringClass = state.getBlock().getClass().getMethod("getStateForNeighborUpdate", BlockState.class, Direction.class, BlockState.class, LevelAccessor.class, BlockPos.class, BlockPos.class).getDeclaringClass();
////                                        if(declaringClass == BlockBehaviour.class) return false;
////                                        List<Class<?>> instantDeclaringClasses = List.of(BlockBehaviour.class, BasePressurePlateBlock.class, AmethystClusterBlock.class, AttachedStemBlock.class, BambooSaplingBlock.class, BannerBlock.class, BedBlock.class, BeehiveBlock.class, BellBlock.class, BigDripleafBlock.class, CakeBlock.class, CampfireBlock.class, CandleCakeBlock.class, CarpetBlock.class, ChestBlock.class, CocoaBlock.class, ConcretePowderBlock.class, CoralPlantBlock.class, CoralFanBlock.class, BaseCoralPlantTypeBlock.class, CoralWallFanBlock.class, BaseCoralWallFanBlock.class, DoorBlock.class, FenceBlock.class, FenceGateBlock.class, FireBlock.class, FlowerPotBlock.class, LiquidBlock.class, FrogspawnBlock.class, HangingRootsBlock.class, CeilingHangingSignBlock.class, LadderBlock.class, LanternBlock.class, MultifaceBlock.class, HugeMushroomBlock.class, NetherPortalBlock.class, NoteBlock.class, IronBarsBlock.class, PistonHeadBlock.class, BushBlock.class, MangrovePropaguleBlock.class, RedStoneWireBlock.class, RepeaterBlock.class, SeagrassBlock.class, SeaPickleBlock.class, StandingSignBlock.class, SnowLayerBlock.class, SnowyDirtBlock.class, SoulFireBlock.class, SporeBlossomBlock.class, StairBlock.class, DoublePlantBlock.class, TorchBlock.class, TripWireBlock.class, TripWireHookBlock.class, VineBlock.class, WallBannerBlock.class, WallBlock.class, WallHangingSignBlock.class, FaceAttachedHorizontalDirectionalBlock.class, RedstoneWallTorchBlock.class, WallSignBlock.class, WallTorchBlock.class);
////                                        return instantDeclaringClasses.contains(declaringClass);
////
////                                    } catch (NoSuchMethodException e) {
////                                        e.printStackTrace();
////                                        return "No such method";
////                                    }
////                                },
////                                blockStates
////                        ).toString(),
////
////                        // None of these are actually reliable ways to find what updates instantly, but it's a nice starting point
////                        new JoaProperty<>(
////                                "instant_block_updater",
////                                "Instant Block Updater",
////                                "",
////                                (state) -> {
////                                    try {
////                                        Class<?> declaringClass = state.getBlock().getClass().getMethod("neighborUpdate", BlockState.class, Level.class, BlockPos.class, Block.class, BlockPos.class, boolean.class).getDeclaringClass();
////                                        if(declaringClass == BlockBehaviour.class) return false;
////                                        List<Class<?>> instantDeclaringClasses = List.of(BaseRailBlock.class, DiodeBlock.class, BellBlock.class, BigDripleafBlock.class, DoorBlock.class, FenceGateBlock.class, LiquidBlock.class, FrostedIceBlock.class, HopperBlock.class, NoteBlock.class, PistonBaseBlock.class, PistonHeadBlock.class, RedStoneWireBlock.class, SpongeBlock.class, StructureBlock.class, TntBlock.class, TrapDoorBlock.class, DispenserBlock.class, RedstoneLampBlock.class);
////                                        return instantDeclaringClasses.contains(declaringClass);
////
////                                    } catch (NoSuchMethodException e) {
////                                        e.printStackTrace();
////                                        return "No such method";
////                                    }
////                                },
////                                blockStates
////                        ).toString(),
////
////                        // None of these are actually reliable ways to find what updates instantly, but it's a nice starting point
////                        new JoaProperty<>(
////                                "instant_updater",
////                                "Instant Updater",
////                                "",
////                                (state) -> {
////                                    try {
////                                        Class<?> blockDeclaringClass = state.getBlock().getClass().getMethod("neighborUpdate", BlockState.class, Level.class, BlockPos.class, Block.class, BlockPos.class, boolean.class).getDeclaringClass();
////                                        List<Class<?>> instantBlockUpdateDeclaringClasses = List.of(BaseRailBlock.class, DiodeBlock.class, BellBlock.class, BigDripleafBlock.class, DoorBlock.class, FenceGateBlock.class, LiquidBlock.class, FrostedIceBlock.class, HopperBlock.class, NoteBlock.class, PistonBaseBlock.class, PistonHeadBlock.class, RedStoneWireBlock.class, SpongeBlock.class, StructureBlock.class, TntBlock.class, TrapDoorBlock.class, DispenserBlock.class, RedstoneLampBlock.class);
////
////                                        Class<?> shapeDeclaringClass = state.getBlock().getClass().getMethod("getStateForNeighborUpdate", BlockState.class, Direction.class, BlockState.class, LevelAccessor.class, BlockPos.class, BlockPos.class).getDeclaringClass();
////                                        List<Class<?>> instantShapeUpdateDeclaringClasses = List.of(BasePressurePlateBlock.class, AmethystClusterBlock.class, AttachedStemBlock.class, BambooSaplingBlock.class, BannerBlock.class, BedBlock.class, BeehiveBlock.class, BellBlock.class, BigDripleafBlock.class, CakeBlock.class, CampfireBlock.class, CandleCakeBlock.class, CarpetBlock.class, ChestBlock.class, CocoaBlock.class, ConcretePowderBlock.class, CoralPlantBlock.class, CoralFanBlock.class, BaseCoralPlantTypeBlock.class, CoralWallFanBlock.class, BaseCoralWallFanBlock.class, DoorBlock.class, FenceBlock.class, FenceGateBlock.class, FireBlock.class, FlowerPotBlock.class, LiquidBlock.class, FrogspawnBlock.class, HangingRootsBlock.class, CeilingHangingSignBlock.class, LadderBlock.class, LanternBlock.class, MultifaceBlock.class, HugeMushroomBlock.class, NetherPortalBlock.class, NoteBlock.class, IronBarsBlock.class, PistonHeadBlock.class, BushBlock.class, MangrovePropaguleBlock.class, RedStoneWireBlock.class, RepeaterBlock.class, SeagrassBlock.class, SeaPickleBlock.class, StandingSignBlock.class, SnowLayerBlock.class, SnowyDirtBlock.class, SoulFireBlock.class, SporeBlossomBlock.class, StairBlock.class, DoublePlantBlock.class, TorchBlock.class, TripWireBlock.class, TripWireHookBlock.class, VineBlock.class, WallBannerBlock.class, WallBlock.class, WallHangingSignBlock.class, FaceAttachedHorizontalDirectionalBlock.class, RedstoneWallTorchBlock.class, WallSignBlock.class, WallTorchBlock.class);
////
////                                        if(blockDeclaringClass == BlockBehaviour.class && shapeDeclaringClass == BlockBehaviour.class) return false;
////                                        return instantBlockUpdateDeclaringClasses.contains(blockDeclaringClass) || instantShapeUpdateDeclaringClasses.contains(shapeDeclaringClass);
////
////                                    } catch (NoSuchMethodException e) {
////                                        e.printStackTrace();
////                                        return "No such method";
////                                    }
////                                },
////                                blockStates
////                        ).toString(),
//                        new JoaProperty<>(
//                                "block_render_type",
//                                "Block Render Type",
//                                "",
//                                (state) -> ItemBlockRenderTypes.getChunkRenderType(state).toString(),
//                                blockStates
//                        ).toString(),
//                        new JoaProperty<>(
//                                "fluid_render_type",
//                                "Fluid Render Type",
//                                "",
//                                (state) -> ItemBlockRenderTypes.getRenderLayer(state.getFluidState()).toString(),
//                                blockStates
//                        ).toString()
//
//
//                        new JoaProperty<>(
//                                "blocks_skylight",
//                                "Blocks Skylight",
//                                "",
//                                (state) -> !state.propagatesSkylightDown(new MockBlockView(state), BlockPos.ZERO),
//                                blockStates
//                        ).toString(),
//                        new JoaProperty<>(
//                                "note_block_instrument",
//                                "Note Block Instrument",
//                                "Which instrument a noteblock will play if placed above this block.",
//                                (state) -> state.instrument().toString(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "solid",
//                                "(Deprecated) Solid",
//                                "blockState.isSolid",
//                                (state) -> state.isSolid(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "blocks_motion",
//                                "(Deprecated) Blocks Motion",
//                                "blockState.blocksMotion()",
//                                (state) -> state.blocksMotion(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "ignited_by_lava",
//                                "Ignited By Lava",
//                                "",
//                                BlockBehaviour.BlockStateBase::ignitedByLava,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "replaceable",
//                                "Replaceable",
//                                "",
//                                BlockBehaviour.BlockStateBase::canBeReplaced,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "obstructs_cactus",
//                                "Obstructs Cactus",
//                                "",
//                                (state) -> state.isSolid() || state.getFluidState().is(FluidTags.LAVA),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "get_map_color",
//                                "(Deprecated) getMapColor",
//                                "blockState.()",
//                                (state) -> state.getMapColor(new MockBlockView(state), BlockPos.ZERO).toString(),
//                                blockStates
//                        ).toString()
                )
        );
    }

    public static <T> void addBlockTagProperties(ArrayList<String> arr, Class<T> clazz) {
        for (Field staticField : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(staticField.getModifiers())) {
                try {
                    @SuppressWarnings("unchecked")
                    TagKey<Block> tag = (TagKey<Block>) staticField.get(null);
                    arr.add(new JoaProperty<>(
                            "tag_" + staticField.getName().toLowerCase(),
                            "Tag: " + staticField.getName(),
                            "" + staticField.getName(),
                            (state) -> state.is(tag),
                            // (state) -> state.isIn(BlockTags.NEEDS_IRON_TOOL),
                            blockStates
                    ).toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static JsonArray jsonArrayFromStream(Stream<Double> arr) {
        JsonArray jsonArray = new JsonArray();
        arr.forEach(jsonArray::add);
        return jsonArray;
    }
}
