package net.fabricmc.joamama.mock;

import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public record MockShapeContext (boolean descending, boolean isAbove, boolean isHolding, boolean canWalkOnFluid) implements ShapeContext {
    public static ShapeContext absent() { return ShapeContext.absent(); }

    public static ShapeContext of(Entity entity) {
        return ShapeContext.of(entity);
    }

    public boolean isDescending() { return descending; }

    public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) { return isAbove; }

    public boolean isHolding(Item var1) { return isHolding; }

    public boolean canWalkOnFluid(FluidState var1, FluidState var2) { return canWalkOnFluid; }

}
