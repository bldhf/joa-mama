package net.fabricmc.joamama;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.fabricmc.joamama.mock.MockBlockView;
import net.fabricmc.joamama.mock.MockWorldView;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@SuppressWarnings("UnusedReturnValue")
public abstract class BlockStateTraits {
    private static final Vector<BlockState> blockStates;
    private static final Map<Material, String> materialMap = new HashMap<>();
    private static final Map<FluidState, String> fluidStateMap = new HashMap<>();
    private static final Map<MapColor, String> mapColorMap = new HashMap<>();

    static {
        blockStates = new Vector<>();
    }

    public static void load (Iterable<Block> blocks) {
        blocks.forEach(block -> blockStates.addAll(block.getStateManager().getStates()));

        // Fill up the lookup maps
        setupClassNames(Material.class, materialMap);
        setupClassNames(FluidState.class, fluidStateMap);
        setupClassNames(MapColor.class, mapColorMap);

        for (Map.Entry<Material,String> material : materialMap.entrySet())
            System.out.println(material.getValue()+" - "+material.getKey());

        System.out.println(materialMap.get(Material.AMETHYST));


    }

    public static <T> void setupClassNames(Class<T> clazz, Map<T, String> map) {
        for(Field staticField : clazz.getDeclaredFields()) {
            if(staticField.getType() == clazz &&
                    Modifier.isStatic(staticField.getModifiers())) {
                try {
                    T value = (T)staticField.get(null);
                    map.put(value, staticField.getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void test () {
        air();
        getFluidState();
        hardness();
        luminance();
        material();
        opaque();
        pistonBehavior();
        hasRandomTicks();
        solidBlock();
        translucent();

        bottomFaceHasFullSquare();
        topFaceHasFullSquare();
        northFaceHasFullSquare();
        southFaceHasFullSquare();
        westFaceHasFullSquare();
        eastFaceHasFullSquare();
        topFaceHasRim();
        bottomFaceHasSmallSquare();
        topFaceHasSmallSquare();
    }


    public static JoaProperty<BlockState, Boolean> air () {
        return new JoaProperty<>(
                "air",
                "Air",
                "",
                (state) -> state.isAir(),
                blockStates
        );
    }

    public static JoaProperty<BlockState, String> getFluidState () {
        return new JoaProperty<>(
                "fluid_state",
                "Fluid State",
                "",
                (state) -> fluidStateMap.get(state.getFluidState()),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Float> hardness () {
        return new JoaProperty<>(
                "hardness",
                "Hardness",
                "",
                (state) -> state.getHardness(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Integer> luminance () {
        return new JoaProperty<>(
                "luminance",
                "Luminance",
                "",
                (state) -> state.getLuminance(),
                blockStates
        );
    }

    public static JoaProperty<BlockState, String> material () {
        return new JoaProperty<>(
                "material",
                "Material",
                "",
                (state) -> materialMap.get(state.getMaterial()),
                blockStates
        );
    }

    public static JoaProperty<BlockState, String> mapColor () {
        return new JoaProperty<>(
                "map_color",
                "Map Color",
                "",
                (state) -> mapColorMap.get(state.getMapColor(new MockBlockView(state), BlockPos.ORIGIN)),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> opaque () {
         return new JoaProperty<>(
                "opaque",
                "Opaque",
                "",
                 (state) -> state.isOpaque(),
                 blockStates
        );
    }

    public static JoaProperty<BlockState, PistonBehavior> pistonBehavior () {
         return new JoaProperty<>(
                "piston_behavior",
                "Piston Behavior",
                "",
                 (state) -> state.getPistonBehavior(),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> hasRandomTicks () {
        return new JoaProperty<>(
                "has_random_ticks",
                "Has Random Ticks",
                "",
                (state) -> state.hasRandomTicks(),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> solidBlock () {
         return new JoaProperty<>(
                "solid_block",
                "Solid Block",
                "",
                 (state) -> state.isSolidBlock(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> shouldSuffocate () {
        return new JoaProperty<>(
                "should_suffocate",
                "Should Suffocate",
                "",
                (state) -> state.shouldSuffocate(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> translucent () {
        return new JoaProperty<>(
                "translucent",
                "Translucent",
                "",
                (state) -> state.isTranslucent(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Integer> opacity () {
        return new JoaProperty<>(
                "opacity",
                "Opacity",
                "",
                (state) -> state.getOpacity(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }



    public static JoaProperty<BlockState, Boolean> bottomFaceHasFullSquare () {
        return new JoaProperty<>(
                "bottom_face_has_full_square",
                "Bottom Face Has Full Square",
                "This is true if the bottom face is a full square.",
                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.DOWN),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> topFaceHasFullSquare () {
        return new JoaProperty<>(
                "top_face_has_full_square",
                "Top Face Has Full Square",
                "This is true if the top face is a full square.",
                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.UP),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> northFaceHasFullSquare () {
        return new JoaProperty<>(
                "north_face_has_full_square",
                "North Face Has Full Square",
                "This is true if the north face is a full square.",
                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.NORTH),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> southFaceHasFullSquare () {
        return new JoaProperty<>(
                "south_face_has_full_square",
                "south Face Has Full Square",
                "This is true if the south face is a full square.",
                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.SOUTH),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> westFaceHasFullSquare () {
        return new JoaProperty<>(
                "west_face_has_full_square",
                "West Face Has Full Square",
                "This is true if the west face is a full square.",
                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.WEST),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> eastFaceHasFullSquare () {
        return new JoaProperty<>(
                "east_face_has_full_square",
                "East Face Has Full Square",
                "This is true if the east face is a full square.",
                (state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.EAST),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> topFaceHasRim () {
        return new JoaProperty<>(
                "top_face_has_rim",
                "Top Face Has Rim",
                "This is true if the top face contains a 2 pixel wide ring going around its edge",
                (state) -> Block.hasTopRim(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> bottomFaceHasSmallSquare () {
        return new JoaProperty<>(
                "bottom_face_has_small_square",
                "Bottom Face Has Small Square",
                "This is true if the bottom face contains a square of length 2 at its center.",
                (state) -> Block.sideCoversSmallSquare(new MockWorldView(state), BlockPos.ORIGIN, Direction.DOWN),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> topFaceHasSmallSquare () {
        return new JoaProperty<>(
                "top_face_has_small_square",
                "Top Face Has Small Square",
                "This is true if the top face contains a square of length 2 at its center.",
                (state) -> Block.sideCoversSmallSquare(new MockWorldView(state), BlockPos.ORIGIN, Direction.UP),
                blockStates
        );
    }

    public static JoaProperty<BlockState, Boolean> wireConnectsNorth () {
        return new JoaProperty<>(
                "redstone_wire_connects_north",
                "Redstone Wire Connects North",
                "This is true if the North side connects to redstone dust",
                (state) -> RedstoneWireBlock.connectsTo(state, Direction.NORTH), // uses an accesswidener
                blockStates
        );
    }
}
