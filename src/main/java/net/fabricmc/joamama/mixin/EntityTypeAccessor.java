package net.fabricmc.joamama.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin (EntityType.class)
public interface EntityTypeAccessor {
    @Accessor
    ImmutableSet<Block> getCanSpawnInside ();
}
