package net.fabricmc.joamama.mock;

import com.google.errorprone.annotations.DoNotCall;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryEntry;
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
    @DoNotCall
    public Chunk getChunk (int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        throw new AssertionError();
    }

    @DoNotCall
    public boolean isChunkLoaded (int chunkX, int chunkZ) {
        throw new AssertionError();
    }

    @DoNotCall
    public int getTopY (Heightmap.Type heightmap, int x, int z) {
        throw new AssertionError();
    }

    @DoNotCall
    public int getAmbientDarkness () {
        throw new AssertionError();
    }

    @DoNotCall
    public BiomeAccess getBiomeAccess () {
        throw new AssertionError();
    }

    @DoNotCall
    public RegistryEntry<Biome> getGeneratorStoredBiome (int biomeX, int biomeY, int biomeZ) {
        throw new AssertionError();
    }

    @DoNotCall
    public boolean isClient () {
        throw new AssertionError();
    }

    @DoNotCall
    public int getSeaLevel() {
        throw new AssertionError();
    }

    @DoNotCall
    public DimensionType getDimension () {
        throw new AssertionError();
    }

    @DoNotCall
    public float getBrightness (Direction direction, boolean shaded) {
        throw new AssertionError();
    }

    @DoNotCall
    public LightingProvider getLightingProvider () {
        throw new AssertionError();
    }

    public WorldBorder getWorldBorder () { return new WorldBorder(); }

    @DoNotCall
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
