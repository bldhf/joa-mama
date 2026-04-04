package net.fabricmc.joamama.mixin;

import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStand.class)
public interface ArmorStandInvoker {

    @Invoker("readAdditionalSaveData")
    void invokeReadAdditionalSaveData(ValueInput valueInput);
}
