package net.fabricmc.joamama.mock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public record MockShapeContext (boolean descending, boolean isAbove, boolean isHolding, boolean canWalkOnFluid) implements CollisionContext {
    public static CollisionContext empty() { return CollisionContext.empty(); }

    public static CollisionContext of(Entity entity) {
        return CollisionContext.of(entity);
    }

    public boolean isDescending() { return descending; }

    public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) { return isAbove; }

    public boolean isHoldingItem(Item var1) { return isHolding; }

    public boolean canStandOnFluid(FluidState var1, FluidState var2) { return canWalkOnFluid; }

}
