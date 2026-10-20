package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.ChromatiCraft.Block.Crystal.BlockCrystalGlow.Bases;
import Reika.ChromatiCraft.Render.ISBRH.CrystalGlowRenderer;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CrystalGlowRenderer.class, remap = false)
public class CrystalGlowRendererMixin {
    @Definition(id = "metadata", local = @Local(type = int.class, argsOnly = true, ordinal = 0))
    @Expression("metadata / 16")
    @ModifyExpressionValue(method = "renderInventoryBlock", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int renderInventoryBlock(int base) {
        return Math.floorMod(base, Bases.baseList.length);
    }

    @ModifyExpressionValue(method = "renderWorldBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I", remap = true))
    private int renderWorldBlock(int meta) {
        return Math.floorMod(meta, 16);
    }
}
