package net.fabricmc.joamama;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import net.fabricmc.joamama.gson.TraitsGson;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static void getTheWholeThing (List<SimpleTrait<Biome, ?>> arr) {
        arr.add(new SimpleTrait<>(
                "spawn_entries_monster",
                "Spawn Entries (Monster)",
                "",
                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.MONSTER)),
                biome -> String.valueOf(biomes.getKey(biome)),
                biomes));
        arr.add(new SimpleTrait<>(
                "spawn_entries_creature",
                "Spawn Entries (Creature)",
                "",
                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.CREATURE)),
                biome -> String.valueOf(biomes.getKey(biome)),
                biomes));
        arr.add(new SimpleTrait<>(
                "spawn_entries_ambient",
                "Spawn Entries (Ambient)",
                "",
                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.AMBIENT)),
                biome -> String.valueOf(biomes.getKey(biome)),
                biomes));
        arr.add(new SimpleTrait<>(
                "spawn_entries_monster",
                "Spawn Entries (Axolotls)",
                "",
                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.AXOLOTLS)),
                biome -> String.valueOf(biomes.getKey(biome)),
                biomes));
        arr.add(new SimpleTrait<>(
                "spawn_entries_underground_water_creature",
                "Spawn Entries (Underground Water Creature)",
                "",
                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.UNDERGROUND_WATER_CREATURE)),
                biome -> String.valueOf(biomes.getKey(biome)),
                biomes));
        arr.add(new SimpleTrait<>(
                "spawn_entries_water_creature",
                "Spawn Entries (Water Creature)",
                "",
                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.WATER_CREATURE)),
                biome -> String.valueOf(biomes.getKey(biome)),
                biomes));
        arr.add(new SimpleTrait<>(
                "spawn_entries_water_ambient",
                "Spawn Entries (Water Ambient)",
                "",
                biome -> spawnPoolToMap(biome.getMobSettings().getMobs(MobCategory.WATER_AMBIENT)),
                biome -> String.valueOf(biomes.getKey(biome)),
                biomes));
        arr.add(new SimpleTrait<>(
                "default_precipitation",
                "Default Precipitation",
                "The default precipitation type of this biome.",
                biome -> !biome.hasPrecipitation()
                    ? Biome.Precipitation.NONE
                    : biome.coldEnoughToSnow(BlockPos.ZERO) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN,
                biome -> String.valueOf(biomes.getId(biome)),
                biomes));
        arr.add(new SimpleTrait<>(
                "snow_height",
                "Snow Height",
                "The approximate height at which it starts snowing. Though unlikely, this can fluctuate by up to 8 blocks.",
                biome -> switch (biome.getPrecipitationAt(BlockPos.ZERO)) {
                    case NONE -> "N/A";
                    case SNOW -> "ALL";
                    case RAIN -> biome.getBaseTemperature() < 0.15 ? "ALL" : (int) (biome.getBaseTemperature() * 800 - 40);
                },
                biome -> String.valueOf(biomes.getId(biome)),
                biomes));
        arr.add(new SimpleTrait<>(
                "temperature",
                "Base Temperature",
                "",
                Biome::getBaseTemperature,
                biome -> String.valueOf(biomes.getKey(biome)),
                biomes));
    }
}
