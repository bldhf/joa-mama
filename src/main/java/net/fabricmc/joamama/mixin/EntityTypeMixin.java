package net.fabricmc.joamama.mixin;

import net.fabricmc.joamama.JoaMama;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {
    @Inject(method = "toString", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings("all")
    private void onToString(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(((EntityType) (Object) this).getDescription().getString());
    }
}
