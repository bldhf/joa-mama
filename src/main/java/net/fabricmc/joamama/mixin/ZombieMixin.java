package net.fabricmc.joamama.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {
    public ZombieMixin(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract boolean isBaby();

    @Inject(method = "getBaseExperienceReward", at = @At("HEAD"), cancellable = true)
    public void getExperienceReward(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((this.isBaby() ? (int)((double)this.xpReward * 1.5) : 0) + super.getBaseExperienceReward());
    }
}
