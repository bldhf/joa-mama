package net.fabricmc.joamama;


import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.joamama.dummy.DummyBlockView;
import net.minecraft.block.*;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoaMama implements ModInitializer {
	// haha, get it? it's like joe mama except with joa! XD
	public static final Logger LOGGER = LoggerFactory.getLogger("JOA MAMA");

	StateTrait<Block, BlockState, Float> HARDNESS;
	StateTrait<Block, BlockState, Integer> LUMINANCE;
	StateTrait<Block, BlockState, PistonBehavior> PISTON_BEHAVIOR;

	@Override
	public void onInitialize () {

		SetMultimap<Block, BlockState> blockStates = MultimapBuilder.hashKeys().hashSetValues().build();
		Registry.BLOCK.forEach(block -> blockStates.putAll(block, block.getStateManager().getStates()));

		HARDNESS = new StateTrait<>(
				"hardness",
				"Hardness",
				"",
				blockState -> blockState.getHardness(new DummyBlockView(blockState), BlockPos.ORIGIN),
				blockStates
		);
		LUMINANCE = new StateTrait<>(
				"luminance",
				"Luminance",
				"",
				AbstractBlock.AbstractBlockState::getLuminance,
				blockStates
		);
		PISTON_BEHAVIOR = new StateTrait<>(
				"piston_behavior",
				"Piston Behavior",
				"",
				AbstractBlock.AbstractBlockState::getPistonBehavior,
				blockStates
		);

		HARDNESS.simplify();
		LUMINANCE.simplify();
		PISTON_BEHAVIOR.simplify();

		LOGGER.info(HARDNESS.toString());
		LOGGER.info(LUMINANCE.toString());
		LOGGER.info(PISTON_BEHAVIOR.toString());
	}
}
