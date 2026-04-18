package cc.unilock.chromatifixes.mixin.late.dragonapi;

import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import com.llamalad7.mixinextras.sugar.Local;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    /**
     * Prevent NPEs during the generation of mob spawners, seems to be related to hodgepodge.
     * If the calling method modifies the returned spawner logic, a null guard is required there as well.
     */
    @Inject(
        method = "generateSpawner",
        at = @At(
            value = "INVOKE",
            target = "LReika/DragonAPI/Libraries/ReikaSpawnerHelper;setMobSpawnerMob(Lnet/minecraft/tileentity/TileEntityMobSpawner;Ljava/lang/String;)V",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private static void generateSpawnerWithGuard(World world, int x, int y, int z, Class mob, CallbackInfoReturnable<MobSpawnerBaseLogic> cir, @Local(name = "te") TileEntityMobSpawner te) {
        if (te == null) {
            cir.setReturnValue(null);
        }
    }

}
