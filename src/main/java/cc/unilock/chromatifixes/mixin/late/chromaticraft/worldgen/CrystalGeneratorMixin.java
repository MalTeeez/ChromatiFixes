package cc.unilock.chromatifixes.mixin.late.chromaticraft.worldgen;

import Reika.ChromatiCraft.API.Event.CrystalGenEvent;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.IWG.CrystalGenerator;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(value = CrystalGenerator.class, remap = false)
public abstract class CrystalGeneratorMixin {

    @Shadow
    @Final
    private static int PER_CHUNK;

    @Shadow
    public static boolean canGenerateAt(World world, int x, int y, int z) {
        throw new RuntimeException("Tried to call shadowed method CrystalGenerator.canGenerateAt in CrystalGeneratorMixin");
    }

    @Shadow
    public static float getDensityFactor(World world, int x, int z) {
        throw new RuntimeException("Tried to call shadowed method CrystalGenerator.getDensityFactor in CrystalGeneratorMixin");
    }

    /**
     * @author MalTeeez
     * @reason Removed bottom lighting update loop, and to avoid using a mostly costly noop instead of func_147479_m,
     * Overwrite seems to be the only option.
     */
    @Overwrite
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        chunkX *= 16;
        chunkZ *= 16;

        int amt = (int)(PER_CHUNK * getDensityFactor(world, chunkX, chunkZ));
        Block id = ChromaBlocks.CRYSTAL.getBlockInstance();
        int maxy = 64;
        if (world.provider.isHellWorld)
            maxy = 128;

        for (int i = 0; i < amt; i++) {
            int posX = chunkX + random.nextInt(16);
            int posZ = chunkZ + random.nextInt(16);

            int posY = 4+random.nextInt(maxy-4);
            int meta = random.nextInt(16);

            if (canGenerateAt(world, posX, posY, posZ)) {
                world.setBlock(posX, posY, posZ, id, meta, 3);
                MinecraftForge.EVENT_BUS.post(new CrystalGenEvent(world, posX, posY, posZ, random, meta));
            }
        }
    }
}
