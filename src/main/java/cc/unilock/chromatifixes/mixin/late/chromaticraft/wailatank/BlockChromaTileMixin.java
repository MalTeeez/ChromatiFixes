package cc.unilock.chromatifixes.mixin.late.chromaticraft.wailatank;

import Reika.ChromatiCraft.Base.BlockChromaTile;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraftforge.fluids.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BlockChromaTile.class, remap = false)
public class BlockChromaTileMixin {

    @Definition(id = "IFluidHandler", type = IFluidHandler.class)
    @Expression("? instanceof IFluidHandler")
    @ModifyExpressionValue(
        method = "getWailaBody",
        at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private boolean disableWailaFluidTooltip(boolean original) {
        return false;
    }

    @Definition(id = "TileEntityCrystalTank", type = TileEntityCrystalTank.class)
    @Expression("? instanceof TileEntityCrystalTank")
    @ModifyExpressionValue(
        method = "getWailaBody",
        at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private boolean disableWailaCrystalTankTooltip(boolean original) {
        return false;
    }
}
