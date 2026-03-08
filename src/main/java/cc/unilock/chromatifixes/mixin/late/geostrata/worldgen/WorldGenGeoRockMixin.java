package cc.unilock.chromatifixes.mixin.late.geostrata.worldgen;

import Reika.GeoStrata.World.WorldGenGeoRock;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(value = WorldGenGeoRock.class, remap = false)
public abstract class WorldGenGeoRockMixin {

    @Final
    @Shadow private int size;
    @Final
    @Shadow private Block id;

    @Shadow
    private boolean canGenerateIn(World world, int x, int y, int z, Block b, int meta) {
        throw new AssertionError("shadow");
    }

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

        // Precompute the sin envelope: sin(l * PI / size) for l in [0, size]
        // This value is used twice per l iteration (d10 and d11 are identical),
        // and was being recomputed from scratch each time.
        float[] sinEnvelope = new float[size + 1];
        for (int l = 0; l <= size; l++) {
            sinEnvelope[l] = MathHelper.sin(l * (float) Math.PI / size);
        }

        for (int l = 0; l <= size; ++l) {
            double d6 = d0 + (d1 - d0) * l / size;
            double d7 = d4 + (d5 - d4) * l / size;
            double d8 = d2 + (d3 - d2) * l / size;
            double d9 = rand.nextDouble() * size / 16.0D;
            // sinEnvelope[l] replaces two calls to MathHelper.sin(l * PI / size)
            double d10 = (sinEnvelope[l] + 1.0F) * d9 + 1.0D;
            double d11 = d10;

            int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
            int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
            int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
            int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
            int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
            int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

            for (int dx = i1; dx <= l1; dx++) {
                double d12 = (dx + 0.5D - d6) / (d10 / 2.0D);
                if (d12 * d12 < 1.0D) {
                    for (int dy = j1; dy <= i2; dy++) {
                        double d13 = (dy + 0.5D - d7) / (d11 / 2.0D);
                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int dz = k1; dz <= j2; dz++) {
                                double d14 = (dz + 0.5D - d8) / (d10 / 2.0D);
                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    Block b = world.getBlock(dx, dy, dz);
                                    int meta = world.getBlockMetadata(dx, dy, dz);
                                    if (this.canGenerateIn(world, dx, dy, dz, b, meta)) {
                                        // flag 0 instead of 2.
                                        // Flag 2 triggers neighbor stuff, that should not be triggered during worldgen.
                                        world.setBlock(dx, dy, dz, id, 0, 0);
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //noinspection ReassignedVariable - Is complaining because the above AssertionError makes it think count is effectively final, but we know it isn't.
        return count > 0;
    }
}
