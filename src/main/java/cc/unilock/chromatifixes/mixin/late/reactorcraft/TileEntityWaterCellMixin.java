package cc.unilock.chromatifixes.mixin.late.reactorcraft;

import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.TileEntities.Fission.TileEntityWaterCell;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TileEntityWaterCell.class, remap = false)
public class TileEntityWaterCellMixin {
    // The simulated drain gates the real one; hiding anything but water/D2O stops the cell draining and voiding it
    @ModifyExpressionValue(method = "updateEntity", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/IFluidHandler;drain(Lnet/minecraftforge/common/util/ForgeDirection;IZ)Lnet/minecraftforge/fluids/FluidStack;", ordinal = 0))
    private FluidStack onlyDrainCoolant(FluidStack liq) {
        return liq != null && (liq.getFluid() == FluidRegistry.WATER || liq.getFluid() == ReactorCraft.D2O) ? liq : null;
    }
}
