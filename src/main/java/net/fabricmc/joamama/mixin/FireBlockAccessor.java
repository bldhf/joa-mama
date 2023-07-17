package net.fabricmc.joamama.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin (FireBlock.class)
public interface FireBlockAccessor {
    @Invoker
    int invokeGetBurnOdds(BlockState state);

    @Invoker
    int invokeGetIgniteOdds(BlockState state);

}
