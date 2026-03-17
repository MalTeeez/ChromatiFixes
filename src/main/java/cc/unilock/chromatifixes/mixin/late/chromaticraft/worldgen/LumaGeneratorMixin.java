package cc.unilock.chromatifixes.mixin.late.chromaticraft.worldgen;

import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.IWG.LumaGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = LumaGenerator.class, remap = false)
public class LumaGeneratorMixin {

    /**
     * @author MalTeeez
     * @reason Prevent this check from causing cascading world gen. Overwrite because it's not very big and unlikely to change.
     */
    @Overwrite
    private boolean isValidLocation(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (!world.getChunkProvider().chunkExists(x >> 4, z >> 4) && !block.isReplaceableOreGen(world, x, y, z, Blocks.stone) && !block.isReplaceableOreGen(world, x, y, z, Blocks.dirt))
            return false;
        if (!world.getBlock(x, y+1, z).isAir(world, x, y+1, z))
            return false;
        for (int i = 0; i < 6; i++) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
            int dx = x+dir.offsetX;
            int dz = z+dir.offsetZ;
            if (!world.getChunkProvider().chunkExists(dx >> 4, dz >> 4))
                return false;
            if (dir != ForgeDirection.UP) {
                Block b = world.getBlock(dx, y+dir.offsetY, dz);
                if (b != ChromaBlocks.LUMA.getBlockInstance() && !b.getMaterial().isSolid())
                    return false;
            }
        }
        return true;
    }
}
