package net.fabricmc.joamama;

import com.google.common.collect.*;
import net.fabricmc.joamama.mock.MockBlockView;
import net.fabricmc.joamama.mock.MockWorldView;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.fluid.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings("UnusedReturnValue")
public abstract class BlockStateTraits {
    private static final Vector<BlockState> blockStates;
    private static final Map<Material, String> materialMap = new HashMap<>();
    private static final Map<MapColor, String> mapColorMap = new HashMap<>();
    private static final Map<BlockTags, String> blockTags = new HashMap<>();

    enum WaterloggableValue {
        FALSE,
        TRUE,
        INHERENT
    }

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
                        /*new JoaProperty<>(
                                "air",
                                "Air",
                                "",
                                AbstractBlock.AbstractBlockState::isAir,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "hardness",
                                "Hardness",
                                "Determines how fast the block can be mined",
                                (state) -> state.getHardness(new MockBlockView(state), BlockPos.ORIGIN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "blast_resistance",
                                "Blast Resistance",
                                "Determines how likely this block is to break from exposure to an explosion",
                                (state) -> state.getBlock().getBlastResistance(),
                                // (state) -> state.isAir() && state.getFluidState().isEmpty() ? Optional.empty() : Optional.of(Math.max(state.getBlock().getBlastResistance(), state.getFluidState().getBlastResistance())),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "luminance",
                                "Luminance",
                                "",
                                AbstractBlock.AbstractBlockState::getLuminance,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "material",
                                "Material",
                                "",
                                (state) -> materialMap.get(state.getMaterial()),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "material_is_liquid",
                                "Material isLiquid()",
                                "",
                                (state) -> state.getMaterial().isLiquid(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "material_is_solid",
                                "Material isSolid()",
                                "",
                                (state) -> state.getMaterial().isSolid(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "material_blocks_movement",
                                "Material blocksMovement()",
                                "",
                                (state) -> state.getMaterial().blocksMovement(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "material_is_burnable",
                                "Material isBurnable()",
                                "",
                                (state) -> state.getMaterial().isBurnable(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "material_is_replaceable",
                                "Material isReplaceable()",
                                "",
                                (state) -> state.getMaterial().isReplaceable(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "material_blocks_light",
                                "Material blocksLight()",
                                "",
                                (state) -> state.getMaterial().blocksLight(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "map_color",
                                "Map Color",
                                "",
                                (state) -> mapColorMap.get(state.getMapColor(new MockBlockView(state), BlockPos.ORIGIN)),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "opaque",
                                "Opaque",
                                "",
                                AbstractBlock.AbstractBlockState::isOpaque,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "piston_behavior",
                                "Piston Behavior",
                                "",
                                AbstractBlock.AbstractBlockState::getPistonBehavior,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "has_random_ticks",
                                "Has Random Ticks",
                                "",
                                AbstractBlock.AbstractBlockState::hasRandomTicks,
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "conductivity",
                                "Conductivity",
                                "",
                                (state) -> state.isSolidBlock(new MockBlockView(state), BlockPos.ORIGIN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "should_suffocate",
                                "Should Suffocate",
                                "",
                                (state) -> state.shouldSuffocate(new MockBlockView(state), BlockPos.ORIGIN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "translucent",
                                "Translucent",
                                "",
                                (state) -> state.isTranslucent(new MockBlockView(state), BlockPos.ORIGIN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "opacity",
                                "Opacity",
                                "",
                                (state) -> state.getOpacity(new MockBlockView(state), BlockPos.ORIGIN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "is_opaque_full_cube",
                                "Is Opaque Full Cube",
                                "",
                                (state) -> state.isOpaqueFullCube(new MockBlockView(state), BlockPos.ORIGIN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "bottom_face_has_full_square",
                                "Bottom Face Has Full Square",
                                "This is true if the bottom face is a full square.",
                                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.DOWN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "top_face_has_full_square",
                                "Top Face Has Full Square",
                                "This is true if the top face is a full square.",
                                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.UP),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "north_face_has_full_square",
                                "North Face Has Full Square",
                                "This is true if the north face is a full square.",
                                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.NORTH),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "south_face_has_full_square",
                                "South Face Has Full Square",
                                "This is true if the south face is a full square.",
                                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.SOUTH),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "west_face_has_full_square",
                                "West Face Has Full Square",
                                "This is true if the west face is a full square.",
                                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.WEST),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "east_face_has_full_square",
                                "East Face Has Full Square",
                                "This is true if the east face is a full square.",
                                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.EAST),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "top_face_has_rim",
                                "Top Face Has Rim",
                                "This is true if the top face contains a 2 pixel wide ring going around its edge",
                                (state) -> Block.hasTopRim(new MockBlockView(state), BlockPos.ORIGIN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "bottom_face_has_small_square",
                                "Bottom Face Has Small Square",
                                "This is true if the bottom face contains a square of length 2 at its center.",
                                (state) -> Block.sideCoversSmallSquare(new MockWorldView(state), BlockPos.ORIGIN, Direction.DOWN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "top_face_has_small_square",
                                "Top Face Has Small Square",
                                "This is true if the top face contains a square of length 2 at its center.",
                                (state) -> Block.sideCoversSmallSquare(new MockWorldView(state), BlockPos.ORIGIN, Direction.UP),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "side_face_has_small_square",
                                "Side Face Has Small Square (North)",
                                "This is true if the north face contains a square of length 2 at its center.",
                                (state) -> Block.sideCoversSmallSquare(new MockWorldView(state), BlockPos.ORIGIN, Direction.NORTH),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "bottom_face_has_collision",
                                "Bottom Face Has Small Square",
                                "This is true if the bottom side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ORIGIN).getFace(Direction.DOWN).isEmpty(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "top_face_has_collision",
                                "Top Face Has Small Square",
                                "This is true if the top side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ORIGIN).getFace(Direction.UP).isEmpty(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "side_face_has_collision",
                                "North Face Has Full Square",
                                "This is true if the north side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ORIGIN).getFace(Direction.NORTH).isEmpty(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "south_face_has_collision",
                                "South Face Has Full Square",
                                "This is true if the south side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ORIGIN).getFace(Direction.SOUTH).isEmpty(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "west_face_has_collision",
                                "West Face Has Full Square",
                                "This is true if the west side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ORIGIN).getFace(Direction.WEST).isEmpty(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "east_face_has_collision",
                                "East Face Has Full Square",
                                "This is true if the east side has collision at the outer face (meaning it has a hard hitbox that aligns with the block grid).",
                                (state) -> !state.getCollisionShape(new MockWorldView(state), BlockPos.ORIGIN).getFace(Direction.EAST).isEmpty(),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "redirects_redstone",
                                "Redirects Redstone Wire (North)",
                                "This is true if the North side connects to/redirects adjacent redstone dust",
                                (state) -> RedstoneWireBlock.connectsTo(state, Direction.SOUTH), // uses an accesswidener, the direction is reversed so the "perspective" makes sense.
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "connects_to_panes",
                                "Connects To Panes (North)",
                                "Whether a glass pane block will connect to this block",
                                (state) -> ((PaneBlock) Blocks.GLASS_PANE).connectsTo(state, state.isSideSolidFullSquare(new MockBlockView(state), BlockPos.ORIGIN, Direction.NORTH)),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "blocks_beacon_beam",
                                "Blocks Beacon Beam",
                                "Whether placing this block above a beacon will prevent its beam from forming, or stop its current one.",
                                // net/minecraft/block/entity/BeaconBlockEntity.java:150
                                (state) -> !( state.getOpacity(new MockBlockView(state), BlockPos.ORIGIN) < 15 || state.isOf(Blocks.BEDROCK) ),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "raid_spawnable",
                                "Raid Spawnable",
                                "Whether raids can spawn on this block.",
                                // net/minecraft/village/raid/Raid.java:573
                                (state) ->
                                    SpawnHelper.canSpawn(
                                        SpawnRestriction.Location.ON_GROUND,
                                        new MockWorldView(state),
                                        BlockPos.ORIGIN.up(),
                                        EntityType.RAVAGER
                                    ) || state.isOf(Blocks.SNOW),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "full_cube",
                                "Full Cube",
                                "Whether the block has a normal cube shape and has full block collision on all sides.",
                                (state) -> state.isFullCube(new MockWorldView(state), BlockPos.ORIGIN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "occlusion_shape",
                                "Occlusion Shape",
                                "Used in rendering",
                                (state) -> state.isFullCube(new MockWorldView(state), BlockPos.ORIGIN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "bottom_face_has_solid_full_square",
                                "Bottom Face Has Solid Full Square",
                                "This is true if the bottom face is a full square.",
                                (state) -> state.isSideSolidFullSquare(new MockBlockView(state), BlockPos.ORIGIN, Direction.DOWN),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "top_face_has_solid_full_square",
                                "Top Face Has Solid Full Square",
                                "This is true if the top face is a full square.",
                                (state) -> state.isSideSolidFullSquare(new MockBlockView(state), BlockPos.ORIGIN, Direction.UP),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "north_face_has_solid_full_square",
                                "North Face Has Solid Full Square",
                                "This is true if the north face is a full square.",
                                (state) -> state.isSideSolidFullSquare(new MockBlockView(state), BlockPos.ORIGIN, Direction.NORTH),
                                blockStates
                        ).toString(),

                        new JoaProperty<>(
                                "waterloggable",
                                "Waterloggable",
                                "Whether this block can be waterlogged.",
                                (state) -> state.getBlock() instanceof Waterloggable ? WaterloggableValue.TRUE : Arrays.<Fluid>asList(Fluids.WATER, Fluids.FLOWING_WATER).contains(state.getFluidState().getFluid()) ? WaterloggableValue.INHERENT : WaterloggableValue.FALSE,
                                blockStates
                        ).toString(),*/

                        new JoaProperty<>(
                                "gets_flushed",
                                "Gets Flushed",
                                "Whether this block will get destroyed by flowing water.",
                                null,
                                blockStates
                        ).toString()
                )
        );
    }

    public static <T> void addBlockTagProperties(ArrayList<String> arr, Class<T> clazz) {
        for (Field staticField : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(staticField.getModifiers())) {
                try {
                    @SuppressWarnings("unchecked")
                    TagKey<Block> value = (TagKey<Block>) staticField.get(null);
                    arr.add(new JoaProperty<>(
                            "tag_" + staticField.getName().toLowerCase(),
                            "Tag: " + staticField.getName(),
                            "" + staticField.getName(),
                            (state) -> state.isIn(value),
                            // (state) -> state.isIn(BlockTags.NEEDS_IRON_TOOL),
                            blockStates
                    ).toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
