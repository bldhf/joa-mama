package net.fabricmc.joamama;

import com.google.common.collect.SetMultimap;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.joamama.entity.EntityState;
import net.fabricmc.joamama.entity.EntityStateManager;
import net.fabricmc.joamama.entity.EntityTraits;
import net.fabricmc.joamama.gson.TraitsGson;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
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

// TODO | Replace copied code segments with actual calls to the methods used. Use mixins/mock worlds?
// TODO | 5/29/2024 | A lot of the basic parts of this project are in dire need of a rewrite. I was (compared to my other languages) inexperienced in Java when I started this project and there's a ton of messy, inconsistent, unnecessary, or just plain bad code here.

public class JoaMama implements ModInitializer {
	// haha, get it? it's like joe mama except with joa! XD
	public static final Logger LOGGER = LoggerFactory.getLogger("JOA MAMA");
	private static final Path OUTPUT_PATH = FabricLoader.getInstance().getConfigDir().resolve("output").resolve("output.json");
	private static boolean CALLED_ON_RELOAD_RESOURCES = false;
	private static final TraitCollection<SimpleTrait<BlockState, ?>, Iterable<BlockState>> blockStateTraits = new TraitCollection<>(SimpleTrait::load);
	private static final TraitCollection<EntityStateTrait<?>, SetMultimap<EntityType<?>, EntityState>> entityTraits = new TraitCollection<>(EntityStateTrait::load);
	private static final TraitCollection<SimpleTrait<Biome, ?>, Registry<Biome>> biomeTraits = new TraitCollection<>(SimpleTrait::load);
	private static final TraitCollection<SimpleTrait<Item, ?>, Registry<Item>> itemTraits = new TraitCollection<>(SimpleTrait::load);
	private static final Map<String, TraitCollection<?, ?>> traits = Map.of(
		"blockstate", blockStateTraits,
		"entity", entityTraits,
		"biome", biomeTraits,
		"item", itemTraits
	);
	public static Level LEVEL;
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_TYPES = (context, builder) -> SharedSuggestionProvider.suggest(traits.keySet(), builder);
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

		BlockStateTraits.load(blockStateTraits);
		BlockStateTraits.getTheWholeThing(blockStateTraits);
		//BlockStateTraits.getInstantUpdaterStuff(blockStateTraits);

		ItemTraits.load(itemTraits, BuiltInRegistries.ITEM);
		ItemTraits.getTheWholeThing(itemTraits);

		registerSaveCommand();
		LOGGER.info("onInitialize finished");
		save("blockstate", "movable");
	}

	public static Collection<String> getTypes() {
		return traits.keySet();
	}

	public static Set<String> getTraitIds(String type) {
		switch (type) {
			case "blockstate" -> {return blockStateTraits.getIds();}
			case "entity" -> {return entityTraits.getIds();}
			case "biome" -> {return biomeTraits.getIds();}
			case "item" -> {return itemTraits.getIds();}
			default -> {return new HashSet<>();}
		}
	}

	public static void onReloadResources(IntegratedServer server, ServerLevel level, Registry<Biome> biomes, Minecraft client, ClientLevel clientLevel, ClientPacketListener connection, StatsCounter stats, ClientRecipeBook recipeBook) {
		LOGGER.info("World load mixin call successful!");

		LEVEL = level;

		if (!CALLED_ON_RELOAD_RESOURCES) {
			BlockStateTraits.addBlockTagProperties(blockStateTraits, BlockTags.class);

			EntityStateManager.load(level);
			EntityState.load(server, level, client, clientLevel, connection, stats, recipeBook);
			EntityTraits.load(entityTraits, BuiltInRegistries.ENTITY_TYPE, BuiltInRegistries.MOB_EFFECT, level);
			EntityTraits.getTheWholeThing(entityTraits);
			EntityTraits.getDamageImmunities(entityTraits);

			BiomeTraits.load(biomeTraits, biomes);
			BiomeTraits.getTheWholeThing(biomeTraits);

			CALLED_ON_RELOAD_RESOURCES = true;
			LOGGER.info("Finished loading traits");
		}

		save("blockstate");
	}

	private static int save(String type, String ... ids) {
		TraitCollection<?, ?> traitCollection;
		switch (type) {
			case "blockstate" -> traitCollection = blockStateTraits;
			case "entity" -> traitCollection = entityTraits;
			case "biome" -> traitCollection = biomeTraits;
			case "item" -> traitCollection = itemTraits;
			default -> {
				LOGGER.error("Unrecognized type \"" + type + "\"");
				return 0;
			}
		}
		Set<String> idSet;
		if (ids.length > 0) {
			idSet = new HashSet<>(Arrays.asList(ids));
			idSet.retainAll(traitCollection.getIds());
		} else {
			idSet = traitCollection.getIds();
		}
		Map<String, Trait<?>> idSetTraits = new HashMap<>();
		for (String id : idSet) {
			idSetTraits.put(id, traitCollection.loadTrait(id));
		}
		try (Writer writer = Files.newBufferedWriter(OUTPUT_PATH)) {
			writer.write(TraitsGson.gson().toJson(idSetTraits));
			writer.flush();
			LOGGER.info("Output written to file");
			return idSet.size();
		} catch (IOException e) {
			LOGGER.error("Failed to save output", e);
			return 0;
		}
	}

	private static void registerSaveCommand() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			Commands.literal("save")
				.requires(commandSourceStack -> commandSourceStack.hasPermission(4))
				.then(
					Commands.argument("type", StringArgumentType.string())
						.suggests(SUGGEST_TYPES)
						.executes(context -> save(StringArgumentType.getString(context, "type")))
						.then(
							Commands.argument("traits", StringArgumentType.greedyString())
								.suggests(SUGGEST_TRAITS)
								.executes(context -> save(StringArgumentType.getString(context, "type"), StringArgumentType.getString(context, "traits").split(" ")))
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
