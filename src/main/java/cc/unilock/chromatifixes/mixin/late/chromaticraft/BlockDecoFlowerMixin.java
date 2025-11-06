package cc.unilock.chromatifixes.mixin.late.chromaticraft;

import Reika.ChromatiCraft.Block.Worldgen.BlockDecoFlower;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockDecoFlower.class, remap = false)
public class BlockDecoFlowerMixin {
    @Inject(method = "setBlockBoundsBasedOnState", at = @At(value = "HEAD"), cancellable = true)
    public void setSafeBlockBounds(IBlockAccess world, int x, int y, int z, CallbackInfo ci) {
        if (world.getBlockMetadata(x, y, z) > 7) {
            ((Block) (Object) this).setBlockBounds(0, 0, 0, 1, 1, 1);
            ci.cancel();
        }
    }
}
