package cc.unilock.chromatifixes.mixin.late.geostrata.worldgen;

import Reika.DragonAPI.Instantiable.Math.LobulatedCurve;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.GeoStrata.GeoStrata;
import Reika.GeoStrata.World.CreepvineGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(value = CreepvineGenerator.class, remap = false)
public class CreepvineGeneratorMixin {

    // Precomputed for a in [-7,7], b in [-7,7], indexed by (a+7, b+7)
    @Unique
    private static final double[][] ANGLE_TABLE = new double[15][15];
    @Unique
    private static final double[][] DIST_TABLE  = new double[15][15];

    static {
        for (int a = -7; a <= 7; a++) {
            for (int b = -7; b <= 7; b++) {
                ANGLE_TABLE[a+7][b+7] = Math.toDegrees(Math.atan2(b, a));
                DIST_TABLE [a+7][b+7] = ReikaMathLibrary.py3d(a, 0, b);
            }
        }
    }

    // Shadow what we need from the class and its parents
    @Shadow
    private boolean generateIn(World world) {
        return false;
    }

    @Shadow
    private void setSeed(World world) {}

    @Shadow
    private boolean isValidLocation(World world, int x, int y, int z) {
        return false;
    }

    @Shadow
    public boolean generate(World world, int x, int y, int z,
                            Random random, int a, int b, float c, boolean d) {
        return false;
    }

    /**
     * @author MalTeeez
     * @reason The original is very unlikely to be changed, and this makes it a lot more efficient than multiple wrap operations.
     * Prevents cascading worldgen triggered by setBiomeForXZ.
     * Also precompute the angle and distance for the 15x15 area around the center point, which was being recomputed multiple times per iteration.
     */
    @Overwrite(remap = false)
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

        chunkX *= 16;
        chunkZ *= 16;

        if (this.generateIn(world)) {
            this.setSeed(world);
            for (int i = 0; i < 64; i++) {
                int x = ReikaRandomHelper.getRandomBetween(chunkX, chunkX+15, random);
                int z = ReikaRandomHelper.getRandomBetween(chunkZ, chunkZ+15, random);
                int y = world.getTopSolidOrLiquidBlock(x, z);
                if (this.isValidLocation(world, x, y, z)) {
                    if (this.generate(world, x, y, z, random, 6, 12, 0.6F, true)) {
                        if (GeoStrata.kelpForest != null) {
                            int maxR = 7;
                            LobulatedCurve lb = LobulatedCurve.fromMinMaxRadii(3, maxR, 5, true);
                            lb.generate(random);

                            for (int a = -maxR; a <= maxR; a++) {
                                for (int b = -maxR; b <= maxR; b++) {
                                    int dx = x+a;
                                    int dz = z+b;
                                    if (!world.getChunkProvider().chunkExists(dx >> 4, dz >> 4))
                                        continue;
                                    if (this.isValidLocation(world, dx, y, dz)) {
                                        double ang  = ANGLE_TABLE[a+7][b+7];
                                        double dist = DIST_TABLE [a+7][b+7];
                                        if (dist <= lb.getRadius(ang))
                                            ReikaWorldHelper.setBiomeForXZ(world, dx, dz,
                                                GeoStrata.kelpForest, false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
