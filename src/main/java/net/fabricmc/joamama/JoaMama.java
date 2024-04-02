package net.fabricmc.joamama;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.joamama.entity.EntityState;
import net.fabricmc.joamama.entity.EntityStateManager;
import net.fabricmc.joamama.entity.EntityTraits;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
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
import java.util.*;
import java.util.function.Supplier;

// FIXME | 4/2/2024 | Traits are processed when they're created, not when toString or some other method is called. Because of this, there's unnecessary lag when loading everything in, when it should be delayed until save() is called.

public class JoaMama implements ModInitializer {
	// haha, get it? it's like joe mama except with joa! XD
	public static final Logger LOGGER = LoggerFactory.getLogger("JOA MAMA");
	private static final Path OUTPUT_PATH = FabricLoader.getInstance().getConfigDir().resolve("output").resolve("output.json");
	private static boolean CALLED_ON_RELOAD_RESOURCES = false;
	private static final List<SimpleTrait<BlockState, ?>> outputBlockState = new ArrayList<>();
	private static final List<StateTrait<EntityType<?>, ?>> outputEntity = new ArrayList<>();
	private static final List<SimpleTrait<Biome, ?>> outputBiome = new ArrayList<>();
	private static final Map<String, List<? extends Trait>> outputs = Map.of(
		"blockstate", outputBlockState,
		"entity", outputEntity,
		"biome", outputBiome
	);
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_TYPES = (context, builder) -> SharedSuggestionProvider.suggest(outputs.keySet(), builder);
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_TRAITS = (context, builder) -> SharedSuggestionProvider.suggest(getTraitIds(StringArgumentType.getString(context, "type")), builder);
	/*private static final SuggestionProvider<CommandSourceStack> SUGGEST_TRAITS = (context, builder) -> {
		String type = StringArgumentType.getString(context, "type");
		if (!JoaMama.getTypes().contains(type)) {
			return Suggestions.empty();
		}
		// LOGGER.info(String.valueOf(builder.getStart()));
		// LOGGER.info(builder.getRemaining());
		builder.createOffset(builder.getStart() + builder.getRemaining().lastIndexOf(' ') + 1);
		// LOGGER.info(String.valueOf(builder.getStart()));
		for (String traitId : JoaMama.getTraitIds(type)) {
			if (SharedSuggestionProvider.matchesSubStr(builder.getRemaining().substring(builder.getRemaining().lastIndexOf(" ") + 1), traitId)) {
				builder.suggest(traitId);
			}
		}
		return builder.buildFuture();
	};*/

	@Override
	public void onInitialize() {
		LOGGER.info("onInitialize called");
		BlockStateTraits.load(BuiltInRegistries.BLOCK);
		BlockStateTraits.getTheWholeThing(outputBlockState);
		//BlockStateTraits.getInstantUpdaterStuff(outputBlockState);

		registerSaveCommand();
	}

	public static Collection<String> getTypes() {
		return outputs.keySet();
	}

	public static List<String> getTraitIds(String type) {
		switch (type) {
			case "blockstate" -> {return outputBlockState.stream().map(Trait::getId).toList();}
			case "entity" -> {return outputEntity.stream().map(Trait::getId).toList();}
			case "biome" -> {return outputBiome.stream().map(Trait::getId).toList();}
			default -> {return new ArrayList<>();}
		}
	}

	public static void onReloadResources(IntegratedServer server, ServerLevel level, Registry<Biome> biomes, Minecraft client, ClientLevel clientLevel, ClientPacketListener connection, StatsCounter stats, ClientRecipeBook recipeBook) {
		LOGGER.info("World load mixin call successful!");

		if (!CALLED_ON_RELOAD_RESOURCES) {
			BlockStateTraits.addBlockTagProperties(outputBlockState, BlockTags.class);

			EntityStateManager.load(level);
			EntityState.load(server, level, client, clientLevel, connection, stats, recipeBook);
			EntityTraits.load(BuiltInRegistries.ENTITY_TYPE, BuiltInRegistries.MOB_EFFECT, level);
			EntityTraits.getTheWholeThing(outputEntity);
			EntityTraits.getDamageImmunities(outputEntity);

			BiomeTraits.load(biomes);
			BiomeTraits.getTheWholeThing(outputBiome);

			CALLED_ON_RELOAD_RESOURCES = true;
			LOGGER.info("Finished loading traits");
		}

		// save("entity", "immune_to_arrows")
	}

	private static int save() {
		return save(null);
	}

	private static int save(String type, String ... ids) {
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
				default -> {
					LOGGER.error("Unrecognized type \"" + type + "\"");
					return 0;
				}
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
			return outputString.size();
		} catch (IOException e) {
			LOGGER.error("Failed to save output", e);
			return 0;
		}
	}

	private static void registerSaveCommand() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			Commands.literal("save")
				.requires(commandSourceStack -> commandSourceStack.hasPermission(4))
				.executes(context -> save())
				.then(
					Commands.argument("type", StringArgumentType.string())
						.suggests(SUGGEST_TYPES)
						.executes(context -> save(StringArgumentType.getString(context, "type")))
						.then(
							Commands.argument("traits", StringArgumentType.string())
								.suggests(SUGGEST_TRAITS)
								.executes(context -> save(StringArgumentType.getString(context, "type"), StringArgumentType.getString(context, "traits")))
						)

				)
		));
	}

	public static <T> Map<T, Integer> testRandom(Supplier<T> supplier, int count) {
		Map<T, Integer> multiset = new HashMap<>();
		for (int i = 0; i < count; i++) {
			T val = supplier.get();
			multiset.put(val, multiset.getOrDefault(val, 0) + 1);
		}
		return multiset;
	}
}
