package net.fabricmc.joamama.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin (RedStoneWireBlock.class)
public interface RedStoneWireBlockAccessor {
    @Invoker
    boolean invokeCanSurviveOn(BlockGetter world, BlockPos pos, BlockState floor);

    @Invoker
    static boolean invokeShouldConnectTo(BlockState blockState, @Nullable Direction direction) { return true; }
}
