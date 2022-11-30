package net.fabricmc.joamama.mock;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MockWorldView (BlockState state) implements WorldView {
    @Deprecated
    public Chunk getChunk (int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        throw new AssertionError();
    }

    @Deprecated
    public boolean isChunkLoaded (int chunkX, int chunkZ) {
        throw new AssertionError();
    }

    @Deprecated
    public int getTopY (Heightmap.Type heightmap, int x, int z) {
        throw new AssertionError();
    }

    @Deprecated
    public int getAmbientDarkness () {
        throw new AssertionError();
    }

    @Deprecated
    public BiomeAccess getBiomeAccess () {
        throw new AssertionError();
    }

    @Deprecated
    public RegistryEntry<Biome> getGeneratorStoredBiome (int biomeX, int biomeY, int biomeZ) {
        throw new AssertionError();
    }

    @Deprecated
    public boolean isClient () {
        throw new AssertionError();
    }

    @Deprecated
    public int getSeaLevel() {
        throw new AssertionError();
    }

    @Deprecated
    public DimensionType getDimension () {
        throw new AssertionError();
    }

    @Deprecated
    public DynamicRegistryManager getRegistryManager() {
        throw new AssertionError();
    }

    @Deprecated
    public FeatureSet getEnabledFeatures() {
        throw new AssertionError();
    }

    @Deprecated
    public float getBrightness (Direction direction, boolean shaded) {
        throw new AssertionError();
    }

    @Deprecated
    public LightingProvider getLightingProvider () {
        throw new AssertionError();
    }

    public WorldBorder getWorldBorder () { return new WorldBorder(); }

    @Deprecated
    public List<VoxelShape> getEntityCollisions (Entity entity, Box box) {
        throw new AssertionError();
    }

    public BlockEntity getBlockEntity (BlockPos pos) {
        return null;
    }

    public BlockState getBlockState (BlockPos pos) {
        return this.state;
    }

    public FluidState getFluidState (BlockPos pos) {
        return this.state.getFluidState();
    }
}
