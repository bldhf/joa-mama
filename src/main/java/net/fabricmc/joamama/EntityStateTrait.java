package net.fabricmc.joamama;

import com.google.common.collect.SetMultimap;
import net.fabricmc.joamama.entity.EntityState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Function;

public class EntityStateTrait<T> extends StateTrait<EntityType<?>, T>{
    private final Function<Entity, T> func;

    public EntityStateTrait (String id, String name, String desc, Function<Entity, T> func) {
        super(id, name, desc);
        this.func = func;
    }

    public void load(SetMultimap<EntityType<?>, EntityState> entries) {
        entries.forEach((owner, state) -> this.entries.put(owner, new SimpleState(state.getEntries()), func.apply(state.entity())));
        this.simplify();
        this.setDefault();
    }
}
