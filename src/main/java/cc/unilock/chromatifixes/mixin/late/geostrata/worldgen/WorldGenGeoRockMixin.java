package cc.unilock.chromatifixes.mixin.late.geostrata.worldgen;

import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry;
import Reika.GeoStrata.API.RockProofStone;
import Reika.GeoStrata.Base.RockBlock;
import Reika.GeoStrata.Registry.GeoOptions;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Mixin(targets = "Reika.GeoStrata.World.WorldGenGeoRock")
public abstract class WorldGenGeoRockMixin {

    @Final
    @Shadow private int size;
    @Final
    @Shadow private Block id;

    @Shadow
    @Final
    private Block overwrite;

    /**
     * @author MalTeeez
     * @reason The original is very unlikely to be changed, and this makes it a lot more efficient than multiple wrap operations.
     * Also cache the sin envelope value that was being recomputed multiple times per iteration, and use flag 0 instead of 2 when setting blocks.
     */
    @Overwrite
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int count = 0;
        float f = rand.nextFloat() * (float) Math.PI;

        double d0 = x + 8 + MathHelper.sin(f) * size / 8.0F;
        double d1 = x + 8 - MathHelper.sin(f) * size / 8.0F;
        double d2 = z + 8 + MathHelper.cos(f) * size / 8.0F;
        double d3 = z + 8 - MathHelper.cos(f) * size / 8.0F;
        double d4 = y + rand.nextInt(3) - 2;
        double d5 = y + rand.nextInt(3) - 2;

        // Precompute sin envelope for the l-loop
        float[] sinEnvelope = new float[size + 1];
        for (int l = 0; l <= size; l++) {
            sinEnvelope[l] = MathHelper.sin(l * (float) Math.PI / size);
        }

        final boolean overgen = GeoOptions.OVERGEN.getState();
        final Block overwriteBlock = this.overwrite;
        final int overwriteBlockId = Block.getIdFromBlock(overwriteBlock);
        final int overwriteBlockIdLSB = overwriteBlockId & 0xFF;
        final boolean overwriteIsRockProof = overwriteBlock instanceof RockProofStone;
        final boolean overwriteIsRockBlock = overwriteBlock instanceof RockBlock;

        // If !overgen and the overwrite target is a RockBlock, nothing will ever
        // pass canGenerateIn — bail out immediately.
        if (!overgen && overwriteIsRockBlock) {
            return false;
        }

        // Track modified chunks so setChunkModified() is called once per chunk,
        // not once per block. Veins touch at most 4 chunks so this stays tiny.
        Set<Chunk> modifiedChunks = new HashSet<Chunk>();

        try {
            WorldGenInterceptionRegistry.skipLighting = true;

            // Chunk cache — avoids hashmap lookup when consecutive dx/dz values
            // fall in the same chunk, which is the common case.
            Chunk cachedChunk = null;
            int cachedChunkX = Integer.MIN_VALUE;
            int cachedChunkZ = Integer.MIN_VALUE;
            ExtendedBlockStorage[] cachedSections = null;

            // These are refreshed whenever the chunk or section changes
            byte[] lsbArray = null;
            NibbleArray msbArray = null;
            ExtendedBlockStorage cachedSection = null;
            int cachedSectionIndex = Integer.MIN_VALUE;

            for (int l = 0; l <= size; ++l) {
                double t = (double) l / size;
                double d6 = d0 + (d1 - d0) * t;
                double d7 = d4 + (d5 - d4) * t;
                double d8 = d2 + (d3 - d2) * t;
                double d9 = rand.nextDouble() * size / 16.0D;
                double d10 = ((sinEnvelope[l] + 1.0F) * d9 + 1.0D) / 2.0D;

                int i1 = MathHelper.floor_double(d6 - d10);
                int j1 = MathHelper.floor_double(d7 - d10);
                int k1 = MathHelper.floor_double(d8 - d10);
                int l1 = MathHelper.floor_double(d6 + d10);
                int i2 = MathHelper.floor_double(d7 + d10);
                int j2 = MathHelper.floor_double(d8 + d10);

                double invHalfD10 = 1.0D / d10;

                for (int dx = i1; dx <= l1; dx++) {
                    double d12 = (dx + 0.5D - d6) * invHalfD10;
                    double d12sq = d12 * d12;
                    if (d12sq >= 1.0D) continue;

                    int localX = dx & 15;
                    int chunkX = dx >> 4;

                    for (int dy = j1; dy <= i2; dy++) {
                        double d13 = (dy + 0.5D - d7) * invHalfD10;
                        double d12d13sq = d12sq + d13 * d13;
                        if (d12d13sq >= 1.0D) continue;

                        double remaining = 1.0D - d12d13sq;
                        int sectionIndex = dy >> 4;
                        int localY = dy & 15;

                        for (int dz = k1; dz <= j2; dz++) {
                            double d14 = (dz + 0.5D - d8) * invHalfD10;
                            if (d14 * d14 >= remaining) continue;

                            int chunkZ = dz >> 4;

                            // Refresh chunk and raw arrays only on chunk boundary crossing
                            if (cachedChunk == null || chunkX != cachedChunkX || chunkZ != cachedChunkZ) {
                                cachedChunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                                cachedChunkX = chunkX;
                                cachedChunkZ = chunkZ;
                                cachedSections = cachedChunk.getBlockStorageArray();

                                // Section may also have changed, force refresh below
                                cachedSectionIndex = Integer.MIN_VALUE;
                            }

                            // Refresh section and its raw arrays only when dy band changes
                            // or when the chunk just changed above
                            if (sectionIndex != cachedSectionIndex) {
                                cachedSectionIndex = sectionIndex;
                                if (sectionIndex < 0 || sectionIndex >= cachedSections.length) {
                                    cachedSection = null;
                                    lsbArray = null;
                                    msbArray = null;
                                } else {
                                    cachedSection = cachedSections[sectionIndex];
                                    if (cachedSection == null) {
                                        lsbArray = null;
                                        msbArray = null;
                                    } else {
                                        lsbArray = cachedSection.getBlockLSBArray();
                                        msbArray = cachedSection.getBlockMSBArray();
                                    }
                                }
                            }

                            if (lsbArray == null) continue;

                            int localZ = dz & 15;
                            int index = localY << 8 | localZ << 4 | localX;

                            // LSB fast-reject — avoids MSB read for the vast majority of blocks
                            int blockIdLSB = lsbArray[index] & 0xFF;
                            if (blockIdLSB != overwriteBlockIdLSB) continue;

                            // LSB matched — now check full ID including MSB if present
                            int blockId = blockIdLSB;
                            if (msbArray != null) {
                                blockId |= msbArray.get(localX, localY, localZ) << 8;
                            }
                            if (blockId != overwriteBlockId) continue;

                            // RockProofStone check — nearly always false, branch predicts well
                            if (overwriteIsRockProof
                                && ((RockProofStone) overwriteBlock).blockRockGeneration(world, dx, dy, dz, overwriteBlock, world.getBlockMetadata(dx, dy, dz))) {
                                continue;
                            }

                            // Write directly into the section array, bypassing World.setBlock
                            // (neighbor notifications, lighting, heightmap). Safe in world gen
                            // because neighbors aren't loaded and lighting rebuilds after gen.
                            cachedSection.func_150818_a(localX, localY, localZ, id);
                            cachedSection.setExtBlockMetadata(localX, localY, localZ, 0);
                            modifiedChunks.add(cachedChunk);
                            count++;
                        }
                    }
                }
            }
        } finally {
            WorldGenInterceptionRegistry.skipLighting = false;
        }

        for (Chunk chunk : modifiedChunks) {
            chunk.setChunkModified();
        }

        return count > 0;
    }
}
