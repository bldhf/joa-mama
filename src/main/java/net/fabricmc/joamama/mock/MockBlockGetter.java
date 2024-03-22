package net.fabricmc.joamama.mock;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public record MockBlockGetter (BlockState state) implements BlockGetter {
    public BlockEntity getBlockEntity (BlockPos pos) {
        return null;
    }

    public BlockState getBlockState (BlockPos pos) {
        return state;
    }

    public FluidState getFluidState (BlockPos pos) {
        return state.getFluidState();
    }

    @Deprecated
    public int getHeight () {
        throw new AssertionError();
    }

    @Deprecated
    public int getMinBuildHeight () {
        throw new AssertionError();
    }
}