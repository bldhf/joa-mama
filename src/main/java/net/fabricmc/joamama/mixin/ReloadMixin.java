package net.fabricmc.joamama.mixin;

import com.mojang.datafixers.DataFixer;
import net.fabricmc.joamama.JoaMama;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.*;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.ApiServices;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin (MinecraftServer.class)
public abstract class ReloadMixin {

    @Inject (
            method = "reloadResources",
            at = @At("RETURN")
    )
    public void onReloadResources (CallbackInfoReturnable<String> cir) {
        if ((Object) this instanceof IntegratedServer server) {
            ServerWorld world = server.getWorld(World.OVERWORLD);

            Registry<Biome> biomes = server.getCombinedDynamicRegistries().getCombinedRegistryManager().get(RegistryKeys.BIOME);

            MinecraftClient client = ((IntegratedServerAccessor) server).getClient();
            ClientWorld clientWorld = client.world;
            ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
            ClientPlayerEntity player = client.player;
            StatHandler stats = player.getStatHandler();
            ClientRecipeBook recipeBook = player.getRecipeBook();

            JoaMama.onWorldLoadOrSumthn(server, world, biomes, client, clientWorld, networkHandler, stats, recipeBook);
        }
    }

}
