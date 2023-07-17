package net.fabricmc.joamama.mock;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public record MockCollisionView (BlockState state) implements CollisionGetter {
    @Deprecated
    public WorldBorder getWorldBorder () {
        throw new AssertionError();
    }

    @Deprecated
    public BlockGetter getChunkForCollisions (int chunkX, int chunkZ) {
        throw new AssertionError();
    }

    @Deprecated
    public List<VoxelShape> getEntityCollisions (Entity entity, AABB box) {
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
    public int getMinBuildHeight() {
        throw new AssertionError();
    }
}
