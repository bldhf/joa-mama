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

public abstract class BlockStateTraits {
    private static final SetMultimap<Block, BlockState> blockStates;

    static {
        blockStates = MultimapBuilder.hashKeys().hashSetValues().build();
    }

    public static void load (Iterable<Block> blocks) {
        blocks.forEach(block -> blockStates.putAll(block, block.getStateManager().getStates()));
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



    public static StateTrait<Block, BlockState, Boolean> hasTopRim () {
        return new StateTrait<>(
                "has_top_rim",
                "Has Top Rim",
                "",
                (block, state) -> Block.hasTopRim(new MockBlockView(state), BlockPos.ORIGIN),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> sideCoversSmallSquare (Direction side) {
        return new StateTrait<>(
                "side_covers_small_square_" + side.toString(),
                "Side Covers Small Square (" + side.toString().substring(0, 1).toUpperCase() + side.toString().substring(1) + ")",
                "",
                (block, state) -> Block.sideCoversSmallSquare(new MockWorldView(state), BlockPos.ORIGIN, side),
                blockStates
        );
    }

    public static StateTrait<Block, BlockState, Boolean> sideCoversSmallSquareDown () {
        return sideCoversSmallSquare(Direction.DOWN);
    }

    public static StateTrait<Block, BlockState, Boolean> sideCoversSmallSquareUp () {
        return sideCoversSmallSquare(Direction.UP);
    }

    public static StateTrait<Block, BlockState, Boolean> sideCoversSmallSquareNorth () {
        return sideCoversSmallSquare(Direction.NORTH);
    }

    public static StateTrait<Block, BlockState, Boolean> sideCoversSmallSquareSouth () {
        return sideCoversSmallSquare(Direction.SOUTH);
    }

    public static StateTrait<Block, BlockState, Boolean> sideCoversSmallSquareWest () {
        return sideCoversSmallSquare(Direction.WEST);
    }

    public static StateTrait<Block, BlockState, Boolean> sideCoversSmallSquareEast () {
        return sideCoversSmallSquare(Direction.EAST);
    }
}
