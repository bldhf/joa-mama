package net.fabricmc.joamama;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoaMama implements ModInitializer {
	// haha, get it? it's like joe mama except with joa! XD
	public static final Logger LOGGER = LoggerFactory.getLogger("JOA MAMA");

	@Override
	public void onInitialize () {
		BlockStateTraits.load(Registry.BLOCK);

		LOGGER.info(BlockStateTraits.sideCoversSmallSquareUp().toString());
	}
}
