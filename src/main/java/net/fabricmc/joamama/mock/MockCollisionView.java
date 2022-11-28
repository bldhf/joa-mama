package net.fabricmc.joamama.mock;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.border.WorldBorder;

import java.util.List;

public record MockCollisionView (BlockState state) implements CollisionView {
    @Deprecated
    public WorldBorder getWorldBorder () {
        throw new AssertionError();
    }

    @Deprecated
    public BlockView getChunkAsView (int chunkX, int chunkZ) {
        throw new AssertionError();
    }

    @Deprecated
    public List<VoxelShape> getEntityCollisions (Entity entity, Box box) {
        throw new AssertionError();
    }

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
    public int getHeight() {
        throw new AssertionError();
    }

    @Deprecated
    public int getBottomY() {
        throw new AssertionError();
    }
}
