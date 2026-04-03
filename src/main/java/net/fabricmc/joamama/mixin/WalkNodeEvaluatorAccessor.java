package net.fabricmc.joamama.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WalkNodeEvaluator.class)
public interface WalkNodeEvaluatorAccessor {
    @Invoker
    static PathType invokeGetPathTypeFromState(BlockGetter blockGetter, BlockPos blockPos) {
        throw new AssertionError();
    }
}
