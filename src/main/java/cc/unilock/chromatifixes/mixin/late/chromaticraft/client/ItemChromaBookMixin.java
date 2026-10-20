package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.ChromatiCraft.Items.Tools.ItemChromaBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// resetGuis() only resets the navigation scroll offset; skip it so the map stays where it was left
@Mixin(value = ItemChromaBook.class, remap = false)
public class ItemChromaBookMixin {
    @Inject(method = "resetGuis", at = @At("HEAD"), cancellable = true)
    private void keepScrollPosition(CallbackInfo ci) {
        ci.cancel();
    }
}
