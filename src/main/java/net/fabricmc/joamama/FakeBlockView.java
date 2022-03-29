package net.fabricmc.joamama;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class FakeBlockView implements BlockView {
    BlockState block;

    public FakeBlockView (BlockState block) {
        this.block = block;
    }

    public BlockEntity getBlockEntity (BlockPos pos) {
        return null;
    }
    public BlockState getBlockState (BlockPos pos) {
        return block;
    }
    public int getLuminance (BlockPos pos) {
        return block.getLuminance();
    }
    public FluidState getFluidState (BlockPos pos) {
        return block.getFluidState();
    }
    public int getHeight () {
        return 0;
    }
    public int getBottomY () {
        return 0;
    }
}