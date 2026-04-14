package net.fabricmc.joamama.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SpreadingSnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpreadingSnowyBlock.class)
public interface SpreadableBlockAccessor {
    @Invoker
    static boolean invokeCanStayAlive(BlockState state, LevelReader world, BlockPos pos) {
        return false;
    }
}
