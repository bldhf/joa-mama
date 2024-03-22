package net.fabricmc.joamama.mock;

import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public record MockMultiBlockLevelReader (Map<BlockPos, BlockState> blockStateMap) implements LevelReader {
    @Deprecated
    public ChunkAccess getChunk (int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        throw new AssertionError();
    }

    @Deprecated
    public boolean hasChunk (int chunkX, int chunkZ) {
        throw new AssertionError();
    }

    @Deprecated
    public int getHeight (Heightmap.Types heightmap, int x, int z) {
        throw new AssertionError();
    }

    @Deprecated
    public int getSkyDarken () {
        throw new AssertionError();
    }

    @Deprecated
    public BiomeManager getBiomeManager () {
        throw new AssertionError();
    }

    @Deprecated
    public Holder<Biome> getUncachedNoiseBiome (int biomeX, int biomeY, int biomeZ) {
        throw new AssertionError();
    }

    @Deprecated
    public boolean isClientSide () {
        throw new AssertionError();
    }

    @Deprecated
    public int getSeaLevel() {
        throw new AssertionError();
    }

    @Deprecated
    public DimensionType dimensionType () {
        throw new AssertionError();
    }

    @Deprecated
    public RegistryAccess registryAccess() {
        throw new AssertionError();
    }

    @Deprecated
    public FeatureFlagSet enabledFeatures() {
        throw new AssertionError();
    }

    @Deprecated
    public float getShade (Direction direction, boolean shaded) {
        throw new AssertionError();
    }

    @Deprecated
    public LevelLightEngine getLightEngine () {
        throw new AssertionError();
    }

    public WorldBorder getWorldBorder () { return new WorldBorder(); }

    @Deprecated
    public List<VoxelShape> getEntityCollisions (Entity entity, AABB box) {
        throw new AssertionError();
    }

    public BlockEntity getBlockEntity (BlockPos pos) {
        return null;
    }

    public BlockState getBlockState (BlockPos pos) {
        return this.blockStateMap.get(pos);
    }

    public FluidState getFluidState (BlockPos pos) {
        return this.blockStateMap.get(pos).getFluidState();
    }
}
