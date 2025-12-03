package cc.unilock.chromatifixes.mixin.late.chromaticraft.biomeeffects;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "Reika.ChromatiCraft.Render.CliffFogRenderer$Cloud", remap = false)
public class CliffFogRendererCloudMixin {

    @Unique
    private long ccfixes$lastUpdatedAt;

    @Inject(method = "<init>(FFFFDIDDD)V", at = @At("RETURN"))
    private void onConstruct(float tx, float ty, float sx, float sy, double s, int l, double x, double y, double z, CallbackInfo ci) {
        ccfixes$lastUpdatedAt = -1;
    }

    @Inject(method = "update", at = @At(value = "HEAD"), cancellable = true)
    private void setCreatedAt(double vx, double vy, double vz, CallbackInfoReturnable<Boolean> cir) {
        long worldTime = Minecraft.getMinecraft().thePlayer.worldObj.getWorldTime();
        if (this.ccfixes$lastUpdatedAt < worldTime) {
            this.ccfixes$lastUpdatedAt = worldTime;
        } else if (ReikaMathLibrary.randinrange(0, 1) == 0) {
            // Still want them to have some more speed so we only skip on the same frame half the time
            cir.setReturnValue(false);
        }
    }
}
