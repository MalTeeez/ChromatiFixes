package cc.unilock.chromatifixes.mixin.late.chromaticraft.worldgen;

import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.GlowingCliffsColumnShaper;
import Reika.DragonAPI.Auxiliary.Trackers.WorldgenProfiler;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value =  BiomeGlowingCliffs.class, remap = false)
public class BiomeGlowingCliffsMixin {
    @Shadow
    private static GlowingCliffsColumnShaper terrain;

    @Shadow
    private static void initTerrain(World world) {
        throw new RuntimeException("Tried to call shadowed method BiomeGlowingCliffs.initTerrain in BiomeGlowingCliffsMixin");
    }

    /**
     * @author MalTeeez
     * @reason Early exit when no glowing cliffs biome is within one chunk,
     * avoiding expensive per-column biome scans in the common case (that is, on each generated chunk).
     */
    @Overwrite
    public static void blendTerrainEdgesAndGenCliffs(World world, int chunkX, int chunkZ, Block[] blockArray, byte[] metaArray) {
        initTerrain(world);

        BiomeGenBase chunkBiome = null;
        boolean chunkIsGlowCliffs = false;
        boolean anyGlowCliffs = false;
        boolean anyNeighborIsGlowCliffs = false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BiomeGenBase b = world.getWorldChunkManager().getBiomeGenAt((chunkX + dx) * 16 + 8, (chunkZ + dz) * 16 + 8);
                boolean isGlow = BiomeGlowingCliffs.isGlowingCliffs(b);
                if (dx == 0 && dz == 0) {
                    chunkBiome = b;
                    chunkIsGlowCliffs = isGlow;
                } else if (isGlow) {
                    anyNeighborIsGlowCliffs = true;
                }
                if (isGlow) anyGlowCliffs = true;
            }
        }

        if (!anyGlowCliffs) return;
        if (!chunkIsGlowCliffs && !anyNeighborIsGlowCliffs) return;

        boolean isBorderChunk = chunkIsGlowCliffs && anyNeighborIsGlowCliffs;

        for (int i = 0; i < 16; i++) {
            for (int k = 0; k < 16; k++) {
                int x = chunkX * 16 + i;
                int z = chunkZ * 16 + k;

                if (chunkIsGlowCliffs) {
                    if (WorldgenProfiler.profilingEnabled())
                        WorldgenProfiler.startGenerator(world, "Luminous cliff terrain shaping", chunkX, chunkZ);
                    terrain.generateColumn(world, x, z, chunkX, chunkZ, blockArray, metaArray, chunkBiome);
                    if (WorldgenProfiler.profilingEnabled())
                        WorldgenProfiler.onRunGenerator(world, "Luminous cliff terrain shaping", chunkX, chunkZ);

                    if (isBorderChunk && ChromaOptions.BIOMEBLEND.getState()) {
                        if (WorldgenProfiler.profilingEnabled())
                            WorldgenProfiler.startGenerator(world, "Luminous cliff adjacency biome blending", chunkX, chunkZ);
                        terrain.blendEdge(world, x, z, blockArray, metaArray);
                        if (WorldgenProfiler.profilingEnabled())
                            WorldgenProfiler.onRunGenerator(world, "Luminous cliff adjacency biome blending", chunkX, chunkZ);
                    }
                } else if (ChromaOptions.BIOMEBLEND.getState()) {
                    if (WorldgenProfiler.profilingEnabled())
                        WorldgenProfiler.startGenerator(world, "Luminous cliff adjacency biome blending", chunkX, chunkZ);
                    terrain.blendEdge(world, x, z, blockArray, metaArray);
                    if (WorldgenProfiler.profilingEnabled())
                        WorldgenProfiler.onRunGenerator(world, "Luminous cliff adjacency biome blending", chunkX, chunkZ);
                }
            }
        }
    }

}
