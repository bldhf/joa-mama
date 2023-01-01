package net.fabricmc.joamama;

import net.minecraft.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public abstract class BiomeTraits {
    private static Registry<Biome> biomes;

    public static void load (Registry<Biome> biomes) {
        BiomeTraits.biomes = biomes;
    }

    public static ArrayList<String> getTheWholeThing () {
        return new ArrayList<>(
                List.of(
                        new JoaProperty<>(
                                "precipitation",
                                "Precipitation",
                                "",
                                Biome::getPrecipitation,
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "snow_height",
                                "Snow Height",
                                "The approximate height at which it starts snowing. Though unlikely, this can fluctuate by up to 8 blocks.",
                                biome -> switch (biome.getPrecipitation()) {
                                    case NONE -> "N/A";
                                    case SNOW -> "ALL";
                                    case RAIN -> biome.getTemperature() < 0.15 ? "ALL" : (int) (biome.getTemperature() * 800 - 40);
                                },
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "temperature",
                                "Base Temperature",
                                "",
                                Biome::getTemperature,
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString()
                )
        );
    }
}
