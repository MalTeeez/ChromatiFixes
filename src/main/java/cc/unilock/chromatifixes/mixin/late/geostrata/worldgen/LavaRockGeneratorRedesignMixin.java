package cc.unilock.chromatifixes.mixin.late.geostrata.worldgen;

import Reika.GeoStrata.World.LavaRockGeneratorRedesign;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import Reika.DragonAPI.Instantiable.Math.Noise.SimplexNoiseGenerator;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.GeoStrata.Registry.GeoBlocks;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(value = LavaRockGeneratorRedesign.class, remap = false)
public abstract class LavaRockGeneratorRedesignMixin {

    @Shadow private SimplexNoiseGenerator lavaRockThickness;

    /**
     * @author MalTeeez
     * @reason The original is very unlikely to be changed, and this makes it a lot more efficient than loads of wrap operations.
     * Also, this way we can easily use our local methods, and we don't have to change the 2 others.
     * Precompute lava position boolean array and noise meta step grid to eliminate repeated getBlock
     * calls in getLavaDistance and repeated noise evaluation in getMetaStep per recursive placeBlock call.
     */
    @Overwrite
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.getWorldInfo().getTerrainType() == WorldType.FLAT || Math.abs(world.provider.dimensionId) == 1)
            return;

        Chunk c = world.getChunkFromChunkCoords(chunkX, chunkZ);

        // Pre-scan lava positions once — avoids loads of getBlock calls
        boolean[][][] hasLava = new boolean[16][12][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y <= 11; y++) {
                    Block b = c.getBlock(x, y, z);
                    hasLava[x][y][z] = (b == Blocks.lava || b == Blocks.flowing_lava);
                }
            }
        }

        // Precompute meta step grid — one noise evaluation per x/z column instead of one per recursive placeBlock call
        int[][] metaStepGrid = new int[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int wx = x + chunkX * 16;
                int wz = z + chunkZ * 16;
                double val = lavaRockThickness.getValue(wx, wz);
                metaStepGrid[x][z] = (int) MathHelper.clamp_double(
                    ReikaMathLibrary.normalizeToBounds(val, 0.5, 3.5), 1, 3);
            }
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 1; y <= 14; y++) {
                    Block b = c.getBlock(x, y, z);
                    if (b.isReplaceableOreGen(world, x + chunkX * 16, y, z + chunkZ * 16, Blocks.stone)
                        || b == GeoBlocks.LAVAROCK.getBlockInstance()) {
                        int d = this.chromatiFixes$getLavaDistance(hasLava, x, y, z);
                        if (d <= 4) {
                            this.chromatiFixes$placeBlock(c, x, y, z, d - 1, b, metaStepGrid);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private int chromatiFixes$getLavaDistance(boolean[][][] hasLava, int x, int y, int z) {
        for (int d = 1; d <= 4; d++) {
            for (int i = 0; i < 6; i++) {
                ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
                int dx = x + d * dir.offsetX;
                int dy = y + d * dir.offsetY;
                int dz = z + d * dir.offsetZ;
                if (dy >= 0 && dy <= 11 && dx >= 0 && dz >= 0 && dx < 16 && dz < 16) {
                    if (hasLava[dx][dy][dz])
                        return d;
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    @Unique
    private void chromatiFixes$placeBlock(Chunk c, int x, int y, int z, int meta, Block at, int[][] metaStepGrid) {
        if (meta > 3)
            return;
        if (at == GeoBlocks.LAVAROCK.getBlockInstance() && meta >= c.getBlockMetadata(x, y, z))
            return;

        c.func_150807_a(x, y, z, GeoBlocks.LAVAROCK.getBlockInstance(), meta);

        if (meta < 3) {
            for (int i = 0; i < 6; i++) {
                ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
                int dx = x + dir.offsetX;
                int dy = y + dir.offsetY;
                int dz = z + dir.offsetZ;
                if (dy >= 0 && dx >= 0 && dz >= 0 && dx < 16 && dz < 16) {
                    Block b = c.getBlock(dx, dy, dz);
                    if (b.isReplaceableOreGen(c.worldObj, dx + c.xPosition * 16, dy, dz + c.zPosition * 16, Blocks.stone)
                        || b == GeoBlocks.LAVAROCK.getBlockInstance()) {
                        this.chromatiFixes$placeBlock(c, dx, dy, dz, meta + metaStepGrid[dx][dz], b, metaStepGrid);
                    }
                }
            }
        }
    }
}
