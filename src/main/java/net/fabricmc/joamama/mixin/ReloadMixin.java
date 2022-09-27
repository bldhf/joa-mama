package net.fabricmc.joamama.mixin;

import com.mojang.datafixers.DataFixer;
import net.fabricmc.joamama.JoaMama;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.ApiServices;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class ReloadMixin
extends ReentrantThreadExecutor<ServerTask>
implements CommandOutput,
AutoCloseable {

    public ReloadMixin(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super("server");
    }

    @Inject(
            method = "reloadResources",
            at = @At("RETURN"),
            cancellable = true
    )
    public void onReloadResources(CallbackInfoReturnable<String> cir) {
        JoaMama.onWorldLoadOrSumthn();
    }

}
