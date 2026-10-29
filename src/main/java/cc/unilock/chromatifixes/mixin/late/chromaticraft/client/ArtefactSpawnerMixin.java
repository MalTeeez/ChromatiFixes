package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.ChromatiCraft.Magic.Artefact.ArtefactSpawner;
import cc.unilock.chromatifixes.ChromatiFixesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ArtefactSpawner.class, remap = false)
public class ArtefactSpawnerMixin {
    /**
     * @author unilock
     * @reason allow disabling the shader via config, and skip it entirely while high above the surface
     */
    @Inject(method = "isShaderActive", at = @At("HEAD"), cancellable = true)
    private void isShaderActive(World world, CallbackInfoReturnable<Boolean> cir) {
        if (ChromatiFixesConfig.disableArtefactShader) {
            cir.setReturnValue(false);
            return;
        }

        int maxHeight = ChromatiFixesConfig.artefactShaderMaxHeightAboveSurface;
        EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
        if (maxHeight > 0 && ep != null && ep.posY > world.getHeightValue(MathHelper.floor_double(ep.posX), MathHelper.floor_double(ep.posZ)) + maxHeight) {
            cir.setReturnValue(false);
        }
    }
}
