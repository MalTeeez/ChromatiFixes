package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.ChromatiCraft.Render.Item.EnderCrystalRenderer;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to lazily initialize the Ender Crystal renderer to avoid early class loading and thereby relying on
 * correct load order. This originally lead to issues with Angelica and client.renderer.entity.RenderManager mixins.
 * The original static initializer is canceled, and the renderer is lazily initialized on first use in renderItem.
 */
@Mixin(value = EnderCrystalRenderer.class, remap = false)
public class EnderCrystalRendererMixin {

    @Shadow
    private static RenderEnderCrystal renderer;

    @Inject(method = "<clinit>", at = @At("HEAD"), cancellable = true, require = 1)
    private static void chroma$cancelEarlyInit(CallbackInfo ci) {
        renderer = null;
        ci.cancel();
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void chroma$lazyInitRenderer(CallbackInfo ci) {
        if (renderer == null) {
            renderer = (RenderEnderCrystal) ReikaEntityHelper.getEntityRenderer(EntityEnderCrystal.class);
        }
    }
}
