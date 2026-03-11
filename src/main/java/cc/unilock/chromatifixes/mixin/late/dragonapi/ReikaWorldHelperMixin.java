package cc.unilock.chromatifixes.mixin.late.dragonapi;

import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ReikaWorldHelper.class, remap = false)
public class ReikaWorldHelperMixin {

    @Inject(
        method = "setBiomeForXZ",
        at = @At(value = "INVOKE", target = "LReika/DragonAPI/DragonAPICore;logError(Ljava/lang/Object;Lcpw/mods/fml/relauncher/Side;)V", shift = At.Shift.AFTER)
    )
    private static void logStacktrace$chromatifixes(World world, int x, int z, BiomeGenBase biome, boolean applyEnvironment, CallbackInfo ci) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            Thread.dumpStack();
            throw new RuntimeException("Attempted to set biome for an unloaded chunk at " + x + ", " + z);
        }
    }

}
