package net.fabricmc.joamama;

import com.google.common.collect.Multimap;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;

import java.util.Collection;

public class ItemTraits {
    //private static Registry<Item> items;

    public static void load(TraitCollection<SimpleTrait<Item, ?>, Registry<Item>> traits, Registry<Item> items) {
        traits.load(items);
        //ItemTraits.items = items;
    }

    private static double getAdditionModifiers(Item item, Attribute attribute, EquipmentSlot slot, double base) {
        Multimap<Attribute, AttributeModifier> attributes = item.getDefaultAttributeModifiers(slot);
        if (attributes.containsKey(attribute)) {
            Collection<AttributeModifier> modifiers = attributes.get(attribute);
            for (AttributeModifier modifier : modifiers) {
                if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                    base += modifier.getAmount();
                } else {
                    JoaMama.LOGGER.error("Expected addition modifier, got " + modifier.getOperation().name());
                    return Double.NaN;
                }
            }
        }
        return base;
    }

    public static void getTheWholeThing(TraitCollection<SimpleTrait<Item, ?>, Registry<Item>> traits) {
        traits.add(new SimpleTrait<>(
            "stack_size",
            "Stack Size",
            "",
            "",
            Item::getMaxStackSize
        ));
        traits.add(new SimpleTrait<>(
            "nutrition",
            "Hunger",
            "The amount of hunger granted by eating this item.",
            "",
            item -> item.isEdible() ? item.getFoodProperties().getNutrition() : "Inedible"
        ));
        traits.add(new SimpleTrait<>(
            "saturation",
            "Saturation",
            "The amount of saturation granted by eating this item.",
            "",
            item -> item.isEdible() ? item.getFoodProperties().getNutrition() * item.getFoodProperties().getSaturationModifier() * 2 : "Inedible"
        ));
        traits.add(new SimpleTrait<>(
            "attack_speed",
            "Attack Speed",
            "",
            "",
            item -> getAdditionModifiers(item, Attributes.ATTACK_SPEED, EquipmentSlot.MAINHAND, 4)
        ));
        traits.add(new SimpleTrait<>(
            "attack_damage",
            "Attack Damage",
            "",
            "",
            item -> getAdditionModifiers(item, Attributes.ATTACK_DAMAGE, EquipmentSlot.MAINHAND, 1)
        ));
        traits.add(new SimpleTrait<>(
            "damage_per_second",
            "Damage Per Second",
            "",
            "",
            item -> getAdditionModifiers(item, Attributes.ATTACK_DAMAGE, EquipmentSlot.MAINHAND, 1) * getAdditionModifiers(item, Attributes.ATTACK_SPEED, EquipmentSlot.MAINHAND, 1)
        ));
        /*traits.add(new SimpleTrait<>(
            "breaking_speed",
            "Breaking Speed",
            "",
            "",
            item -> getAdditionModifiers(item, Attributes., EquipmentSlot.MAINHAND, 1)
        ));*/
        // TODO | 5/30/2024 | Add breaking and smelting properties
    }
}
