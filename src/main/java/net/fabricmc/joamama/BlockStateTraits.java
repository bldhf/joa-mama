package net.fabricmc.joamama;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.joamama.mixin.*;
import net.fabricmc.joamama.mock.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.piston.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"deprecation"})
public abstract class BlockStateTraits {
    private static final Map<MapColor, String> mapColorMap = new HashMap<>();
    private static final Map<BlockTags, String> blockTags = new HashMap<>();

    public static void load(TraitCollection<SimpleTrait<BlockState, ?>, Iterable<BlockState>> traits) {
        traits.load(BuiltInRegistries.BLOCK.stream().flatMap((block) -> block.getStateDefinition().getPossibleStates().stream()).toList());
//        setupClassNames(Material.class, materialMap);
        setupClassNames(MapColor.class, mapColorMap);
        setupClassNames(BlockTags.class, blockTags, true);

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

    public static boolean isWater(BlockState state) {
        FluidState fluidState = state.getFluidState();
        return fluidState.is(Fluids.FLOWING_WATER) || fluidState.is(Fluids.WATER);
    }

    public static boolean isWaterSource(BlockState state) {
        FluidState fluidState = state.getFluidState();
        return (fluidState.is(Fluids.FLOWING_WATER) || fluidState.is(Fluids.WATER)) && fluidState.getAmount() == 8;
    }

    public static void getTheWholeThing(TraitCollection<SimpleTrait<BlockState, ?>, Iterable<BlockState>> traits) {
        traits.add(new SimpleTrait<>(
            "hardness",
            "Hardness",
            "Determines how fast the block can be mined.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getDestroySpeed",
            (state) -> {
                var hardness = state.getDestroySpeed(new MockBlockGetter(state), BlockPos.ZERO);
                if (hardness == -1) return "∞";
                return hardness;
            }
        ));
        traits.add(new SimpleTrait<>(
            "blast_resistance",
            "Blast Resistance",
            "Determines how likely the block is to break from exposure to an explosion.",
            "net.minecraft.world.level.block.Block#getExplosionResistance",
            (state) -> state.getBlock().getExplosionResistance()
        ));
        traits.add(new SimpleTrait<>(
            "luminance",
            "Luminance",
            "How much light the block emits.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getLightEmission",
            BlockBehaviour.BlockStateBase::getLightEmission
        ));
        traits.add(new SimpleTrait<>(
            "opaque",
            "Opaque",
            "Whether the block is visually opaque.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#canOcclude",
            BlockBehaviour.BlockStateBase::canOcclude
        ));
        traits.add(new SimpleTrait<>(
            "movable",
            "Movable",
            "Whether the block can be pushed by a piston, stops the piston from extending, or whether attempting to push it destroys the block.",
            "",
            // TODO: this does not account for block entities
            state -> switch (state.getPistonPushReaction()) {
                case NORMAL, PUSH_ONLY -> "Yes";
                case BLOCK -> "No";
                case DESTROY -> "Breaks";
                case IGNORE -> null;
            }
        ));
        traits.add(new SimpleTrait<>(
            "sticky",
            "Sticky",
            "Whether the block can be pulled by a sticky piston or an adjacent slime/honey block.\nSlime and honey are listed as 'partially' as they are not sticky when pulled by one another.",
            "",
            // TODO: this does not account for block entities
            state -> switch (state.getPistonPushReaction()) {
                case NORMAL -> "Yes";
                case PUSH_ONLY, BLOCK, DESTROY -> "No";
                case IGNORE -> null;
            }
        ));
        traits.add(new SimpleTrait<>(
            "gets_random_ticked",
            "Gets Random Ticked",
            "Whether the block gets affected by random ticks.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isRandomlyTicking",
            BlockBehaviour.BlockStateBase::isRandomlyTicking
        ));
        traits.add(new SimpleTrait<>(
            "flammable",
            "Flammable",
            "Whether the block can be destroyed by fire.",
            "",
            state -> ((FireBlockAccessor) Blocks.FIRE).invokeGetBurnOdds(state) > 0
        ));
        traits.add(new SimpleTrait<>(
            "burn_odds",
            "Burn Odds",
            "The higher the burn odds, the quicker a block burns away (when on fire). 0 means it is non-flammable.",
            "net.minecraft.world.level.block.FireBlock#getBurnOdds",
            ((FireBlockAccessor) Blocks.FIRE)::invokeGetBurnOdds
        ));
        traits.add(new SimpleTrait<>(
            "ignite_odds",
            "Ignite Odds",
            "The higher the ignite odds, the more likely a block is to catch fire (if it is able to spread there).",
            "net.minecraft.world.level.block.FireBlock#getIgniteOdds(net.minecraft.world.level.block.state.BlockState)",
            ((FireBlockAccessor) Blocks.FIRE)::invokeGetIgniteOdds
        ));
        traits.add(new SimpleTrait<>(
            "conductive",
            "Conductive",
            "Whether or not a redstone component can be powered through this block.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isRedstoneConductor",
            (state) -> state.isRedstoneConductor(new MockBlockGetter(state), BlockPos.ZERO)
        ));
        traits.add(new SimpleTrait<>(
            "suffocates_mobs",
            "Suffocates Mobs",
            "Whether a mob or player should suffocate in this block.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase.isSuffocating",
            (state) -> state.isSuffocating(new MockBlockGetter(state), BlockPos.ZERO)
        ));
        traits.add(new SimpleTrait<>(
            "opacity",
            "Opacity",
            "How much light the block... blocks.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getLightBlock",
            (state) -> state.getLightBlock(new MockBlockGetter(state), BlockPos.ZERO)
        ));
        traits.add(new SimpleTrait<>(
            "is_opaque_full_cube",
            "Is Opaque Full Cube",
            "Whether the block is opaque and renders as a full cube. Note that this is not an AND of Opaque and Full Cube,\nas this uses the rendering shape and Full Cube uses the collision shape.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isSolidRender",
            (state) -> state.isSolidRender(new MockBlockGetter(state), BlockPos.ZERO)
        ));
        traits.add(new SimpleTrait<>(
            "bottom_face_has_full_square",
            "Bottom Face Has Full Square",
            "This is true if the bottom face is a full square.",
            "net.minecraft.world.level.block.Block#isFaceFull",
            (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockGetter(state), BlockPos.ZERO), Direction.DOWN)
        ));
        traits.add(new SimpleTrait<>(
            "top_face_has_full_square",
            "Top Face Has Full Square",
            "This is true if the top face is a full square.",
            "net.minecraft.world.level.block.Block#isFaceFull",
            (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockGetter(state), BlockPos.ZERO), Direction.UP)
        ));
        traits.add(new SimpleTrait<>(
            "side_face_has_full_square",
            "Side Face Has Full Square (North)",
            "This is true if the north face has a full, square surface.",
            "net.minecraft.world.level.block.Block#isFaceFull",
            (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockGetter(state), BlockPos.ZERO), Direction.NORTH)
        ));
        traits.add(new SimpleTrait<>(
            "top_face_has_rim",
            "Top Face Has Rim",
            "This is true if the top face contains a 2 pixel wide ring going around its edge",
            "net.minecraft.world.level.block.Block#canSupportRigidBlock",
            (state) -> Block.canSupportRigidBlock(new MockBlockGetter(state), BlockPos.ZERO)
        ));
        traits.add(new SimpleTrait<>(
            "bottom_face_has_small_square",
            "Bottom Face Has Small Square",
            "This is true if the bottom face contains a square of length 2 at its center.",
            "net.minecraft.world.level.block.Block#canSupportCenter",
            (state) -> Block.canSupportCenter(new MockLevelReader(state), BlockPos.ZERO, Direction.DOWN)
        ));
        traits.add(new SimpleTrait<>(
            "top_face_has_small_square",
            "Top Face Has Small Square",
            "This is true if the top face contains a square of length 2 at its center.",
            "net.minecraft.world.level.block.Block#canSupportCenter",
            (state) -> Block.canSupportCenter(new MockLevelReader(state), BlockPos.ZERO, Direction.UP)
        ));
        traits.add(new SimpleTrait<>(
            "bottom_face_has_collision",
            "Bottom Face Has Collision",
            "This is true if the bottom side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getCollisionShape(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos)",
            (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).getFaceShape(Direction.DOWN).isEmpty()
        ));
        traits.add(new SimpleTrait<>(
            "top_face_has_collision",
            "Top Face Has Collision",
            "This is true if the top side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getCollisionShape(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos)",
            (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).getFaceShape(Direction.UP).isEmpty()
        ));
        traits.add(new SimpleTrait<>(
            "side_face_has_collision",
            "Side Face Has Collision (North)",
            "This is true if the north side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getCollisionShape(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos)",
            (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).getFaceShape(Direction.NORTH).isEmpty()
        ));
        traits.add(new SimpleTrait<>(
            "has_collision",
            "Has Collision",
            "Whether the block has any solid collision box.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getCollisionShape(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos)",
            (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).isEmpty()
        ));
        traits.add(new SimpleTrait<>(
            "redirects_redstone",
            "Redirects Redstone Wire (North)",
            "This is true if the North side connects to/redirects adjacent redstone dust.",
            "",
            (state) -> RedStoneWireBlockAccessor.invokeShouldConnectTo(state, Direction.SOUTH) // the direction is reversed so the "perspective" makes sense.
        ));
        traits.add(new SimpleTrait<>(
            "connects_to_panes",
            "Connects To Panes (North)",
            "Whether a glass pane block to the north will connect to this block.",
            "",
            (state) -> ((IronBarsBlock) Blocks.GLASS_PANE).attachsTo(state, state.isFaceSturdy(new MockBlockGetter(state), BlockPos.ZERO, Direction.NORTH))
        ));
        traits.add(new SimpleTrait<>(
            "blocks_beacon_beam",
            "Blocks Beacon Beam",
            "Whether placing this block above a beacon will prevent its beam from forming, or stop its current one.",
            "",
            // net/minecraft/block/entity/BeaconBlockEntity.java:150
            (state) -> !( state.getLightBlock(new MockBlockGetter(state), BlockPos.ZERO) < 15 || state.is(Blocks.BEDROCK) )
        ));
        traits.add(new SimpleTrait<>(
            "full_cube",
            "Full Cube",
            "Whether the block has a normal cube shape and has full block collision on all sides.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isCollisionShapeFullBlock",
            (state) -> state.isCollisionShapeFullBlock(new MockLevelReader(state), BlockPos.ZERO)
        ));
        traits.add(new SimpleTrait<>(
            "bottom_face_has_full_square",
            "Bottom Face Has Full Square",
            "This is true if the bottom face is a full square.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isFaceSturdy(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos, net.minecraft.core.Direction)",
            (state) -> state.isFaceSturdy(new MockBlockGetter(state), BlockPos.ZERO, Direction.DOWN)
        ));
        traits.add(new SimpleTrait<>(
            "top_face_has_full_square",
            "Top Face Has Full Square",
            "This is true if the top face is a full square.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isFaceSturdy(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos, net.minecraft.core.Direction)",
            (state) -> state.isFaceSturdy(new MockBlockGetter(state), BlockPos.ZERO, Direction.UP)
        ));
        traits.add(new SimpleTrait<>(
            "side_face_has_full_square",
            "Side Face Has Full Square (North)",
            "This is true if the north face is a full square.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isFaceSturdy(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos, net.minecraft.core.Direction)",
            (state) -> state.isFaceSturdy(new MockBlockGetter(state), BlockPos.ZERO, Direction.NORTH)
        ));
        traits.add(new SimpleTrait<>(
            "waterloggable",
            "Waterloggable",
            "Whether this block can be waterlogged.",
            "",
            (state) -> (
                state.getBlock() instanceof SimpleWaterloggedBlock ? true :
                isWater(state) ? "Inherent" : false
            )
        ));
        traits.add(new SimpleTrait<>(
            "supports_redstone_dust",
            "Supports Redstone Dust",
            "Whether redstone dust (\"wire\") can be placed on top of this block.",
            "net.minecraft.world.level.block.RedStoneWireBlock#canSurviveOn",
            (state) -> ((RedStoneWireBlockAccessor) (Object) Blocks.REDSTONE_WIRE).invokeCanSurviveOn(new MockLevelReader(state), BlockPos.ZERO, state)
        ));
        traits.add(new SimpleTrait<>(
            "gets_flushed",
            "Gets Flushed",
            "Whether this block will get destroyed by flowing water or lava.",
            "",
            (state) -> ((FlowableFluidAccessor) Fluids.WATER).invokeCanSpreadTo(
                    new MockLevelReader(state),
                    BlockPos.ZERO,
                    Blocks.WATER.defaultBlockState(),
                    Direction.NORTH,
                    BlockPos.ZERO.north(),
                    state,
                    state.getFluidState(),
                    Fluids.WATER
                ) && !(state.getBlock() instanceof SimpleWaterloggedBlock)
        ));
        traits.add(new SimpleTrait<>(
            "emits_power",
            "Emits Power",
            "Whether this block can emit redstone signals.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isSignalSource",
            BlockBehaviour.BlockStateBase::isSignalSource
        ));
        traits.add(new SimpleTrait<>(
            "kills_grass",
            "Kills Grass",
            "Whether grass will die when placed underneath this block.",
            "net.minecraft.world.level.block.SpreadingSnowyDirtBlock#canBeGrass",
            (state) -> !SpreadableBlockAccessor.invokeCanBeGrass(state, new MockLevelReader(state), BlockPos.ZERO)
        ));
        traits.add(new SimpleTrait<>(
            "exists_as_item",
            "Exists As Item",
            "Whether this block has a direct item equivalent.",
            "net.minecraft.world.level.block.Block#asItem",
            (state) -> !Objects.equals(state.getBlock().asItem().getDescription().getString(), "Air")
        ));
        traits.add(new SimpleTrait<>(
            "block_entity",
            "Block Entity",
            "Whether this block has an associated block entity, and whether it's ticking or non-ticking.",
            "",
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
            }
        ));
        traits.add(new SimpleTrait<>(
            "height_all",
            "Height (All)",
            "The block's upwards-facing collision surfaces.\nIncludes internal collisions.\nDefaults to pixel values, this can be converted in the settings.",
            "",
            (state) -> state.getCollisionShape(
                    new MockBlockGetter(state),
                    BlockPos.ZERO,
                    new MockCollisionContext(false, true, true, true)
            ).toAabbs().stream().map(box -> box.maxY * 16).distinct().toArray()
        ));
        traits.add(new SimpleTrait<>(
            "width_all",
            "Width (All)",
            "The block's sideways-facing collision surfaces.\nIncludes internal collisions.\nDefaults to pixel values, this can be converted in the settings.",
            "",
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
            }
        ));
        traits.add(new SimpleTrait<>(
            "collision_bottom_all",
            "Collision Bottom (All)",
            "The block's upwards-facing collision surfaces.\nIncludes internal collisions.\nDefaults to pixel values, this can be converted in the settings.",
            "",
            (state) -> state.getCollisionShape(
                    new MockBlockGetter(state),
                    BlockPos.ZERO,
                    new MockCollisionContext(true, true, true, true)
                ).toAabbs().stream().map(box -> (1 - box.minY) * 16).distinct().toArray()
        ));
        traits.add(new SimpleTrait<>(
            "block_render_type",
            "Block Render Type",
            "TODO",
            "",
            (state) -> ItemBlockRenderTypes.getChunkRenderType(state).toString()
        ));
        traits.add(new SimpleTrait<>(
            "fluid_render_type",
            "Fluid Render Type",
            "TODO",
            "",
            (state) -> ItemBlockRenderTypes.getRenderLayer(state.getFluidState()).toString()
        ));
        traits.add(new SimpleTrait<>(
            "blocks_skylight",
            "Blocks Skylight",
            "Whether this block... blocks... skylight.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#propagatesSkylightDown",
            (state) -> !state.propagatesSkylightDown(new MockBlockGetter(state), BlockPos.ZERO)
        ));
        traits.add(new SimpleTrait<>(
            "instrument",
            "Instrument",
            "Which instrument a note block will play if placed above this block. This does not include mob heads.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#instrument",
            (state) -> state.instrument().toString()
        ));
        traits.add(new SimpleTrait<>(
            "solid",
            "(Legacy) Solid",
            "Not fully accurate to its name; remains from when materials were still used.\nMarked as deprecated in the code but still used extensively.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isSolid",
            BlockBehaviour.BlockStateBase::isSolid
        ));
        traits.add(new SimpleTrait<>(
            "blocks_motion",
            "(Legacy) Blocks Motion",
            "Not fully accurate to its name; remains from when materials were still used.\nMarked as deprecated in the code but still used extensively.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#blocksMotion",
            BlockBehaviour.BlockStateBase::blocksMotion
        ));
        traits.add(new SimpleTrait<>(
            "liquid",
            "(Legacy) Liquid",
            "Not fully accurate to its name; remains from when materials were still used.\nMarked as deprecated in the code but still used extensively.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#liquid",
            BlockBehaviour.BlockStateBase::liquid
        ));
        traits.add(new SimpleTrait<>(
            "ignited_by_lava",
            "Ignited By Lava",
            "Whether lava can set this block on fire.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#ignitedByLava",
            BlockBehaviour.BlockStateBase::ignitedByLava
        ));
        traits.add(new SimpleTrait<>(
            "replaceable",
            "Replaceable",
            "Determines whether a block placed or falling on this block will replace it rather than being placed against or on it.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#canBeReplaced()",
            BlockBehaviour.BlockStateBase::canBeReplaced
        ));
        traits.add(new SimpleTrait<>(
            "map_color",
            "Map Color",
            "The color of this block when viewed on a map. NONE is transparent, meaning it is simply ignored. Note that waterlogged blocks will have the map color of water.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getMapColor",
            (state) -> "{{mapColor|" + mapColorMap.get(state.getMapColor(new MockBlockGetter(state), BlockPos.ZERO)) + "}}"
        ));
        traits.add(new SimpleTrait<>(
            "falling_block",
            "Falling Block",
            "Whether the block can fall when unsupported (as a falling_block entity).",
            "",
            (state) -> state.getBlock() instanceof Fallable || state.is(Blocks.SCAFFOLDING)
        ));
        traits.add(new SimpleTrait<>(
            "water_forms_source_above",
            "Water Forms Sources Above",
            "Whether water can form a source above this block.\nIf infinite lava is on, it will form sources above the same blocks, but above lava and not above water.",
            "",
            (state) -> state.isSolid() || isWaterSource(state)
        ));
        traits.add(new SimpleTrait<>(
            "spawnable",
            "Spawnable On",
            "Whether mobs can spawn on this block.",
            "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isValidSpawn",
            (state) -> (
                state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.PLAYER) ? "Yes" :
                state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.OCELOT) ? "Ocelots and Parrots Only" :
                state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.POLAR_BEAR) ? "Polar Bear Only" :
                state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.BLAZE) ? "Fire-Immune Mobs Only" : "No"
                //                                                                       O
                //                                                                       B
            )
        ));
        traits.add(new SimpleTrait<>(
            "spawnable_in",
            "Spawnable In",
            "Whether mobs can spawn in this block.",
            "",
            (state) -> ""
        ));
    }

    public static void getInstantUpdaterStuff(TraitCollection<SimpleTrait<BlockState, ?>, Iterable<BlockState>> traits) {
        // None of these are actually reliable ways to find what updates instantly, but it's a nice starting point
        traits.add(new SimpleTrait<>(
            "instant_shape_updater",
            "Instant Shape Updater",
            "",
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
            }
        ));
        traits.add(new SimpleTrait<>(
            "instant_block_updater",
            "Instant Block Updater",
            "",
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
            }
        ));
        traits.add(new SimpleTrait<>(
            "instant_updater",
            "Instant Updater",
            "",
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
            }
        ));
    }

    public static <T> void addBlockTagProperties(TraitCollection<SimpleTrait<BlockState, ?>, Iterable<BlockState>> traits, Class<T> clazz) {
        /*for (Field staticField : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(staticField.getModifiers())) {
                try {
                    @SuppressWarnings("unchecked")
                    TagKey<Block> tag = (TagKey<Block>) staticField.get(null);
                    traits.add(new SimpleTrait<>(
                            "tag_" + staticField.getName().toLowerCase(),
                            "Tag: " + staticField.getName(),
                            "" + staticField.getName(),
                            (state) -> state.is(tag),
                            blockStates
                    ));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }*/
        traits.add(new SimpleTrait<>(
            "dragon_immune",
            "Dragon Immune",
            "Whether this block is immune to The Ender Dragon flying through it.",
            "",
            (state) -> state.is(BlockTags.DRAGON_IMMUNE) || state.is(BlockTags.DRAGON_TRANSPARENT)
        ));
        traits.add(new SimpleTrait<>(
            "wither_block_break_immune",
            "Wither Block Break Immune",
            "Whether this block is immune to the wither's block breaking attack.",
            "",
            (state) -> state.is(BlockTags.WITHER_IMMUNE) || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.BUBBLE_COLUMN
        ));
        traits.add(new SimpleTrait<>(
            "wither_skull_immune",
            "Wither Skull Immune",
            "Whether this block is immune to the wither's skull attack.",
            "",
            (state) -> {
                float expRes = Math.max(state.getBlock().getExplosionResistance(), state.getFluidState().getExplosionResistance());
                // net.minecraft.world.entity.projectile.WitherSkull.getBlockExplosionResistance
                if (1.3 < 0.3*(0.3+expRes)) {
                    if (WitherBoss.canDestroy(state)) {
                        return "Only to black skulls";
                    }
                    return "Yes";
                }
                return "No";
            }
        ));
        StructureTemplateManager manager = Minecraft.getInstance().getSingleplayerServer().getStructureManager();
        Set<Block> blocksInStructures = manager.listTemplates()
                .map(rl -> manager.get(rl).orElseThrow())
                .flatMap(template -> {
                    try {
                        Field palettesField = template.getClass().getDeclaredField("palettes");
                        palettesField.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        var palettes = (List<StructureTemplate.Palette>) palettesField.get(template);
                        return palettes.stream()
                                .map(StructureTemplate.Palette::blocks)
                                .flatMap(blockInfos -> blockInfos
                                        .stream()
                                        .map(StructureTemplate.StructureBlockInfo::state)
                                        .map(BlockState::getBlock)
                                );
                    } catch (NoSuchFieldException e) {
                        System.out.println("Generates in structures: Fuck 1");
                    } catch (IllegalAccessException e) {
                        System.out.println("Generates in structures: Fuck 2");
                    }
                    return Stream.of();
                })
                .collect(Collectors.toUnmodifiableSet());
        traits.add(new SimpleTrait<>(
            "generates_in_structures",
            "Generates in Structures",
            "Based off of what blocks show up in the standard structure palletes. Does not include all complex structures.",
            "",
            state -> blocksInStructures.contains(state.getBlock())
        ));
        traits.add(new SimpleTrait<>(
            "obstructs_cactus",
            "Obstructs Cactus",
            "Whether placing this block next to cactus destroys it.",
            "",
            state -> state.isSolid() || state.getFluidState().is(FluidTags.LAVA)
        ));
        traits.add(new SimpleTrait<>(
            "obstructs_tree_growth",
            "Obstructs Tree Growth",
            "",
            "",
            (state) -> !state.isAir() && !state.is(BlockTags.REPLACEABLE_BY_TREES)
        ));
        traits.add(new SimpleTrait<>(
            "connects_to_walls",
            "Connects To Walls (North)",
            "Whether a wall block to the north will connect to this block.",
            "",
            (state) -> ((WallBlockAccessor) Blocks.ANDESITE_WALL).invokeConnectsTo(state, state.isFaceSturdy(new MockLevelReader(state), BlockPos.ZERO, Direction.NORTH), Direction.NORTH)
        ));
        traits.add(new SimpleTrait<>(
            "raid_spawnable",
            "Raid Spawnable",
            "Whether raids can spawn on this block.",
            "",
            (state) ->
                    SpawnPlacementTypes.ON_GROUND.isSpawnPositionOk(
                            new MockMultiBlockLevelReader(Map.of(
                                    BlockPos.ZERO.above(),  Blocks.AIR.defaultBlockState(),
                                    BlockPos.ZERO,          Blocks.AIR.defaultBlockState(),
                                    BlockPos.ZERO.below(),  state
                            )),
                            BlockPos.ZERO,
                            EntityType.RAVAGER
                    ) || state.is(Blocks.SNOW)
        ));
    }

    public static JsonArray jsonArrayFromStream(Stream<Double> arr) {
        JsonArray jsonArray = new JsonArray();
        arr.forEach(jsonArray::add);
        return jsonArray;
    }
}
