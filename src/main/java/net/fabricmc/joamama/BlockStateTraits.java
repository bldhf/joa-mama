package net.fabricmc.joamama;

import com.google.gson.JsonArray;
import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("UnusedReturnValue")
public abstract class BlockStateTraits {
    private static final Vector<BlockState> blockStates;
    private static final Map<Material, String> materialMap = new HashMap<>();
    private static final Map<MapColor, String> mapColorMap = new HashMap<>();
    private static final Map<BlockTags, String> blockTags = new HashMap<>();

    static {
        blockStates = new Vector<>();
    }

    public static void load (Iterable<Block> blocks) {
        blocks.forEach(block -> blockStates.addAll(block.getStateManager().getStates()));

        // Fill the lookup maps
        setupClassNames(Material.class, materialMap);
        setupClassNames(MapColor.class, mapColorMap);
        setupClassNames(BlockTags.class, blockTags, true);
        System.out.println(materialMap);
        System.out.println(mapColorMap);
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
//                                (state) -> state.getHardness(new MockBlockView(state), BlockPos.ORIGIN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "blast_resistance",
//                                "Blast Resistance",
//                                "Determines how likely this block is to break from exposure to an explosion",
//                                (state) -> state.getBlock().getBlastResistance(),
//                                // (state) -> state.isAir() && state.getFluidState().isEmpty() ? Optional.empty() : Optional.of(Math.max(state.getBlock().getBlastResistance(), state.getFluidState().getBlastResistance())),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "luminance",
//                                "Luminance",
//                                "",
//                                AbstractBlock.AbstractBlockState::getLuminance,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "material",
//                                "Material",
//                                "",
//                                (state) -> materialMap.get(state.getMaterial()),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "material_is_liquid",
//                                "Material isLiquid()",
//                                "",
//                                (state) -> state.getMaterial().isLiquid(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "material_is_solid",
//                                "Material isSolid()",
//                                "",
//                                (state) -> state.getMaterial().isSolid(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "material_blocks_movement",
//                                "Material blocksMovement()",
//                                "",
//                                (state) -> state.getMaterial().blocksMovement(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "material_is_burnable",
//                                "Material isBurnable()",
//                                "",
//                                (state) -> state.getMaterial().isBurnable(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "material_is_replaceable",
//                                "Material isReplaceable()",
//                                "",
//                                (state) -> state.getMaterial().isReplaceable(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "material_blocks_light",
//                                "Material blocksLight()",
//                                "",
//                                (state) -> state.getMaterial().blocksLight(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "map_color",
//                                "Map Color",
//                                "",
//                                (state) -> mapColorMap.get(state.getMapColor(new MockBlockView(state), BlockPos.ORIGIN)),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "opaque",
//                                "Opaque",
//                                "",
//                                AbstractBlock.AbstractBlockState::isOpaque,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "movable",
//                                "Movable",
//                                "Whether it can be pushed by a piston, stops the piston from extending, or whether attempting to push it destroys the block.",
//                                state -> switch (state.getPistonBehavior()) {
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
//                                state -> switch (state.getPistonBehavior()) {
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
//                                "",
//                                AbstractBlock.AbstractBlockState::hasRandomTicks,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "flammable",
//                                "Flammable",
//                                "",
//                                state -> ((FireBlockAccessor) Blocks.FIRE).invokeGetBurnChance(state) > 0,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "burn_odds",
//                                "Burn Odds",
//                                "The higher the burn odds, the quicker a block burns away (when on fire). 0 means it is non-flammable.",
//                                ((FireBlockAccessor) Blocks.FIRE)::invokeGetBurnChance,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "ignite_odds",
//                                "Ignite Odds",
//                                "The higher the ignite odds, the more likely a block is to catch fire (if it is able to spread there).",
//                                ((FireBlockAccessor) Blocks.FIRE)::invokeGetSpreadChance,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "gets_random_ticked",
//                                "Gets Random Ticked",
//                                "",
//                                AbstractBlock.AbstractBlockState::hasRandomTicks,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "conductivity",
//                                "Conductivity",
//                                "",
//                                (state) -> state.isSolidBlock(new MockBlockView(state), BlockPos.ORIGIN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "suffocates_mobs",
//                                "Suffocates Mobs",
//                                "",
//                                (state) -> state.shouldSuffocate(new MockBlockView(state), BlockPos.ORIGIN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "opacity",
//                                "Opacity",
//                                "",
//                                (state) -> state.getOpacity(new MockBlockView(state), BlockPos.ORIGIN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "is_opaque_full_cube",
//                                "Is Opaque Full Cube",
//                                "",
//                                (state) -> state.isOpaqueFullCube(new MockBlockView(state), BlockPos.ORIGIN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "bottom_face_has_full_square",
//                                "Bottom Face Has Full Square",
//                                "This is true if the bottom face is a full square.",
//                                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.DOWN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "top_face_has_full_square",
//                                "Top Face Has Full Square",
//                                "This is true if the top face is a full square.",
//                                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.UP),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "side_face_has_full_square",
//                                "Side Face Has Full Square (Notrh)",
//                                "This is true if the north face has a full, square surface.",
//                                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.NORTH),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "top_face_has_rim",
//                                "Top Face Has Rim",
//                                "This is true if the top face contains a 2 pixel wide ring going around its edge",
//                                (state) -> Block.hasTopRim(new MockBlockView(state), BlockPos.ORIGIN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "bottom_face_has_small_square",
//                                "Bottom Face Has Small Square",
//                                "This is true if the bottom face contains a square of length 2 at its center.",
//                                (state) -> Block.sideCoversSmallSquare(new MockWorldView(state), BlockPos.ORIGIN, Direction.DOWN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "top_face_has_small_square",
//                                "Top Face Has Small Square",
//                                "This is true if the top face contains a square of length 2 at its center.",
//                                (state) -> Block.sideCoversSmallSquare(new MockWorldView(state), BlockPos.ORIGIN, Direction.UP),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "bottom_face_has_collision",
//                                "Bottom Face Has Collision",
//                                "This is true if the bottom side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
//                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ORIGIN).getFace(Direction.DOWN).isEmpty(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "top_face_has_collision",
//                                "Top Face Has Collision",
//                                "This is true if the top side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
//                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ORIGIN).getFace(Direction.UP).isEmpty(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "side_face_has_collision",
//                                "Side Face Has Collision (North)",
//                                "This is true if the north side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
//                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ORIGIN).getFace(Direction.NORTH).isEmpty(),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "redirects_redstone",
//                                "Redirects Redstone Wire (North)",
//                                "This is true if the North side connects to/redirects adjacent redstone dust",
//                                (state) -> RedstoneWireBlock.connectsTo(state, Direction.SOUTH), // uses an accesswidener, the direction is reversed so the "perspective" makes sense.
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "connects_to_panes",
//                                "Connects To Panes (North)",
//                                "Whether a glass pane block will connect to this block",
//                                (state) -> ((PaneBlock) Blocks.GLASS_PANE).connectsTo(state, state.isSideSolidFullSquare(new MockBlockView(state), BlockPos.ORIGIN, Direction.NORTH)),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "blocks_beacon_beam",
//                                "Blocks Beacon Beam",
//                                "Whether placing this block above a beacon will prevent its beam from forming, or stop its current one.",
//                                // net/minecraft/block/entity/BeaconBlockEntity.java:150
//                                (state) -> !( state.getOpacity(new MockBlockView(state), BlockPos.ORIGIN) < 15 || state.isOf(Blocks.BEDROCK) ),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "obstructs_cactus",
//                                "Obstructs Cactus",
//                                "Whether placing this block next to cactus destroys it.",
//                                (state) -> (
//                                        state.getMaterial().isSolid() ||
//                                        Arrays.<Fluid>asList(Fluids.LAVA, Fluids.LAVA).contains(state.getFluidState().getFluid())
//                                ),
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
//                                (state) -> state.isFullCube(new MockWorldView(state), BlockPos.ORIGIN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "occlusion_shape",
//                                "Occlusion Shape",
//                                "Used in rendering",
//                                (state) -> state.isFullCube(new MockWorldView(state), BlockPos.ORIGIN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "bottom_face_has_solid_full_square",
//                                "Bottom Face Has Solid Full Square",
//                                "This is true if the bottom face is a full square.",
//                                (state) -> state.isSideSolidFullSquare(new MockBlockView(state), BlockPos.ORIGIN, Direction.DOWN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "top_face_has_solid_full_square",
//                                "Top Face Has Solid Full Square",
//                                "This is true if the top face is a full square.",
//                                (state) -> state.isSideSolidFullSquare(new MockBlockView(state), BlockPos.ORIGIN, Direction.UP),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "side_face_has_solid_full_square",
//                                "Side Face Has Solid Full Square (North)",
//                                "This is true if the north face is a full square.",
//                                (state) -> state.isSideSolidFullSquare(new MockBlockView(state), BlockPos.ORIGIN, Direction.NORTH),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "waterloggable",
//                                "Waterloggable",
//                                "Whether this block can be waterlogged.",
//                                (state) -> state.getBlock() instanceof Waterloggable ? true : Arrays.<Fluid>asList(Fluids.WATER, Fluids.FLOWING_WATER).contains(state.getFluidState().getFluid()) ? "Inherent" : false,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "supports_redstone_dust",
//                                "Supports Redstone Dust",
//                                "Whether redstone dust (\"wire\") can be placed on top of this block.",
//                                (state) -> ((RedstoneWireBlockAccessor) Blocks.REDSTONE_WIRE).invokeCanRunOnTop(new MockWorldView(state), BlockPos.ORIGIN, state),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "gets_flushed",
//                                "Gets Flushed",
//                                "Whether this block will get destroyed by flowing water or lava.",
//                                (state) -> ((FlowableFluidAccessor) Fluids.WATER).invokeCanFlow(
//                                        new MockWorldView(state),
//                                        BlockPos.ORIGIN,
//                                        Blocks.WATER.getDefaultState(),
//                                        Direction.NORTH,
//                                        BlockPos.ORIGIN.north(),
//                                        state,
//                                        state.getFluidState(),
//                                        Fluids.WATER
//                                    ) && !(state.getBlock() instanceof Waterloggable),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "emits_redstone_power",
//                                "Emits Redstone Power",
//                                "What it says on the tin.",
//                                AbstractBlock.AbstractBlockState::emitsRedstonePower,
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "kills_grass",
//                                "Kills Grass",
//                                "Whether grass will die when placed underneath.",
//                                (state) -> !SpreadableBlockAccessor.invokeCanSurvive(state, new MockWorldView(state), BlockPos.ORIGIN),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "as_item",
//                                "As Item",
//                                "This block in item form.",
//                                (state) -> !Objects.equals(state.getBlock().asItem().getName().getString(), "Air"),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "ticking_tile_entity",
//                                "Ticking Tile Entity",
//                                "",
//                                state -> state.getBlockEntityTicker(world, Registries.BLOCK_ENTITY_TYPE.get(new Identifier(state.getBlock().toString()))),
//                                blockStates
//                        ).toString(),
//
//                        new JoaProperty<>(
//                                "ticking_block_entity",
//                                "Ticking Block Entity",
//                                "",
//                                state -> {
//                                    if(state.getBlock() instanceof BlockEntityProvider) {
//                                        try {
//                                            return state.getBlock().getClass().getMethod("getTicker", World.class, BlockState.class, BlockEntityType.class).getDeclaringClass() != BlockEntityProvider.class
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
//                        new JoaProperty<>(
//                                "block_entity",
//                                "Block Entity",
//                                "",
//                                AbstractBlock.AbstractBlockState::hasBlockEntity,
//                                blockStates
//                        ).toString(),

//                        new JoaProperty<>(
//                                "height_all",
//                                "Height (All)",
//                                "The block's upwards-facing collision surfaces.</p><p>Includes internal collisions.</p><p>Defaults to pixel values, this can be converted in the settings.</p>",
//                                (state) -> state.getCollisionShape(
//                                        new MockBlockView(state),
//                                        BlockPos.ORIGIN,
//                                        new MockShapeContext(false, true, true, true)
//                                ).getBoundingBoxes().stream().map(box -> box.maxY * 16).distinct().toArray(),
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
//                                                            BlockPos.ORIGIN,
//                                                            new MockShapeContext(true, true, true, true)
//                                                    ).getBoundingBoxes().stream().map(box -> (1 - box.minZ) * 16).distinct()
//                                            )
//                                    );
//                                    obj.add(
//                                            "South",
//                                            jsonArrayFromStream(
//                                                    state.getCollisionShape(
//                                                            new MockBlockView(state),
//                                                            BlockPos.ORIGIN,
//                                                            new MockShapeContext(true, true, true, true)
//                                                    ).getBoundingBoxes().stream().map(box -> box.maxZ * 16).distinct()
//                                            )
//                                    );
//                                    obj.add(
//                                            "East",
//                                            jsonArrayFromStream(
//                                                    state.getCollisionShape(
//                                                            new MockBlockView(state),
//                                                            BlockPos.ORIGIN,
//                                                            new MockShapeContext(true, true, true, true)
//                                                    ).getBoundingBoxes().stream().map(box -> (1 - box.minX) * 16).distinct()
//                                            )
//                                    );
//                                    obj.add(
//                                            "West",
//                                            jsonArrayFromStream(
//                                                    state.getCollisionShape(
//                                                            new MockBlockView(state),
//                                                            BlockPos.ORIGIN,
//                                                            new MockShapeContext(true, true, true, true)
//                                                    ).getBoundingBoxes().stream().map(box -> box.maxX * 16).distinct()
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
//                                        BlockPos.ORIGIN,
//                                        new MockShapeContext(true, true, true, true)
//                                    ).getBoundingBoxes().stream().map(box -> (1 - box.minY) * 16).distinct().toArray(),
//                                blockStates
//                        ).toString(),

//                        // None of these are actually reliable ways to find what updates instantly, but it's a nice starting point
//                        new JoaProperty<>(
//                                "instant_shape_updater",
//                                "Instant Shape Updater",
//                                "",
//                                (state) -> {
//                                    try {
//                                        Class<?> declaringClass = state.getBlock().getClass().getMethod("getStateForNeighborUpdate", BlockState.class, Direction.class, BlockState.class, WorldAccess.class, BlockPos.class, BlockPos.class).getDeclaringClass();
//                                        if(declaringClass == AbstractBlock.class) return false;
//                                        List<Class<?>> instantDeclaringClasses = List.of(AbstractBlock.class, AbstractPressurePlateBlock.class, AmethystClusterBlock.class, AttachedStemBlock.class, BambooSaplingBlock.class, BannerBlock.class, BedBlock.class, BeehiveBlock.class, BellBlock.class, BigDripleafBlock.class, CakeBlock.class, CampfireBlock.class, CandleCakeBlock.class, CarpetBlock.class, ChestBlock.class, CocoaBlock.class, ConcretePowderBlock.class, CoralBlock.class, CoralFanBlock.class, CoralParentBlock.class, CoralWallFanBlock.class, DeadCoralWallFanBlock.class, DoorBlock.class, FenceBlock.class, FenceGateBlock.class, FireBlock.class, FlowerPotBlock.class, FluidBlock.class, FrogspawnBlock.class, HangingRootsBlock.class, HangingSignBlock.class, LadderBlock.class, LanternBlock.class, MultifaceGrowthBlock.class, MushroomBlock.class, NetherPortalBlock.class, NoteBlock.class, PaneBlock.class, PistonHeadBlock.class, PlantBlock.class, PropaguleBlock.class, RedstoneWireBlock.class, RepeaterBlock.class, SeagrassBlock.class, SeaPickleBlock.class, SignBlock.class, SnowBlock.class, SnowyBlock.class, SoulFireBlock.class, SporeBlossomBlock.class, StairsBlock.class, TallPlantBlock.class, TorchBlock.class, TripwireBlock.class, TripwireHookBlock.class, VineBlock.class, WallBannerBlock.class, WallBlock.class, WallHangingSignBlock.class, WallMountedBlock.class, WallRedstoneTorchBlock.class, WallSignBlock.class, WallTorchBlock.class);
//                                        return instantDeclaringClasses.contains(declaringClass);
//
//                                    } catch (NoSuchMethodException e) {
//                                        e.printStackTrace();
//                                        return "No such method";
//                                    }
//                                },
//                                blockStates
//                        ).toString(),
//
//                        // None of these are actually reliable ways to find what updates instantly, but it's a nice starting point
//                        new JoaProperty<>(
//                                "instant_block_updater",
//                                "Instant Block Updater",
//                                "",
//                                (state) -> {
//                                    try {
//                                        Class<?> declaringClass = state.getBlock().getClass().getMethod("neighborUpdate", BlockState.class, World.class, BlockPos.class, Block.class, BlockPos.class, boolean.class).getDeclaringClass();
//                                        if(declaringClass == AbstractBlock.class) return false;
//                                        List<Class<?>> instantDeclaringClasses = List.of(AbstractRailBlock.class, AbstractRedstoneGateBlock.class, BellBlock.class, BigDripleafBlock.class, DoorBlock.class, FenceGateBlock.class, FluidBlock.class, FrostedIceBlock.class, HopperBlock.class, NoteBlock.class, PistonBlock.class, PistonHeadBlock.class, RedstoneWireBlock.class, SpongeBlock.class, StructureBlock.class, TntBlock.class, TrapdoorBlock.class, DispenserBlock.class, RedstoneLampBlock.class);
//                                        return instantDeclaringClasses.contains(declaringClass);
//
//                                    } catch (NoSuchMethodException e) {
//                                        e.printStackTrace();
//                                        return "No such method";
//                                    }
//                                },
//                                blockStates
//                        ).toString(),
//
//                        // None of these are actually reliable ways to find what updates instantly, but it's a nice starting point
//                        new JoaProperty<>(
//                                "instant_updater",
//                                "Instant Updater",
//                                "",
//                                (state) -> {
//                                    try {
//                                        Class<?> blockDeclaringClass = state.getBlock().getClass().getMethod("neighborUpdate", BlockState.class, World.class, BlockPos.class, Block.class, BlockPos.class, boolean.class).getDeclaringClass();
//                                        List<Class<?>> instantBlockUpdateDeclaringClasses = List.of(AbstractRailBlock.class, AbstractRedstoneGateBlock.class, BellBlock.class, BigDripleafBlock.class, DoorBlock.class, FenceGateBlock.class, FluidBlock.class, FrostedIceBlock.class, HopperBlock.class, NoteBlock.class, PistonBlock.class, PistonHeadBlock.class, RedstoneWireBlock.class, SpongeBlock.class, StructureBlock.class, TntBlock.class, TrapdoorBlock.class, DispenserBlock.class, RedstoneLampBlock.class);
//
//                                        Class<?> shapeDeclaringClass = state.getBlock().getClass().getMethod("getStateForNeighborUpdate", BlockState.class, Direction.class, BlockState.class, WorldAccess.class, BlockPos.class, BlockPos.class).getDeclaringClass();
//                                        List<Class<?>> instantShapeUpdateDeclaringClasses = List.of(AbstractPressurePlateBlock.class, AmethystClusterBlock.class, AttachedStemBlock.class, BambooSaplingBlock.class, BannerBlock.class, BedBlock.class, BeehiveBlock.class, BellBlock.class, BigDripleafBlock.class, CakeBlock.class, CampfireBlock.class, CandleCakeBlock.class, CarpetBlock.class, ChestBlock.class, CocoaBlock.class, ConcretePowderBlock.class, CoralBlock.class, CoralFanBlock.class, CoralParentBlock.class, CoralWallFanBlock.class, DeadCoralWallFanBlock.class, DoorBlock.class, FenceBlock.class, FenceGateBlock.class, FireBlock.class, FlowerPotBlock.class, FluidBlock.class, FrogspawnBlock.class, HangingRootsBlock.class, HangingSignBlock.class, LadderBlock.class, LanternBlock.class, MultifaceGrowthBlock.class, MushroomBlock.class, NetherPortalBlock.class, NoteBlock.class, PaneBlock.class, PistonHeadBlock.class, PlantBlock.class, PropaguleBlock.class, RedstoneWireBlock.class, RepeaterBlock.class, SeagrassBlock.class, SeaPickleBlock.class, SignBlock.class, SnowBlock.class, SnowyBlock.class, SoulFireBlock.class, SporeBlossomBlock.class, StairsBlock.class, TallPlantBlock.class, TorchBlock.class, TripwireBlock.class, TripwireHookBlock.class, VineBlock.class, WallBannerBlock.class, WallBlock.class, WallHangingSignBlock.class, WallMountedBlock.class, WallRedstoneTorchBlock.class, WallSignBlock.class, WallTorchBlock.class);
//
//                                        if(blockDeclaringClass == AbstractBlock.class && shapeDeclaringClass == AbstractBlock.class) return false;
//                                        return instantBlockUpdateDeclaringClasses.contains(blockDeclaringClass) || instantShapeUpdateDeclaringClasses.contains(shapeDeclaringClass);
//
//                                    } catch (NoSuchMethodException e) {
//                                        e.printStackTrace();
//                                        return "No such method";
//                                    }
//                                },
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
                            (state) -> state.isIn(tag),
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
