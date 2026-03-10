package cc.unilock.chromatifixes.mixin.late.dragonapi;

import Reika.DragonAPI.IO.ReikaFileReader;
import cc.unilock.chromatifixes.ChromatiFixesConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ReikaFileReader.class, remap = false)
public class ReikaFileReaderMixin {

    @ModifyArg(
        method = "isInternetAccessible",
        at = @At(value = "INVOKE", target = "Ljava/net/URLConnection;setConnectTimeout(I)V", ordinal = 0),
        index = 0
    )
    private static int modifyConnectTimeout$chromatifixes(int constant) {
        return ChromatiFixesConfig.internetConnectivityTimeoutDuration;
    }
}
