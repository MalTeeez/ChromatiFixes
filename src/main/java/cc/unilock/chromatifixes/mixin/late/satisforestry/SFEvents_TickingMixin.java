package cc.unilock.chromatifixes.mixin.late.satisforestry;

import Reika.DragonAPI.Auxiliary.Trackers.SpecialDayTracker;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.Satisforestry.SFEvents;
import Reika.Satisforestry.Satisforestry;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SFEvents.class, remap = false)
public class SFEvents_TickingMixin {
    @Unique
    private long chromatifixes$cachedChunkKey = Long.MIN_VALUE;
    @Unique
    private boolean chromatifixes$cachedIsPinkForest = false;

    @Inject(method = "meltSnowIce", at = @At("HEAD"), cancellable = true)
    private void chromatifixes$meltSnowIce(BlockTickEvent evt, CallbackInfo ci) {
        ci.cancel();

        if (evt.world.isRaining() || !evt.world.isDaytime() || SpecialDayTracker.instance.isWinterEnabled())
            return;
        if (evt.block != Blocks.snow_layer && evt.block != Blocks.ice)
            return;

        int chunkX = evt.xCoord >> 4;
        int chunkZ = evt.zCoord >> 4;
        long key = (long) chunkX << 32 | (chunkZ & 0xFFFFFFFFL);
        if (key != chromatifixes$cachedChunkKey) {
            chromatifixes$cachedChunkKey = key;
            chromatifixes$cachedIsPinkForest = Satisforestry.isPinkForest(evt.world, evt.xCoord, evt.zCoord);
        }

        if (chromatifixes$cachedIsPinkForest && evt.world.canBlockSeeTheSky(evt.xCoord, evt.yCoord + 1, evt.zCoord)) {
            if (evt.block == Blocks.snow_layer)
                evt.world.setBlockToAir(evt.xCoord, evt.yCoord, evt.zCoord);
                // Always ice because we excluded the others earlier
            else evt.world.setBlock(evt.xCoord, evt.yCoord, evt.zCoord, Blocks.water);
        }
    }
}
