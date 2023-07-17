package net.fabricmc.joamama.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.block.SlabBlock.TYPE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;

@Mixin (SlabBlock.class)
public abstract class SlabBlockMixin extends Block {
    public SlabBlockMixin (Properties settings) {
        super(settings);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void onInit (CallbackInfo ci) {
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, SlabType.TOP).setValue(WATERLOGGED, true));
    }
}
