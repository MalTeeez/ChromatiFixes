package cc.unilock.chromatifixes.mixin.late.geostrata.worldgen;

import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModList;
import Reika.GeoStrata.Blocks.BlockOreVein;
import Reika.GeoStrata.Registry.GeoBlocks;
import Reika.GeoStrata.World.DecoGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;


@Mixin(value = DecoGenerator.Decorations.class, remap = false)
public class DecoGenerator$DecorationsMixin {

    /**
     * @author MalTeeez
     * @reason The original is very unlikely to be changed, and this makes it a lot more efficient than multiple wrap operations.
     *
     * Prevents cascading worldgen triggered by countAdjacentBlocks and getTopSolidOrLiquidBlock.
     */
    @Overwrite(remap = false)
    private boolean generate(World world, int x, int y, int z, Random rand) {
        switch ((DecoGenerator.Decorations)(Object)this) {
            case OCEANSPIKE: {
                int h = 0;
                int d = rand.nextInt(8);
                int min = ReikaRandomHelper.getRandomBetween(4, 7, rand);
                while (h < 15 && (world.getBlock(x, y+h+d, z) == Blocks.water
                    || (h < min && world.getBlock(x, y+h+1, z) == Blocks.water))) {
                    world.setBlock(x, y+h, z, GeoBlocks.DECOGEN.getBlockInstance(), 0, 3);
                    h++;
                }
                return true;
            }
            case OCEANSPIKES: {
                int n = 4+rand.nextInt(9);
                for (int i = 0; i < n; i++) {
                    int dx = ReikaRandomHelper.getRandomPlusMinus(x, 8, rand);
                    int dz = ReikaRandomHelper.getRandomPlusMinus(z, 8, rand);
                    if (!world.getChunkProvider().chunkExists(dx >> 4, dz >> 4))
                        continue;
                    int dy = world.getTopSolidOrLiquidBlock(dx, dz);
                    ((DecoGenerator$DecorationsAccessor)(Object) DecoGenerator.Decorations.OCEANSPIKE).invokeGenerate(world, dx, dy, dz, rand);
                }
                return true;
            }
            case OREVEINS: {
                int amt = 16;
                int minY = 4;
                int maxY = 56;
                BlockOreVein.VeinType vein = BlockOreVein.VeinType.STONE;
                if (world.provider.dimensionId == -1) {
                    vein = BlockOreVein.VeinType.NETHER;
                    amt = 4;
                    maxY = 126;
                }
                else if (world.provider.dimensionId == 1) {
                    vein = BlockOreVein.VeinType.END;
                    amt = 3;
                    minY = 8;
                    maxY = 64;
                }
                if (!vein.isEnabled())
                    return false;
                if (vein == BlockOreVein.VeinType.END && ModList.CHROMATICRAFT.isLoaded())
                    amt = ReikaMathLibrary.py3d(x, 0, z) <= 850 ? 0 : 6;
                for (int i = 0; i < amt; i++) {
                    int dy = ReikaRandomHelper.getRandomBetween(minY, maxY, rand);
                    int dx = ReikaRandomHelper.getRandomPlusMinus(x, 8, rand);
                    int dz = ReikaRandomHelper.getRandomPlusMinus(z, 8, rand);
                    if (world.getChunkProvider().chunkExists(dx >> 4, dz >> 4)) {
                        if (world.getBlock(dx, dy, dz) == vein.template) {
                            int adj = ReikaWorldHelper.countAdjacentBlocks(world, dx, dy, dz, Blocks.air, false);
                            if (adj > 0 && adj < 3)
                                world.setBlock(dx, dy, dz, GeoBlocks.OREVEIN.getBlockInstance(), vein.ordinal(), 3);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
}
