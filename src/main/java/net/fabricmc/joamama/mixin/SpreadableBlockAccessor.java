package net.fabricmc.joamama.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin (SpreadableBlock.class)
public interface SpreadableBlockAccessor {
    @Invoker
    static boolean invokeCanSurvive(BlockState state, WorldView world, BlockPos pos) {
        return false;
    }
}
