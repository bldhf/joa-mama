package net.fabricmc.joamama;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.joamama.mixin.*;
import net.fabricmc.joamama.mock.*;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.piston.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"deprecation"})
public abstract class BlockStateTraits {
    private static final Map<MapColor, String> mapColorMap = new HashMap<>();
    private static final Map<BlockTags, String> blockTags = new HashMap<>();
    private static ServerLevel level;
    private static BlockStateModelSet blockStateModelSet;
    private static FluidStateModelSet fluidStateModelSet;

    public static void load(TraitCollection<BlockStateTrait<?>, SetMultimap<Block, BlockState>> traits, ServerLevel level, Minecraft client) {
        SetMultimap<Block, BlockState> multimap = MultimapBuilder.hashKeys().hashSetValues().build();
        for (Block block : BuiltInRegistries.BLOCK) {
            multimap.putAll(block, block.getStateDefinition().getPossibleStates());
        }
        traits.load(multimap);
        BlockStateTraits.level = level;
        setupClassNames(MapColor.class, mapColorMap);
        setupClassNames(BlockTags.class, blockTags, true);

        blockStateModelSet = client.getModelManager().getBlockStateModelSet();
        fluidStateModelSet = client.getModelManager().getFluidStateModelSet();
    }

    public static <T> void setupClassNames(Class<T> clazz, Map<T, String> map) {
        setupClassNames(clazz, map, false);
    }

    public static <T> void setupClassNames(Class<T> clazz, Map<T, String> map, Boolean ignoreClassType) {
        for(Field staticField : clazz.getDeclaredFields()) {
            if((staticField.getType() == clazz || ignoreClassType) &&
                    Modifier.isStatic(staticField.getModifiers())) {
                try {
                    @SuppressWarnings("unchecked")
                    T value = (T) staticField.get(null);
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

    public static void getTheWholeThing(TraitCollection<BlockStateTrait<?>, SetMultimap<Block, BlockState>> traits) {
        traits.add(new BlockStateTrait<>(
                "block_id",
                "Block ID",
                "",
                "",
                (state) -> state.toString().replaceAll("\\[.*", "")
        ));
        traits.add(new BlockStateTrait<>(
                "blockstate",
                "Blockstate",
                "",
                "",
                BlockState::toString
        ));
        traits.add(new BlockStateTrait<>(
                "translated_name",
                "Translated Name",
                "The translated name of the block, if it exists, i think.",
                "",
                (state) -> {
                    Language language = Language.getInstance();
                    var contents = (TranslatableContents) state.getBlock().getName().getContents();
                    // imitates net.minecraft.network.chat.contents.TranslatableContents.decompose
                    return contents.getFallback() != null ? language.getOrDefault(contents.getKey(), contents.getFallback()) : language.getOrDefault(contents.getKey());
                }
        ));
        traits.add(new BlockStateTrait<>(
                "translation_key",
                "Translation Key",
                "The untranslated key used to fetch the name of the block from language mappings.",
                "",
                (state) -> state.getBlock().getName().toString()
        ));
        traits.add(new BlockStateTrait<>(
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
        traits.add(new BlockStateTrait<>(
                "blast_resistance",
                "Blast Resistance",
                "Determines how likely the block is to break from exposure to an explosion.",
                "net.minecraft.world.level.block.Block#getExplosionResistance",
                (state) -> Math.max(state.getBlock().getExplosionResistance(), state.getFluidState().getExplosionResistance())
        ));
        traits.add(new BlockStateTrait<>(
                "requires_correct_tool",
                "Requires Correct Tool For Drops",
                "Whether using the required tool is needed for this block to drop as an item.",
                "",
                BlockBehaviour.BlockStateBase::requiresCorrectToolForDrops
        ));
        traits.add(new BlockStateTrait<>(
                "luminance",
                "Luminance",
                "How much light the block emits.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getLightEmission",
                BlockBehaviour.BlockStateBase::getLightEmission
        ));
        traits.add(new BlockStateTrait<>(
                "opaque",
                "Opaque",
                "Whether the block is visually opaque.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#canOcclude",
                BlockBehaviour.BlockStateBase::canOcclude
        ));
        traits.add(new BlockStateTrait<>(
                "movable",
                "Movable",
                "Whether the block can be pushed by a piston, stops the piston from extending, or whether attempting to push it destroys the block.",
                "",
                // TODO: bedrock, crying obsidian, end portal frame, light block, ++ are wrong
                state -> {
                    if (state.getBlock() instanceof EntityBlock) {
                        return switch (state.getPistonPushReaction()) {
                            case NORMAL, PUSH_ONLY, BLOCK -> "No";
                            case DESTROY -> "Breaks";
                            case IGNORE -> null;
                        };
                    } else {
                        return switch (state.getPistonPushReaction()) {
                            case NORMAL, PUSH_ONLY -> "Yes";
                            case BLOCK -> "No";
                            case DESTROY -> "Breaks";
                            case IGNORE -> null;
                        };
                    }
                }
        ));
        traits.add(new BlockStateTrait<>(
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
        traits.add(new BlockStateTrait<>(
                "gets_random_ticked",
                "Gets Random Ticked",
                "Whether the block gets affected by random ticks.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isRandomlyTicking",
                BlockBehaviour.BlockStateBase::isRandomlyTicking
        ));
        traits.add(new BlockStateTrait<>(
                "flammable",
                "Flammable",
                "Whether the block can be destroyed by fire.",
                "",
                state -> ((FireBlockAccessor) Blocks.FIRE).invokeGetBurnOdds(state) > 0
        ));
        traits.add(new BlockStateTrait<>(
                "burn_odds",
                "Burn Odds",
                "The higher the burn odds, the quicker a block burns away (when on fire). 0 means it is non-flammable.",
                "net.minecraft.world.level.block.FireBlock#getBurnOdds",
                ((FireBlockAccessor) Blocks.FIRE)::invokeGetBurnOdds
        ));
        traits.add(new BlockStateTrait<>(
                "ignite_odds",
                "Ignite Odds",
                "The higher the ignite odds, the more likely a block is to catch fire (if it is able to spread there).",
                "net.minecraft.world.level.block.FireBlock#getIgniteOdds(net.minecraft.world.level.block.state.BlockState)",
                ((FireBlockAccessor) Blocks.FIRE)::invokeGetIgniteOdds
        ));
        traits.add(new BlockStateTrait<>(
                "conductive",
                "Conductive",
                "Whether or not a redstone component can be powered through this block.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isRedstoneConductor",
                (state) -> state.isRedstoneConductor(new MockBlockGetter(state), BlockPos.ZERO)
        ));
        traits.add(new BlockStateTrait<>(
                "suffocates_mobs",
                "Suffocates Mobs",
                "Whether a mob or player should suffocate in this block.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase.isSuffocating",
                (state) -> state.isSuffocating(new MockBlockGetter(state), BlockPos.ZERO)
        ));
        traits.add(new BlockStateTrait<>(
                "opacity",
                "Opacity",
                "How much light the block dampens",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getLightDampening",
                BlockBehaviour.BlockStateBase::getLightDampening
        ));
        traits.add(new BlockStateTrait<>(
                "is_opaque_full_cube",
                "Is Opaque Full Cube",
                "Whether the block is opaque and renders as a full cube. Note that this is not an AND of Opaque and Full Cube,\nas this uses the rendering shape and Full Cube uses the collision shape.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isSolidRender",
                BlockBehaviour.BlockStateBase::isSolidRender
        ));
        traits.add(new BlockStateTrait<>(
                "bottom_face_has_full_square",
                "Bottom Face Has Full Square",
                "This is true if the bottom face is a full square.",
                "net.minecraft.world.level.block.Block#isFaceFull",
                (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockGetter(state), BlockPos.ZERO), Direction.DOWN)
        ));
        traits.add(new BlockStateTrait<>(
                "top_face_has_full_square",
                "Top Face Has Full Square",
                "This is true if the top face is a full square.",
                "net.minecraft.world.level.block.Block#isFaceFull",
                (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockGetter(state), BlockPos.ZERO), Direction.UP)
        ));
        traits.add(new BlockStateTrait<>(
                "side_face_has_full_square",
                "Side Face Has Full Square (North)",
                "This is true if the north face has a full, square surface.",
                "net.minecraft.world.level.block.Block#isFaceFull",
                (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockGetter(state), BlockPos.ZERO), Direction.NORTH)
        ));
        traits.add(new BlockStateTrait<>(
                "top_face_has_rim",
                "Top Face Has Rim",
                "This is true if the top face contains a 2 pixel wide ring going around its edge",
                "net.minecraft.world.level.block.Block#canSupportRigidBlock",
                (state) -> Block.canSupportRigidBlock(new MockBlockGetter(state), BlockPos.ZERO)
        ));
        traits.add(new BlockStateTrait<>(
                "bottom_face_has_small_square",
                "Bottom Face Has Small Square",
                "This is true if the bottom face contains a square of length 2 at its center.",
                "net.minecraft.world.level.block.Block#canSupportCenter",
                (state) -> Block.canSupportCenter(new MockLevelReader(state), BlockPos.ZERO, Direction.DOWN)
        ));
        traits.add(new BlockStateTrait<>(
                "top_face_has_small_square",
                "Top Face Has Small Square",
                "This is true if the top face contains a square of length 2 at its center.",
                "net.minecraft.world.level.block.Block#canSupportCenter",
                (state) -> Block.canSupportCenter(new MockLevelReader(state), BlockPos.ZERO, Direction.UP)
        ));
        traits.add(new BlockStateTrait<>(
                "bottom_face_has_collision",
                "Bottom Face Has Collision",
                "This is true if the bottom side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getCollisionShape(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos)",
                (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).getFaceShape(Direction.DOWN).isEmpty()
        ));
        traits.add(new BlockStateTrait<>(
                "top_face_has_collision",
                "Top Face Has Collision",
                "This is true if the top side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getCollisionShape(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos)",
                (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).getFaceShape(Direction.UP).isEmpty()
        ));
        traits.add(new BlockStateTrait<>(
                "side_face_has_collision",
                "Side Face Has Collision (North)",
                "This is true if the north side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getCollisionShape(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos)",
                (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).getFaceShape(Direction.NORTH).isEmpty()
        ));
        traits.add(new BlockStateTrait<>(
                "has_collision",
                "Has Collision",
                "Whether the block has any solid collision box.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getCollisionShape(net.minecraft.world.level.BlockGetter, net.minecraft.core.BlockPos)",
                (state) -> !state.getCollisionShape(new MockLevelReader(state), BlockPos.ZERO).isEmpty()
        ));
        traits.add(new BlockStateTrait<>(
                "redirects_redstone",
                "Redirects Redstone Wire (North)",
                "This is true if the North side connects to/redirects adjacent redstone dust.",
                "",
                (state) -> RedStoneWireBlockAccessor.invokeShouldConnectTo(state, Direction.SOUTH)
        ));
        traits.add(new BlockStateTrait<>(
                "connects_to_panes",
                "Connects To Panes (North)",
                "Whether a glass pane block to the north will connect to this block.",
                "",
                (state) -> ((StainedGlassPaneBlock) Blocks.ORANGE_STAINED_GLASS_PANE).attachsTo(state, state.isFaceSturdy(new MockBlockGetter(state), BlockPos.ZERO, Direction.NORTH))
        ));
        traits.add(new BlockStateTrait<>(
                "blocks_beacon_beam",
                "Blocks Beacon Beam",
                "Whether placing this block above a beacon will prevent its beam from forming, or stop its current one.",
                "",
                // net/minecraft/block/entity/BeaconBlockEntity.java:150
                (state) -> !(state.getLightDampening() < 15 || state.is(Blocks.BEDROCK))
        ));
        traits.add(new BlockStateTrait<>(
                "full_cube",
                "Full Cube",
                "Whether the block has a normal cube shape and has full block collision on all sides.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isCollisionShapeFullBlock",
                (state) -> state.isCollisionShapeFullBlock(new MockLevelReader(state), BlockPos.ZERO)
        ));
        traits.add(new BlockStateTrait<>(
                "waterloggable",
                "Waterloggable",
                "Whether this block can be waterlogged.",
                "",
                (state) -> (state.getBlock() instanceof SimpleWaterloggedBlock) ? true : (isWater(state) ? "Inherent" : false)
        ));
        traits.add(new BlockStateTrait<>(
                "supports_redstone_dust",
                "Supports Redstone Dust",
                "Whether redstone dust (\"wire\") can be placed on top of this block.",
                "net.minecraft.world.level.block.RedStoneWireBlock#canSurviveOn",
                (state) -> ((RedStoneWireBlockAccessor) Blocks.REDSTONE_WIRE).invokeCanSurviveOn(new MockLevelReader(state), BlockPos.ZERO, state)
        ));
        traits.add(new BlockStateTrait<>(
                "gets_flushed1",
                "Gets Flushed1",
                "Whether this block will get destroyed by flowing water or lava.",
                "",
                (state) -> ((FlowableFluidAccessor) Fluids.WATER).invokeCanMaybePassThrough(
                        new MockLevelReader(state),
                        BlockPos.ZERO,
                        Blocks.WATER.defaultBlockState(),
                        Direction.NORTH,
                        BlockPos.ZERO.north(),
                        state,
                        state.getFluidState()
                ) && !(state.getBlock() instanceof SimpleWaterloggedBlock)
        ));
        traits.add(new BlockStateTrait<>(
                "gets_flushed2",
                "Gets Flushed2",
                "Whether this block will get destroyed by flowing water or lava.",
                "",
                (state) -> FlowableFluidAccessor.invokeCanHoldSpecificFluid(
                        new MockLevelReader(state),
                        BlockPos.ZERO,
                        Blocks.WATER.defaultBlockState(),
                        Fluids.WATER
                ) && !(state.getBlock() instanceof SimpleWaterloggedBlock)
        ));
        traits.add(new BlockStateTrait<>(
                "gets_flushed3",
                "Gets Flushed3",
                "Whether this block will get destroyed by flowing water or lava.",
                "",
                (state) -> Fluids.WATER.defaultFluidState().canBeReplacedWith(
                        new MockLevelReader(state),
                        BlockPos.ZERO,
                        Fluids.WATER,
                        Direction.NORTH
                ) && !(state.getBlock() instanceof SimpleWaterloggedBlock)
        ));
        traits.add(new BlockStateTrait<>(
                "emits_power",
                "Emits Power",
                "Whether this block can emit redstone signals.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isSignalSource",
                BlockBehaviour.BlockStateBase::isSignalSource
        ));
        traits.add(new BlockStateTrait<>(
                "kills_grass",
                "Kills Grass",
                "Whether grass will die when placed underneath this block.",
                "net.minecraft.world.level.block.SpreadingSnowyDirtBlock#canBeGrass",
                (state) -> !SpreadableBlockAccessor.invokeCanStayAlive(state, new MockLevelReader(state), BlockPos.ZERO)
        ));
        traits.add(new BlockStateTrait<>(
                "exists_as_item",
                "Exists As Item",
                "Whether this block has a direct item equivalent.",
                "net.minecraft.world.level.block.Block#asItem",
                (state) -> !Objects.equals(state.getBlock().asItem().getDescriptionId(), "Air")
        ));
        traits.add(new BlockStateTrait<>(
                "block_entity",
                "Block Entity",
                "Whether this block has an associated block entity, and whether it's ticking or non-ticking.",
                "",
                state -> {
                    if (state.getBlock() instanceof EntityBlock) {
                        try {
                            return state.getBlock().getClass().getMethod("getTicker", Level.class, BlockState.class, BlockEntityType.class).getDeclaringClass() != EntityBlock.class
                                    ? "Ticking"
                                    : "Non-Ticking";
                        } catch (NoSuchMethodException e) {
                            return "Goofy one <- this made me laugh, thanks Joa";
                        }
                    }
                    return "No";
                }
        ));
        traits.add(new BlockStateTrait<>(
                "height_all",
                "Height (All)",
                "The block's upwards-facing collision surfaces.\nIncludes internal collisions.\nDefaults to pixel values, this can be converted in the settings.",
                "",
                (state) -> state.getCollisionShape(
                        new MockBlockGetter(state),
                        BlockPos.ZERO,
                        new MockCollisionContext(true, true, true, true, true)
                ).toAabbs().stream().map(box -> box.maxY * 16).distinct().toArray()
        ));
        traits.add(new BlockStateTrait<>(
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
                                            new MockCollisionContext(true, true, true, true, true)
                                    ).toAabbs().stream().map(box -> (1 - box.minZ) * 16).distinct()
                            )
                    );
                    obj.add(
                            "South",
                            jsonArrayFromStream(
                                    state.getCollisionShape(
                                            new MockBlockGetter(state),
                                            BlockPos.ZERO,
                                            new MockCollisionContext(true, true, true, true, true)
                                    ).toAabbs().stream().map(box -> box.maxZ * 16).distinct()
                            )
                    );
                    obj.add(
                            "East",
                            jsonArrayFromStream(
                                    state.getCollisionShape(
                                            new MockBlockGetter(state),
                                            BlockPos.ZERO,
                                            new MockCollisionContext(true, true, true, true, true)
                                    ).toAabbs().stream().map(box -> (1 - box.minX) * 16).distinct()
                            )
                    );
                    obj.add(
                            "West",
                            jsonArrayFromStream(
                                    state.getCollisionShape(
                                            new MockBlockGetter(state),
                                            BlockPos.ZERO,
                                            new MockCollisionContext(true, true, true, true, true)
                                    ).toAabbs().stream().map(box -> box.maxX * 16).distinct()
                            )
                    );
                    return obj;
                }
        ));
        traits.add(new BlockStateTrait<>(
                "collision_bottom_all",
                "Collision Bottom (All)",
                "The block's downwards-facing collision surfaces.\nIncludes internal collisions.\nDefaults to pixel values, this can be converted in the settings.",
                "",
                (state) -> state.getCollisionShape(
                        new MockBlockGetter(state),
                        BlockPos.ZERO,
                        new MockCollisionContext(true, true, true, true, true)
                ).toAabbs().stream().map(box -> (1 - box.minY) * 16).distinct().toArray()
        ));
        traits.add(new BlockStateTrait<>(
                "block_render_type",
                "Block Render Type",
                "Which block render type the block uses.",
                "net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo.layer",
                (state) -> {
                    final List<BlockStateModelPart> parts = new ArrayList<>();
                    blockStateModelSet.get(state).collectParts(RandomSource.create(), parts);
                    var types = parts.stream().flatMap(
                            part -> Stream.of(
                                    // `null` represents unculled textures. but for some reason, not every block has
                                    // textures when using unculled textures, so we also fetch all the other directions,
                                    // just to be safe.
                                    part.getQuads(null),
                                    part.getQuads(Direction.UP),
                                    part.getQuads(Direction.DOWN),
                                    part.getQuads(Direction.NORTH),
                                    part.getQuads(Direction.SOUTH),
                                    part.getQuads(Direction.EAST),
                                    part.getQuads(Direction.WEST)
                            ).flatMap(Collection::stream)
                            .map(
                                quad -> quad.materialInfo().layer().toString()
                            )
                    ).collect(Collectors.toSet());
                    if (types.size() == 1) return types.iterator().next();
                    if (types.isEmpty()) return "Not Applicable";
                    return types;
                }
        ));
        traits.add(new BlockStateTrait<>(
                "fluid_render_type",
                "Fluid Render Type",
                "Which fluid render type the block uses.",
                "",
                (state) -> fluidStateModelSet.get(state.getFluidState()).layer().toString()
        ));
        traits.add(new BlockStateTrait<>(
                "blocks_skylight",
                "Blocks Skylight",
                "Whether this block... blocks... skylight.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#propagatesSkylightDown",
                (state) -> !state.propagatesSkylightDown()
        ));
        traits.add(new BlockStateTrait<>(
                "instrument",
                "Instrument",
                "Which instrument a note block will play if placed above this block. This does not include mob heads.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#instrument",
                (state) -> state.instrument().toString()
        ));
        traits.add(new BlockStateTrait<>(
                "solid",
                "(Legacy) Solid",
                "Not fully accurate to its name; remains from when materials were still used.\nMarked as deprecated in the code but still used extensively.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isSolid",
                BlockBehaviour.BlockStateBase::isSolid
        ));
        traits.add(new BlockStateTrait<>(
                "blocks_motion",
                "(Legacy) Blocks Motion",
                "Not fully accurate to its name; remains from when materials were still used.\nMarked as deprecated in the code but still used extensively.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#blocksMotion",
                BlockBehaviour.BlockStateBase::blocksMotion
        ));
        traits.add(new BlockStateTrait<>(
                "liquid",
                "(Legacy) Liquid",
                "Not fully accurate to its name; remains from when materials were still used.\nMarked as deprecated in the code but still used extensively.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#liquid",
                BlockBehaviour.BlockStateBase::liquid
        ));
        traits.add(new BlockStateTrait<>(
                "ignited_by_lava",
                "Ignited By Lava",
                "Whether lava can set this block on fire.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#ignitedByLava",
                BlockBehaviour.BlockStateBase::ignitedByLava
        ));
        traits.add(new BlockStateTrait<>(
                "replaceable",
                "Replaceable",
                "Determines whether a block placed or falling on this block will replace it rather than being placed against or on it.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#canBeReplaced()",
                BlockBehaviour.BlockStateBase::canBeReplaced
        ));
        traits.add(new BlockStateTrait<>(
                "map_color",
                "Map Color",
                "The color of this block when viewed on a map. NONE is transparent, meaning it is simply ignored. Note that waterlogged blocks will have the map color of water.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getMapColor",
                (state) -> "{{mapColor|" + mapColorMap.get(state.getMapColor(new MockBlockGetter(state), BlockPos.ZERO)) + "}}"
        ));
        traits.add(new BlockStateTrait<>(
                "falling_block",
                "Falling Block",
                "Whether the block can fall when unsupported (as a falling_block entity).",
                "",
                (state) -> state.getBlock() instanceof Fallable || state.is(Blocks.SCAFFOLDING)
        ));
        traits.add(new BlockStateTrait<>(
                "water_forms_source_above",
                "Water Forms Sources Above",
                "Whether water can form a source above this block.\nIf infinite lava is on, it will form sources above the same blocks, but above lava and not above water.",
                "",
                (state) -> state.isSolid() || isWaterSource(state)
        ));
        traits.add(new BlockStateTrait<>(
                "spawnable",
                "Spawnable On",
                "Whether mobs can spawn on this block.",
                "net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#isValidSpawn",
                (state) -> (
                        state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.PLAYER) ? "Yes" :
                        state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.OCELOT) ? "Ocelots and Parrots Only" :
                        state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.POLAR_BEAR) ? "Polar Bear Only" :
                        state.isValidSpawn(new MockBlockGetter(state), BlockPos.ZERO, EntityType.BLAZE) ? "Fire-Immune Mobs Only" : "No"
                )
        ));
        traits.add(new BlockStateTrait<>(
                "iron_golem_spawnable_on",
                "Iron Golem Spawnable On",
                "Whether iron golems can spawn on this block.",
                "",
                (state) -> Block.isFaceFull(state.getCollisionShape(new MockBlockGetter(state), BlockPos.ZERO), Direction.UP)
        ));
        traits.add(new BlockStateTrait<>(
                "dragon_immune",
                "Dragon Immune",
                "Whether this block is immune to The Ender Dragon flying through it.",
                "",
                (state) -> state.is(BlockTags.DRAGON_IMMUNE) || state.is(BlockTags.DRAGON_TRANSPARENT)
        ));
        traits.add(new BlockStateTrait<>(
                "wither_block_break_immune",
                "Wither Block Break Immune",
                "Whether this block is immune to the wither's block breaking attack.",
                "",
                (state) -> state.is(BlockTags.WITHER_IMMUNE) || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.BUBBLE_COLUMN
        ));
        traits.add(new BlockStateTrait<>(
                "wither_skull_immune",
                "Wither Skull Immune",
                "Whether this block is immune to the wither's skull attack.",
                "",
                (state) -> {
                    float expRes = Math.max(state.getBlock().getExplosionResistance(), state.getFluidState().getExplosionResistance());
                    // net.minecraft.world.entity.projectile.WitherSkull.getBlockExplosionResistance
                    if (1.3 < 0.3 * (0.3 + expRes)) {
                        if (WitherBoss.canDestroy(state)) {
                            return "Only to black skulls";
                        }
                        return "Yes";
                    }
                    return "No";
                }
        ));
        assert Minecraft.getInstance().getSingleplayerServer() != null;
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
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        System.out.println("Generates in structures: " + e.getMessage());
                        return Stream.of();
                    }
                })
                .collect(Collectors.toUnmodifiableSet());
        traits.add(new BlockStateTrait<>(
                "generates_in_structures",
                "Generates in Structures",
                "Based off of what blocks show up in the standard structure palletes. Does not include all complex structures.",
                "",
                state -> blocksInStructures.contains(state.getBlock())
        ));
        traits.add(new BlockStateTrait<>(
                "obstructs_cactus",
                "Obstructs Cactus",
                "Whether placing this block next to cactus destroys it.",
                "",
                state -> state.isSolid() || state.getFluidState().is(FluidTags.LAVA)
        ));
        traits.add(new BlockStateTrait<>(
                "obstructs_tree_growth",
                "Obstructs Tree Growth",
                "",
                "",
                (state) -> !state.isAir() && !state.is(BlockTags.REPLACEABLE_BY_TREES)
        ));
        traits.add(new BlockStateTrait<>(
                "connects_to_walls",
                "Connects To Walls (North)",
                "Whether a wall block to the north will connect to this block.",
                "",
                (state) -> ((WallBlockAccessor) Blocks.ANDESITE_WALL).invokeConnectsTo(state, state.isFaceSturdy(new MockLevelReader(state), BlockPos.ZERO, Direction.NORTH), Direction.NORTH)
        ));
        traits.add(new BlockStateTrait<>(
                "raid_spawnable",
                "Raid Spawnable",
                "Whether raids can spawn on this block.",
                "",
                (state) ->
                        SpawnPlacementTypes.ON_GROUND.isSpawnPositionOk(
                                new MockMultiBlockLevelReader(Map.of(
                                        BlockPos.ZERO.above(), Blocks.AIR.defaultBlockState(),
                                        BlockPos.ZERO, Blocks.AIR.defaultBlockState(),
                                        BlockPos.ZERO.below(), state
                                )),
                                BlockPos.ZERO,
                                EntityType.RAVAGER
                        ) || state.is(Blocks.SNOW)
        ));
        traits.add(new BlockStateTrait<>(
                "pathfinding_type",
                "Pathfinding Type",
                "",
                "net.minecraft.world.level.pathfinder.WalkNodeEvaluator.getPathTypeFromState",
                (state) -> WalkNodeEvaluatorAccessor.invokeGetPathTypeFromState(new MockBlockGetter(state), BlockPos.ZERO)
        ));
        traits.add(new BlockStateTrait<>(
                "pathfinding_penalty",
                "Pathfinding Penalty",
                "",
                "net.minecraft.world.level.pathfinder.PathType.getMalus",
                (state) -> WalkNodeEvaluatorAccessor.invokeGetPathTypeFromState(new MockBlockGetter(state), BlockPos.ZERO).getMalus()
        ));
        traits.add(new BlockStateTrait<>(
                "requires_silk_touch",
                "Different Drop When Using Silk Touch",
                "Whether this block has a different drop when using a silk touch enchanted tool",
                "",
                // Making this foolproof is way, way more difficult than I thought it would be.
                (state) -> {
                    Optional<ResourceKey<LootTable>> tableKey = state.getBlock().getLootTable();
                    if (tableKey.isEmpty())
                        return "Not Applicable";
                    List<LootPool> pools = ((LootTableAccessor) (level.getServer().reloadableRegistries().getLootTable(tableKey.get()))).getPools();
                    for (LootPool pool : pools) {
                        Queue<LootItemCondition> conditions = new LinkedList<>(pool.conditions);
                        Queue<LootPoolEntryContainer> entries = new LinkedList<>(pool.entries);
                        while (!entries.isEmpty()) {
                            LootPoolEntryContainer entry = entries.remove();
                            conditions.addAll(((LootPoolEntryContainerAccessor) entry).getConditions());
                            if (entry instanceof CompositeEntryBase composite)
                                entries.addAll(((CompositeEntryBaseAccessor) composite).getChildren());
                        }
                        while (!conditions.isEmpty()) {
                            switch (conditions.remove()) {
                                case MatchTool matchTool:
                                    Optional<ItemPredicate> predicate = matchTool.predicate();
                                    if (predicate.isEmpty())
                                        continue;
                                    Map<DataComponentPredicate.Type<?>, DataComponentPredicate> partial = predicate.get().components().partial();
                                    if (!partial.containsKey(DataComponentPredicates.ENCHANTMENTS))
                                        continue;
                                    if (partial.get(DataComponentPredicates.ENCHANTMENTS) instanceof EnchantmentsPredicate enchantmentsPredicate) {
                                        for (EnchantmentPredicate enchantmentPredicate : ((EnchantmentsPredicateAccessor) enchantmentsPredicate).getEnchantments()) {
                                            Optional<HolderSet<Enchantment>> enchantments = enchantmentPredicate.enchantments();
                                            if (enchantments.isEmpty())
                                                continue;
                                            for (Holder<Enchantment> enchantment : enchantments.get()) {
                                                if (enchantment.is(Enchantments.SILK_TOUCH))
                                                    return "Yes";
                                            }
                                        }
                                    }
                                    break;
                                case CompositeLootItemCondition composite:
                                    conditions.addAll(((CompositeLootItemConditionAccessor) composite).getTerms());
                                    break;
                                case InvertedLootItemCondition inverted:
                                    conditions.add(inverted.term());
                                default:
                            }
                        }
                    }
                    return "No";
                }
        ));
        traits.add(new BlockStateTrait<>(
                "requires_shears",
                "Different Drop When Using Shears",
                "Whether this block has a different drop when using shears",
                "",
                (state) -> {
                    Optional<ResourceKey<LootTable>> tableKey = state.getBlock().getLootTable();
                    if (tableKey.isEmpty())
                        return "Not Applicable";
                    LootTable table = level.getServer().reloadableRegistries().getLootTable(tableKey.get());
                    List<LootPool> pools = ((LootTableAccessor) table).getPools();
                    for (LootPool pool : pools) {
                        Queue<LootItemCondition> conditions = new LinkedList<>(pool.conditions);
                        Queue<LootPoolEntryContainer> entries = new LinkedList<>(pool.entries);
                        while (!entries.isEmpty()) {
                            LootPoolEntryContainer entry = entries.remove();
                            conditions.addAll(((LootPoolEntryContainerAccessor) entry).getConditions());
                            if (entry instanceof CompositeEntryBase composite)
                                entries.addAll(((CompositeEntryBaseAccessor) composite).getChildren());
                        }
                        while (!conditions.isEmpty()) {
                            switch (conditions.remove()) {
                                case MatchTool matchTool:
                                    Optional<ItemPredicate> predicate = matchTool.predicate();
                                    if (predicate.isEmpty())
                                        continue;
                                    Optional<HolderSet<Item>> items = predicate.get().items();
                                    if (items.isEmpty())
                                        continue;
                                    for (Holder<Item> item : items.get()) {
                                        if (item.value().equals(Items.SHEARS))
                                            return "Yes";
                                    }
                                    break;
                                case CompositeLootItemCondition composite:
                                    conditions.addAll(((CompositeLootItemConditionAccessor) composite).getTerms());
                                    break;
                                case InvertedLootItemCondition inverted:
                                    conditions.add(inverted.term());
                                default:
                            }
                        }
                    }
                    return "No";
                }
        ));
        traits.add(new BlockStateTrait<>(
                "intended_tool",
                "Intended Tool",
                "The intended tool(s) used to destroy this block faster",
                "",
                (state) -> {
                    record Tool (String name, ItemStack item) {}
                    Tool[] tools = {
                            new Tool("Shovel", new ItemStack(Items.DIAMOND_SHOVEL)),
                            new Tool("Pickaxe", new ItemStack(Items.DIAMOND_PICKAXE)),
                            new Tool("Axe", new ItemStack(Items.DIAMOND_AXE)),
                            new Tool("Hoe", new ItemStack(Items.DIAMOND_HOE)),
                            new Tool("Sword", new ItemStack(Items.DIAMOND_SWORD)),
                            new Tool("Shears", new ItemStack(Items.SHEARS))
                    };
                    List<String> names = new ArrayList<>();
                    for (Tool tool : tools)
                        if (tool.item().getDestroySpeed(state) > 1)
                            names.add(tool.name());
                    return names;
                }
        ));
    }

    public static void getInstantUpdaterStuff(TraitCollection<BlockStateTrait<?>, SetMultimap<Block, BlockState>> traits) {
        traits.add(new BlockStateTrait<>(
                "instant_shape_updater",
                "Instant Shape Updater",
                "",
                "",
                (state) -> {
                    try {
                        Class<?> declaringClass = state.getBlock().getClass().getMethod("getStateForNeighborUpdate", BlockState.class, Direction.class, BlockState.class, LevelAccessor.class, BlockPos.class, BlockPos.class).getDeclaringClass();
                        if (declaringClass == BlockBehaviour.class) return false;
                        List<Class<?>> instantDeclaringClasses = List.of(BlockBehaviour.class, BasePressurePlateBlock.class, AmethystClusterBlock.class, AttachedStemBlock.class, BambooSaplingBlock.class, BannerBlock.class, BedBlock.class, BeehiveBlock.class, BellBlock.class, BigDripleafBlock.class, CakeBlock.class, CampfireBlock.class, CandleCakeBlock.class, CarpetBlock.class, ChestBlock.class, CocoaBlock.class, ConcretePowderBlock.class, CoralPlantBlock.class, CoralFanBlock.class, BaseCoralPlantTypeBlock.class, CoralWallFanBlock.class, BaseCoralWallFanBlock.class, DoorBlock.class, FenceBlock.class, FenceGateBlock.class, FireBlock.class, FlowerPotBlock.class, LiquidBlock.class, FrogspawnBlock.class, HangingRootsBlock.class, CeilingHangingSignBlock.class, LadderBlock.class, LanternBlock.class, MultifaceBlock.class, HugeMushroomBlock.class, NetherPortalBlock.class, NoteBlock.class, IronBarsBlock.class, PistonHeadBlock.class, BushBlock.class, MangrovePropaguleBlock.class, RedStoneWireBlock.class, RepeaterBlock.class, SeagrassBlock.class, SeaPickleBlock.class, StandingSignBlock.class, SnowLayerBlock.class, SoulFireBlock.class, SporeBlossomBlock.class, StairBlock.class, DoublePlantBlock.class, TorchBlock.class, TripWireBlock.class, TripWireHookBlock.class, VineBlock.class, WallBannerBlock.class, WallBlock.class, WallHangingSignBlock.class, FaceAttachedHorizontalDirectionalBlock.class, RedstoneWallTorchBlock.class, WallSignBlock.class, WallTorchBlock.class);
                        return instantDeclaringClasses.contains(declaringClass);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        return "No such method";
                    }
                }
        ));
        traits.add(new BlockStateTrait<>(
                "instant_block_updater",
                "Instant Block Updater",
                "",
                "",
                (state) -> {
                    try {
                        Class<?> declaringClass = state.getBlock().getClass().getMethod("neighborUpdate", BlockState.class, Level.class, BlockPos.class, Block.class, BlockPos.class, boolean.class).getDeclaringClass();
                        if (declaringClass == BlockBehaviour.class) return false;
                        List<Class<?>> instantDeclaringClasses = List.of(BaseRailBlock.class, DiodeBlock.class, BellBlock.class, BigDripleafBlock.class, DoorBlock.class, FenceGateBlock.class, LiquidBlock.class, FrostedIceBlock.class, HopperBlock.class, NoteBlock.class, PistonBaseBlock.class, PistonHeadBlock.class, RedStoneWireBlock.class, SpongeBlock.class, StructureBlock.class, TntBlock.class, TrapDoorBlock.class, DispenserBlock.class, RedstoneLampBlock.class);
                        return instantDeclaringClasses.contains(declaringClass);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        return "No such method";
                    }
                }
        ));
    }

    public static <T> void addBlockTagProperties(TraitCollection<BlockStateTrait<?>, SetMultimap<Block, BlockState>> traits, Class<T> clazz) {
        for (Field staticField : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(staticField.getModifiers())) {
                try {
                    @SuppressWarnings("unchecked")
                    TagKey<Block> tag = (TagKey<Block>) staticField.get(null);
                    traits.add(new BlockStateTrait<>(
                            "tag_" + staticField.getName().toLowerCase(),
                            "Tag: " + staticField.getName(),
                            staticField.getName(),
                            "",
                            (state) -> state.is(tag)
                    ));
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