package net.fabricmc.joamama;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.fabricmc.joamama.mock.MockBlockView;
import net.fabricmc.joamama.mock.MockWorldView;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnusedReturnValue")
public abstract class BlockStateTraits {
    private static final SetMultimap<Block, BlockState> blockStates;

    static {
        blockStates = MultimapBuilder.hashKeys().hashSetValues().build();
    }

    public static void load (Iterable<Block> blocks) {
        blocks.forEach(block -> blockStates.putAll(block, block.getStateManager().getStates()));
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



    public static StateTrait<Block, BlockState, Boolean> air () {
        return new StateTrait<>(
                "air",
                "Air",
                "",
                (block, state) -> state.isAir(),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, FluidState> getFluidState () {
        return new StateTrait<>(
                "fluid_state",
                "Fluid State",
                "",
                (block, state) -> state.getFluidState(),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Float> hardness () {
        return new StateTrait<>(
                "hardness",
                "Hardness",
                "",
                (block, state) -> state.getHardness(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Integer> luminance () {
        return new StateTrait<>(
                "luminance",
                "Luminance",
                "",
                (block, state) -> state.getLuminance(),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Material> material () {
        return new StateTrait<>(
                "material",
                "Material",
                "",
                (block, state) -> state.getMaterial(),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> opaque () {
         return new StateTrait<>(
                "opaque",
                "Opaque",
                "",
                 (block, state) -> state.isOpaque(),
                 blockStates
        );
    }

    public static StateTrait<Block, BlockState, PistonBehavior> pistonBehavior () {
         return new StateTrait<>(
                "piston_behavior",
                "Piston Behavior",
                "",
                 (block, state) -> state.getPistonBehavior(),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> hasRandomTicks () {
        return new StateTrait<>(
                "has_random_ticks",
                "Has Random Ticks",
                "",
                (block, state) -> state.hasRandomTicks(),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> solidBlock () {
         return new StateTrait<>(
                "solid_block",
                "Solid Block",
                "",
                 (block, state) -> state.isSolidBlock(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> translucent () {
        return new StateTrait<>(
                "translucent",
                "Translucent",
                "",
                (block, state) -> state.isTranslucent(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }



    public static StateTrait<Block, BlockState, Boolean> bottomFaceHasFullSquare () {
        return new StateTrait<>(
                "bottom_face_has_full_square",
                "Bottom Face Has Full Square",
                "This is true if the bottom face is a full square.",
                (block, state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.DOWN),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> topFaceHasFullSquare () {
        return new StateTrait<>(
                "top_face_has_full_square",
                "Top Face Has Full Square",
                "This is true if the top face is a full square.",
                (block, state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.UP),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> northFaceHasFullSquare () {
        return new StateTrait<>(
                "north_face_has_full_square",
                "North Face Has Full Square",
                "This is true if the north face is a full square.",
                (block, state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.NORTH),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> southFaceHasFullSquare () {
        return new StateTrait<>(
                "south_face_has_full_square",
                "south Face Has Full Square",
                "This is true if the south face is a full square.",
                (block, state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.SOUTH),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> westFaceHasFullSquare () {
        return new StateTrait<>(
                "west_face_has_full_square",
                "West Face Has Full Square",
                "This is true if the west face is a full square.",
                (block, state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.WEST),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> eastFaceHasFullSquare () {
        return new StateTrait<>(
                "east_face_has_full_square",
                "East Face Has Full Square",
                "This is true if the east face is a full square.",
                (block, state) -> Block.isFaceFullSquare(state.getCollisionShape(new MockBlockView(state), BlockPos.ORIGIN), Direction.EAST),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> topFaceHasRim () {
        return new StateTrait<>(
                "top_face_has_rim",
                "Top Face Has Rim",
                "This is true if the top face contains a 2 pixel wide ring going around its edge",
                (block, state) -> Block.hasTopRim(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> bottomFaceHasSmallSquare () {
        return new StateTrait<>(
                "bottom_face_has_small_square",
                "Bottom Face Has Small Square",
                "This is true if the bottom face contains a square of length 2 at its center.",
                (block, state) -> Block.sideCoversSmallSquare(new MockWorldView(state), BlockPos.ORIGIN, Direction.DOWN),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> topFaceHasSmallSquare () {
        return new StateTrait<>(
                "top_face_has_small_square",
                "Top Face Has Small Square",
                "This is true if the top face contains a square of length 2 at its center.",
                (block, state) -> Block.sideCoversSmallSquare(new MockWorldView(state), BlockPos.ORIGIN, Direction.UP),
                blockStates
        );
    }
}
