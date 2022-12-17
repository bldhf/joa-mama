package net.fabricmc.joamama.entity;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.fabricmc.joamama.StateTrait;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityTraits {
    private static final EntityGroup EntityGroup_NONE = new EntityGroup();
    private static final SetMultimap<EntityType<?>, EntityState> entityStates;

    static {
        entityStates = MultimapBuilder.hashKeys().hashSetValues().build();
    }

    public static void load (Iterable<EntityType<?>> entityTypes) {
        entityTypes.forEach(type -> entityStates.putAll(type, EntityStateManager.getEntityStateManager(type).getStates()));
    }

    public static ArrayList<String> getTheWholeThing () {
        return new ArrayList<>(
                List.of(
                        new StateTrait<>(
                                "width",
                                "Width",
                                "",
                                Entity::getWidth,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "height",
                                "Height",
                                "",
                                Entity::getHeight,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "bucketable",
                                "Bucketable",
                                "",
                                entity -> entity instanceof Bucketable,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "entity_group",
                                "Entity Group",
                                "",
                                entity -> entity instanceof LivingEntity ? ((LivingEntity) entity).getGroup() : EntityGroup_NONE,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "eye_height",
                                "Eye Height",
                                "",
                                entity -> entity.getEyeHeight(entity.getPose()),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "fire_immune",
                                "Fire Immune",
                                "",
                                Entity::isFireImmune,
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "leashable",
                                "Leashable",
                                "",
                                entity -> entity instanceof MobEntity && ((MobEntity) entity).canBeLeashedBy(null),
                                entityStates
                        ).toString(),
                        new StateTrait<>(
                                "step_height",
                                "Step Height",
                                "",
                                entity -> entity.stepHeight,
                                entityStates
                        ).toString()
                )
        );
    }
}
