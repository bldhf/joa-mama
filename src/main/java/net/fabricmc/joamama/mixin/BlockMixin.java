package net.fabricmc.joamama.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (Block.class)
public abstract class BlockMixin extends AbstractBlock{
    public BlockMixin (Settings settings) {
        super(settings);
    }

    @Inject (
            method = "toString",
            at = @At ("RETURN"),
            cancellable = true
    )
    private void onToString (CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(Registry.BLOCK.getId((Block) (AbstractBlock) this).toString());
    }
}
