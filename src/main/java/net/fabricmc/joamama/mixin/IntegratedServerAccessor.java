package net.fabricmc.joamama.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin (IntegratedServer.class)
public interface IntegratedServerAccessor {
    @Accessor
    MinecraftClient getClient ();
}
