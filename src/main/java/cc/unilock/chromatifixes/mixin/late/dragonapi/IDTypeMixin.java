package cc.unilock.chromatifixes.mixin.late.dragonapi;

import Reika.DragonAPI.Extras.IDType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = IDType.class, remap = false)
public class IDTypeMixin {

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 254, ordinal = 0))
    private static int expandBiomeIDSpace(int constant) {
        return 65536;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "LReika/DragonAPI/Extras/IDType;<init>(Ljava/lang/String;II)V", ordinal = 4), index = 2)
    private static int expandPotionIDSpace(int par2) {
        return 65536;
    }

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "LReika/DragonAPI/Extras/IDType;<init>(Ljava/lang/String;II)V", ordinal = 5), index = 2)
    private static int expandEnchantmentIDSpace(int par2) {
        return 32768;
    }
}
