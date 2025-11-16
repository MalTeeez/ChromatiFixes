package cc.unilock.chromatifixes.mixin.late.chromaticraft.wailatank;

import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import Reika.DragonAPI.Instantiable.FlaggedTank;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TileEntityCrystalTank.class, remap = false)
public abstract class TileEntityCrystalTankMixin {

    @Shadow
    @Final
    private FlaggedTank tank;

    @Shadow
    public abstract int getCapacity();

    @WrapOperation(method = "getTankInfo", at = @At(value = "INVOKE", target = "LReika/DragonAPI/Instantiable/FlaggedTank;getInfo()Lnet/minecraftforge/fluids/FluidTankInfo;"))
    public FluidTankInfo getTankInfoWithCorrectCapacity(FlaggedTank instance, Operation<FluidTankInfo> original) {
        return new FluidTank(tank.getFluid(), this.getCapacity()).getInfo();
    }
}
