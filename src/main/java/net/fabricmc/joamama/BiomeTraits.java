package net.fabricmc.joamama;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.joamama.gson.TraitsGson;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registry;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weight;
import net.minecraft.util.collection.Weighted;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BiomeTraits {
    private static Registry<Biome> biomes;

    public static void load (Registry<Biome> biomes) {
        BiomeTraits.biomes = biomes;
    }

    record SpawnEntry (@Expose int weight, @Expose int min, @Expose int max) {
        private static final Gson GSON = TraitsGson.gson();
        public String toString () {
            return GSON.toJson(this);
        }
    }

    private static Map<EntityType<?>, SpawnEntry> spawnPoolToMap (Pool<SpawnSettings.SpawnEntry> pool) {
        Map<EntityType<?>, SpawnEntry> map = new HashMap<>();
        pool.getEntries().forEach(entry -> map.put(entry.type, new SpawnEntry(entry.getWeight().getValue(), entry.minGroupSize, entry.maxGroupSize)));
        return map;
    }

    public static ArrayList<String> getTheWholeThing () {
        return new ArrayList<>(
                List.of(
                        new JoaProperty<>(
                                "spawn_entries_monster",
                                "Spawn Entries (Monster)",
                                "",
                                biome -> spawnPoolToMap(biome.getSpawnSettings().getSpawnEntries(SpawnGroup.MONSTER)),
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_creature",
                                "Spawn Entries (Creature)",
                                "",
                                biome -> spawnPoolToMap(biome.getSpawnSettings().getSpawnEntries(SpawnGroup.CREATURE)),
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_ambient",
                                "Spawn Entries (Ambient)",
                                "",
                                biome -> spawnPoolToMap(biome.getSpawnSettings().getSpawnEntries(SpawnGroup.AMBIENT)),
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_monster",
                                "Spawn Entries (Axolotls)",
                                "",
                                biome -> spawnPoolToMap(biome.getSpawnSettings().getSpawnEntries(SpawnGroup.AXOLOTLS)),
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_underground_water_creature",
                                "Spawn Entries (Underground Water Creature)",
                                "",
                                biome -> spawnPoolToMap(biome.getSpawnSettings().getSpawnEntries(SpawnGroup.UNDERGROUND_WATER_CREATURE)),
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_water_creature",
                                "Spawn Entries (Water Creature)",
                                "",
                                biome -> spawnPoolToMap(biome.getSpawnSettings().getSpawnEntries(SpawnGroup.WATER_CREATURE)),
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_water_ambient",
                                "Spawn Entries (Water Ambient)",
                                "",
                                biome -> spawnPoolToMap(biome.getSpawnSettings().getSpawnEntries(SpawnGroup.WATER_AMBIENT)),
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        /*new JoaProperty<>(
                                "precipitation",
                                "Precipitation",
                                "The default precipitation type of this biome.",
                                Biome::getPrecipitation,
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),*/
                        /*new JoaProperty<>(
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
                        ).toString(),*/
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
