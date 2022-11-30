package net.fabricmc.joamama;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static net.fabricmc.joamama.BlockStateTraits.addBlockTagProperties;

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

	public static void onWorldLoadOrSumthn() {
		LOGGER.info("YOOOOOOOOOOOOOOOOOOOOO");

		addBlockTagProperties(output, BlockTags.class);

		save();
	}

	private static void save() {
		try (Writer writer = Files.newBufferedWriter(OUTPUT_PATH)) {
			writer.write("[\n" + String.join(",\n", output) + "\n]"); // scuffed but I didn't find a better solution lol
			writer.flush();
			LOGGER.info("Output written to file");
		} catch (IOException e) {
			LOGGER.error("Failed to save output", e);
		}
	}
}
