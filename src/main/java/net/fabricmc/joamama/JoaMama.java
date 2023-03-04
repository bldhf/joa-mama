package net.fabricmc.joamama;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.joamama.entity.EntityState;
import net.fabricmc.joamama.entity.EntityStateManager;
import net.fabricmc.joamama.entity.EntityTraits;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.StatHandler;
import net.minecraft.world.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class JoaMama implements ModInitializer {
	// haha, get it? it's like joe mama except with joa! XD
	public static final Logger LOGGER = LoggerFactory.getLogger("JOA MAMA");
	private static final Path OUTPUT_PATH = FabricLoader.getInstance().getConfigDir().resolve("output").resolve("output.json");
	private static final ArrayList<String> output = new ArrayList<>();

	@Override
	public void onInitialize () {
		BlockStateTraits.load(Registries.BLOCK);
		output.addAll(BlockStateTraits.getTheWholeThing());

		save();
	}

	public static void onWorldLoadOrSumthn (IntegratedServer server, ServerWorld world, Registry<Biome> biomes, MinecraftClient client, ClientWorld clientWorld, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook) {
//	public static void onWorldLoadOrSumthn () {
		LOGGER.info("YOOOOOOOOOOOOOOOOOOOOO");

		BlockStateTraits.addBlockTagProperties(output, BlockTags.class);

		//EntityStateManager.load(world);
		//EntityState.load(server, world, client, clientWorld, networkHandler, stats, recipeBook);
		//EntityTraits.load(Registries.ENTITY_TYPE);
		//output.addAll(EntityTraits.getTheWholeThing());

//		BiomeTraits.load(biomes);
//		output.addAll(BiomeTraits.getTheWholeThing());

		save();
	}

	private static void save () {
		try (Writer writer = Files.newBufferedWriter(OUTPUT_PATH)) {
			writer.write("[\n" + String.join(",\n", output) + "\n]"); // scuffed but I didn't find a better solution lol
			writer.flush();
			LOGGER.info("Output written to file");
		} catch (IOException e) {
			LOGGER.error("Failed to save output", e);
		}
	}
}
