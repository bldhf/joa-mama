package net.fabricmc.joamama;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.joamama.gson.TraitsGson;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
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

    private static Map<EntityType<?>, SpawnEntry> spawnPoolToMap (WeightedRandomList<MobSpawnSettings.SpawnerData> pool) {
        Map<EntityType<?>, SpawnEntry> map = new HashMap<>();
        pool.unwrap().forEach(entry -> map.put(entry.type, new SpawnEntry(entry.getWeight().asInt(), entry.minCount, entry.maxCount)));
        return map;
    }

    public static ArrayList<String> getTheWholeThing () {
        return new ArrayList<>(
                List.of(
                        new JoaProperty<>(
                                "spawn_entries_monster",
                                "Spawn Entries (Monster)",
                                "",
                                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.MONSTER)),
                                biomes,
                                biome -> String.valueOf(biomes.getKey(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_creature",
                                "Spawn Entries (Creature)",
                                "",
                                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.CREATURE)),
                                biomes,
                                biome -> String.valueOf(biomes.getKey(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_ambient",
                                "Spawn Entries (Ambient)",
                                "",
                                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.AMBIENT)),
                                biomes,
                                biome -> String.valueOf(biomes.getKey(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_monster",
                                "Spawn Entries (Axolotls)",
                                "",
                                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.AXOLOTLS)),
                                biomes,
                                biome -> String.valueOf(biomes.getKey(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_underground_water_creature",
                                "Spawn Entries (Underground Water Creature)",
                                "",
                                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.UNDERGROUND_WATER_CREATURE)),
                                biomes,
                                biome -> String.valueOf(biomes.getKey(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_water_creature",
                                "Spawn Entries (Water Creature)",
                                "",
                                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.WATER_CREATURE)),
                                biomes,
                                biome -> String.valueOf(biomes.getKey(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "spawn_entries_water_ambient",
                                "Spawn Entries (Water Ambient)",
                                "",
                                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.WATER_AMBIENT)),
                                biomes,
                                biome -> String.valueOf(biomes.getKey(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "default_precipitation",
                                "Default Precipitation",
                                "The default precipitation type of this biome.",
                                biome -> !biome.hasPrecipitation()
                                    ? Biome.Precipitation.NONE
                                    : biome.coldEnoughToSnow(BlockPos.ZERO) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN,
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "snow_height",
                                "Snow Height",
                                "The approximate height at which it starts snowing. Though unlikely, this can fluctuate by up to 8 blocks.",
                                biome -> switch (biome.getPrecipitationAt(BlockPos.ZERO)) {
                                    case NONE -> "N/A";
                                    case SNOW -> "ALL";
                                    case RAIN -> biome.getBaseTemperature() < 0.15 ? "ALL" : (int) (biome.getBaseTemperature() * 800 - 40);
                                },
                                biomes,
                                biome -> String.valueOf(biomes.getId(biome))
                        ).toString(),
                        new JoaProperty<>(
                                "temperature",
                                "Base Temperature",
                                "",
                                Biome::getBaseTemperature,
                                biomes,
                                biome -> String.valueOf(biomes.getKey(biome))
                        ).toString()
                )
        );
    }
}
