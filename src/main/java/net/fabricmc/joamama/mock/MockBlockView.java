package net.fabricmc.joamama.mock;

import com.google.errorprone.annotations.DoNotCall;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public record MockBlockView (BlockState state) implements BlockView {
    public BlockEntity getBlockEntity (BlockPos pos) {
        return null;
    }

    public BlockState getBlockState (BlockPos pos) {
        return state;
    }

    public FluidState getFluidState (BlockPos pos) {
        return state.getFluidState();
    }

    @DoNotCall
    public int getHeight () {
        throw new AssertionError();
    }

    @DoNotCall
    public int getBottomY () {
        throw new AssertionError();
    }
}