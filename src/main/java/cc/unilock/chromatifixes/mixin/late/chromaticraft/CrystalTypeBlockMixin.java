package cc.unilock.chromatifixes.mixin.late.chromaticraft;

import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Metadata > 15 (e.g. with EndlessIDs) indexes past CrystalElement.elements
@Mixin(value = CrystalTypeBlock.class, remap = false)
public class CrystalTypeBlockMixin {
    @ModifyVariable(method = "breakBlock", at = @At("HEAD"), argsOnly = true, ordinal = 3, remap = true)
    private int breakBlock(int meta) {
        return Math.floorMod(meta, 16);
    }

    @ModifyExpressionValue(method = "ding(Lnet/minecraft/world/World;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I", remap = true))
    private static int ding(int meta) {
        return Math.floorMod(meta, 16);
    }

    @ModifyExpressionValue(method = {"getLightValue", "getCrystalElement"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I", remap = true))
    private int getElementMeta(int meta) {
        return Math.floorMod(meta, 16);
    }
}
