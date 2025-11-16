package cc.unilock.chromatifixes.mixin.late.rotarycraft;

import Reika.RotaryCraft.Base.BlockBasicMultiTE;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BlockBasicMultiTE.class, remap = false)
public class BlockBasicMultiTEMixin {

    /**
     * Exploit the null check at https://github.com/ReikaKalseki/RotaryCraft/blob/master/Base/BlockBasicMultiTE.java#L1085 to cancel the condition
     * Usually I would prefer a @ModifyExpressionValue , but this class requires BuildCraft to parse with that way of injecting (which is usually removed at runtime by DAPIs DependentAnnotationStripper).
     */
    @WrapOperation(method = "getWailaBody", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/IFluidHandler;getTankInfo(Lnet/minecraftforge/common/util/ForgeDirection;)[Lnet/minecraftforge/fluids/FluidTankInfo;"))
    public FluidTankInfo[] disableWailaTip(IFluidHandler instance, ForgeDirection forgeDirection, Operation<FluidTankInfo[]> original) {
        return null;
    }
}
