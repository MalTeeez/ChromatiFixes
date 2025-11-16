package cc.unilock.chromatifixes.mixin.late.chromaticraft.wailatank;

import Reika.ChromatiCraft.Block.BlockCrystalTank;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BlockCrystalTank.class, remap = false)
public class BlockCrystalTankMixin {

    @Inject(method = "getWailaBody", at = @At(value = "HEAD"), cancellable = true)
    void disableDefaultWailaBody(ItemStack is, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler cfg, CallbackInfoReturnable<List<String>> cir) {
        cir.setReturnValue(currenttip);
    }
}
