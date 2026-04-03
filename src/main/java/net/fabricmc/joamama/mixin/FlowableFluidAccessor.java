package net.fabricmc.joamama.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlowingFluid.class)
public interface FlowableFluidAccessor {
//    @Invoker
//    boolean invokeCanSpreadTo(BlockGetter world, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid);

    // replaced with one of:

//    canMaybePassThrough(serverLevel, blockPos, blockState, direction, blockPos2, blockState2, fluidState)
    @Invoker
    boolean invokeCanMaybePassThrough(
            BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Direction direction, BlockPos blockPos2, BlockState blockState2, FluidState fluidState
    );
//    canHoldSpecificFluid(serverLevel, blockPos2, blockState2, fluidState2.getType())
    @Invoker
    static boolean invokeCanHoldSpecificFluid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return false;
    }
    // canBeReplacedWith(serverLevel, blockPos2, fluidState2.getType(), direction)
    // (already public)
}
