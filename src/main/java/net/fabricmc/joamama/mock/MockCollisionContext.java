package net.fabricmc.joamama.mock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public record MockCollisionContext (boolean descending, boolean isAbove, boolean isHolding, boolean alwaysCollideWithFlud, boolean canWalkOnFluid) implements CollisionContext {
    public static CollisionContext empty() { return CollisionContext.empty(); }

    public static CollisionContext of(Entity entity) {
        return CollisionContext.of(entity);
    }

    public boolean isDescending() { return descending; }

    public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) { return isAbove; }

    public boolean isHoldingItem(Item var1) { return isHolding; }

    public boolean alwaysCollideWithFluid() { return alwaysCollideWithFlud; }

    public boolean canStandOnFluid(FluidState var1, FluidState var2) { return canWalkOnFluid; }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, CollisionGetter collisionGetter, BlockPos blockPos) {
        return null;
    }

}
