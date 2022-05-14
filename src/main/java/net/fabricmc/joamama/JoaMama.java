package net.fabricmc.joamama;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Material;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoaMama implements ModInitializer {
	// haha, get it? it's like joe mama except with joa! XD
	public static final Logger LOGGER = LoggerFactory.getLogger("JOA MAMA");
	private static final Path OUTPUT_PATH = FabricLoader.getInstance().getConfigDir().resolve("output").resolve("output.json");

	@Override
	public void onInitialize () {
		BlockStateTraits.load(Registry.BLOCK);

		ArrayList<String> arr = new ArrayList<>();


		arr.add(BlockStateTraits.air().toString());
		// arr.add(BlockStateTraits.getFluidState().toString());
		arr.add(BlockStateTraits.hardness().toString());
		arr.add(BlockStateTraits.luminance().toString());
		arr.add(BlockStateTraits.material().toString());
		arr.add(BlockStateTraits.mapColor().toString());
		arr.add(BlockStateTraits.opaque().toString());
		arr.add(BlockStateTraits.pistonBehavior().toString());
		arr.add(BlockStateTraits.hasRandomTicks().toString());
		arr.add(BlockStateTraits.solidBlock().toString());
		arr.add(BlockStateTraits.shouldSuffocate().toString());
		arr.add(BlockStateTraits.translucent().toString());
		arr.add(BlockStateTraits.opacity().toString());

		arr.add(BlockStateTraits.bottomFaceHasFullSquare().toString());
		arr.add(BlockStateTraits.topFaceHasFullSquare().toString());
		arr.add(BlockStateTraits.northFaceHasFullSquare().toString());
		arr.add(BlockStateTraits.southFaceHasFullSquare().toString());
		arr.add(BlockStateTraits.westFaceHasFullSquare().toString());
		arr.add(BlockStateTraits.eastFaceHasFullSquare().toString());
		arr.add(BlockStateTraits.topFaceHasRim().toString());
		arr.add(BlockStateTraits.bottomFaceHasSmallSquare().toString());
		arr.add(BlockStateTraits.topFaceHasSmallSquare().toString());


		arr.add(BlockStateTraits.wireConnectsNorth().toString());

		try (Writer writer = Files.newBufferedWriter(OUTPUT_PATH)) {
			writer.write("[\n" + String.join(",\n", arr) + "\n]"); // scuffed but I didn't find a better solution lol
			writer.flush();
		} catch (IOException e) {
			LOGGER.error("Failed to save output", e);
		}
	}
}
