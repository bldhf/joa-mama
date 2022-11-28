package net.fabricmc.joamama.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.SlabBlock.TYPE;
import static net.minecraft.state.property.Properties.WATERLOGGED;

@Mixin (SlabBlock.class)
public abstract class SlabBlockMixin extends Block {
    public SlabBlockMixin (Settings settings) {
        super(settings);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void onInit (CallbackInfo ci) {
        this.setDefaultState(this.getDefaultState().with(TYPE, SlabType.TOP).with(WATERLOGGED, true));
    }
}
