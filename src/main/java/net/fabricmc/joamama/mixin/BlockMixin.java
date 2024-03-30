package net.fabricmc.joamama.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour {
    public BlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "toString", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings("all")
    private void onToString(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(BuiltInRegistries.BLOCK.getKey((Block) (BlockBehaviour) this).toString());
    }
}
