package cc.unilock.chromatifixes.mixin.late.chromaticraft.worldgen;

import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.ChromatiCraft.World.GlowingCliffsColumnShaper;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = GlowingCliffsColumnShaper.class, remap = false)
public abstract class GlowingCliffsColumnShaperMixin {

    @Shadow
    protected abstract BiomeGenBase getBiome(World world, int x, int z);

    @Unique
    private static final int ANGLE_STEPS = (int) (360 / 7.5);
    @Unique
    private static final double[] COS_TABLE = new double[ANGLE_STEPS];
    @Unique
    private static final double[] SIN_TABLE = new double[ANGLE_STEPS];

    static {
        for (int i = 0; i < ANGLE_STEPS; i++) {
            double rad = Math.toRadians(i * 7.5);
            COS_TABLE[i] = Math.cos(rad);
            SIN_TABLE[i] = Math.sin(rad);
        }
    }

    /**
     * @author MalTeeez
     * @reason The original is very unlikely to be changed, and this makes it a lot more efficient than multiple wrap operations.
     * This is a faster version of the original method that checks for glowing cliffs biomes
     * in a spiral pattern around the given coordinates, instead of checking every point in a square area.
     * It returns the distance factor and the coordinates of the closest glowing cliffs biome found.
     * Optimized here by using precomputed cosine and sine tables to avoid expensive trigonometric calculations during the search.
     */
    @Overwrite
    private Object[] getInvertedDistanceFactor(World world, int x, int z, int search) {
        int mind = Integer.MAX_VALUE;
        int look = search;
        Coordinate result = null;
        for (int d = 1; d <= look; d += 4) {
            for (int i = 0; i < ANGLE_STEPS; i++) {
                int dx = MathHelper.floor_double(x + d * COS_TABLE[i]);
                int dz = MathHelper.floor_double(z + d * SIN_TABLE[i]);
                BiomeGenBase b = this.getBiome(world, dx, dz);
                if (BiomeGlowingCliffs.isGlowingCliffs(b)) {
                    mind = d;
                    result = new Coordinate(dx, 0, dz);
                    look = mind - 1;
                    break;
                }
            }
        }
        return mind == Integer.MAX_VALUE ? null : new Object[]{mind / (double) search, result};
    }
}
