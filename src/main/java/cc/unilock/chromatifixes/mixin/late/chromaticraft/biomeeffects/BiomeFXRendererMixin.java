package cc.unilock.chromatifixes.mixin.late.chromaticraft.biomeeffects;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "Reika.ChromatiCraft.Render.BiomeFXRenderer$RainPoint", remap = false)
public class BiomeFXRendererMixin {

    @Unique
    private long ccfixes$lastUpdatedAt;

    @Inject(method = "<init>(DDIDDF)V", at = @At("RETURN"))
    private void onConstruct(double x, double y, int lifetime, double radius, double height, float speed, CallbackInfo ci) {
        ccfixes$lastUpdatedAt = -1;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void setCreatedAt(EntityPlayer ep, CallbackInfoReturnable<Boolean> cir) {
        if (this.ccfixes$lastUpdatedAt < ep.worldObj.getWorldTime()) {
            this.ccfixes$lastUpdatedAt = ep.worldObj.getWorldTime();
        } else {
            cir.setReturnValue(false);
        }
    }
}
