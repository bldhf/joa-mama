package net.fabricmc.joamama;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.joamama.entity.EntityState;
import net.fabricmc.joamama.entity.EntityStateManager;
import net.fabricmc.joamama.entity.EntityTraits;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
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
		LOGGER.info("onInitialize called");
		BlockStateTraits.load(BuiltInRegistries.BLOCK);
		output.addAll(BlockStateTraits.getTheWholeThing());

		save();
	}

	public static void onWorldLoadOrSumthn (IntegratedServer server, ServerLevel world, Registry<Biome> biomes, Minecraft client, ClientLevel clientWorld, ClientPacketListener networkHandler, StatsCounter stats, ClientRecipeBook recipeBook) {
//	public static void onWorldLoadOrSumthn () {
		LOGGER.info("World load mixin call successful!");

		BlockStateTraits.addBlockTagProperties(output, BlockTags.class);

//		EntityStateManager.load(world);
//		EntityState.load(server, world, client, clientWorld, networkHandler, stats, recipeBook);
//		EntityTraits.load(BuiltInRegistries.ENTITY_TYPE);
//		output.addAll(EntityTraits.getTheWholeThing());

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
