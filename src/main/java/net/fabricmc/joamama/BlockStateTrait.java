package net.fabricmc.joamama;

import com.google.common.collect.SetMultimap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BlockStateTrait<T> extends StateTrait<Block, T> {
    private final Function<BlockState, T> func;

    public BlockStateTrait(String id, String name, String desc, String definition, Function<BlockState, T> func) {
        super(id, name, desc, definition);
        this.func = func;
    }

    public void load(SetMultimap<Block, BlockState> entries) {
        for (Block block : entries.keySet()) {
            for (BlockState state : entries.get(block)) {
                Map<Property<?>, Comparable<?>> map = new HashMap<>();
                for (Property<?> property : state.getProperties()) {
                    map.put(property, state.getValue(property));
                }
                SimpleState simpleState = new SimpleState(map);
                this.entries.put(block, simpleState, func.apply(state));
            }
        }
        this.simplify();
        this.setDefault();
    }

}