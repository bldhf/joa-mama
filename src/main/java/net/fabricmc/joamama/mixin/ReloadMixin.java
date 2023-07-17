package net.fabricmc.joamama.mixin;

import net.fabricmc.joamama.JoaMama;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin (MinecraftServer.class)
public abstract class ReloadMixin {

    @Inject (
            method = "reloadResources",
            at = @At("RETURN")
    )
    public void onReloadResources(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if ((Object) this instanceof IntegratedServer server) {
            ServerLevel world = server.getLevel(Level.OVERWORLD);

            Registry<Biome> biomes = server.registries().compositeAccess().registryOrThrow(Registries.BIOME);

            Minecraft client = ((IntegratedServerAccessor) server).getMinecraft();
            ClientLevel clientWorld = client.level;
            ClientPacketListener networkHandler = client.getConnection();
            LocalPlayer player = client.player;
            StatsCounter stats = player.getStats();
            ClientRecipeBook recipeBook = player.getRecipeBook();

            JoaMama.onWorldLoadOrSumthn(server, world, biomes, client, clientWorld, networkHandler, stats, recipeBook);
        }
    }

}
