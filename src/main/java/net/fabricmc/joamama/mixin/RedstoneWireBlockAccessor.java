package net.fabricmc.joamama.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin (RedstoneWireBlock.class)
public interface RedstoneWireBlockAccessor {
    @Invoker
    boolean invokeCanRunOnTop(BlockView world, BlockPos pos, BlockState floor);
}
