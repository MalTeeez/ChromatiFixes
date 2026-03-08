package cc.unilock.chromatifixes.mixin.late.geostrata.worldgen;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(targets = "Reika.GeoStrata.World.ArcticSpiresGenerator$ArcticSpire", remap = false)
public class ArcticSpiresGeneratorArcticSpireMixin {

    @Inject(
        method = "generate",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;ceiling_double_int(D)I", shift = At.Shift.AFTER, ordinal = 0),
        cancellable = true
    )
    private void checkChunksInRadiusAreLoaded(World world, int x, int y0, int z, double tilt, Random rand, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0, argsOnly = true) int r) {
        for (int cx = (x-r) >> 4; cx <= (x+r) >> 4; cx++) {
            for (int cz = (z-r) >> 4; cz <= (z+r) >> 4; cz++) {
                if (!world.getChunkProvider().chunkExists(cx, cz)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
