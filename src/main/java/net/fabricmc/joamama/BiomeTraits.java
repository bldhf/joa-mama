package net.fabricmc.joamama;

import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Vector;

public abstract class BiomeTraits {
    private static final Vector<?> biomes;

    static {
        biomes = new Vector<>();
    }

    public static void load (Iterable<Biome> biomes) {
    }

    public static ArrayList<String> getTheWholeThing () {
        return null;
    }
}
