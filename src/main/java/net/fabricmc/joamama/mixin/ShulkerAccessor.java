package net.fabricmc.joamama.mixin;

import net.minecraft.world.entity.monster.Shulker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Shulker.class)
public interface ShulkerAccessor {
    @Invoker
    void invokeSetRawPeekAmount(int i);

    @Accessor
    void setCurrentPeekAmount(float f);
}
