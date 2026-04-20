package net.fabricmc.joamama.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CactusBlock.class)
public interface CactusBlockAccessor {
    @Invoker
    boolean invokeCanSurvive(final BlockState state, final LevelReader level, final BlockPos pos);

}
