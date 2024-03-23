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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JoaMama implements ModInitializer {
	// haha, get it? it's like joe mama except with joa! XD
	public static final Logger LOGGER = LoggerFactory.getLogger("JOA MAMA");
	private static final Path OUTPUT_PATH = FabricLoader.getInstance().getConfigDir().resolve("output").resolve("output.json");
	private static final List<SimpleTrait<BlockState, ?>> outputBlockState = new ArrayList<>();
	private static final List<StateTrait<EntityType<?>, ?>> outputEntity = new ArrayList<>();
	private static final List<SimpleTrait<Biome, ?>> outputBiome = new ArrayList<>();

	@Override
	public void onInitialize () {
		LOGGER.info("onInitialize called");
		BlockStateTraits.load(BuiltInRegistries.BLOCK);
		BlockStateTraits.getTheWholeThing(outputBlockState);
		//BlockStateTraits.getInstantUpdaterStuff(outputBlockState);

		save("blockstate", "hardness", "exists_as_item", "supports_redstone_dust");
	}

	public static void onReloadResources (IntegratedServer server, ServerLevel world, Registry<Biome> biomes, Minecraft client, ClientLevel clientWorld, ClientPacketListener networkHandler, StatsCounter stats, ClientRecipeBook recipeBook) {
		LOGGER.info("World load mixin call successful!");

		BlockStateTraits.addBlockTagProperties(outputBlockState, BlockTags.class);

		EntityStateManager.load(world);
		EntityState.load(server, world, client, clientWorld, networkHandler, stats, recipeBook);
		EntityTraits.load(BuiltInRegistries.ENTITY_TYPE);
		EntityTraits.getTheWholeThing(outputEntity);

		BiomeTraits.load(biomes);
		BiomeTraits.getTheWholeThing(outputBiome);

		save("entity", "hurt_by_water", "fire_immune", "shearable");
	}

	private static void save () {
		save(null);
	}

	private static void save (String type, String ... ids) {
		List<String> outputString;
		if (type == null) {
			outputString = new ArrayList<>();
			outputString.addAll(outputBlockState.stream().map(Trait::toString).toList());
			outputString.addAll(outputEntity.stream().map(Trait::toString).toList());
			outputString.addAll(outputBiome.stream().map(Trait::toString).toList());
		} else {
			List<? extends Trait> output;
			switch (type) {
				case "blockstate" -> output = outputBlockState;
				case "entity" -> output = outputEntity;
				case "biome" -> output = outputBiome;
				default -> throw new IllegalStateException("What do you mean you want me to save type " + type + "???");
			}
			if (ids.length > 0) {
				List<String> idList = Arrays.asList(ids);
				outputString = output.stream().filter(trait -> idList.contains(trait.getId())).map(Trait::toString).toList();
			} else {
				outputString = output.stream().map(Trait::toString).toList();
			}
		}
		try (Writer writer = Files.newBufferedWriter(OUTPUT_PATH)) {
			writer.write("[\n" + String.join(",\n", outputString) + "\n]"); // scuffed but I didn't find a better solution lol
			writer.flush();
			LOGGER.info("Output written to file");
		} catch (IOException e) {
			LOGGER.error("Failed to save output", e);
		}
	}
}
