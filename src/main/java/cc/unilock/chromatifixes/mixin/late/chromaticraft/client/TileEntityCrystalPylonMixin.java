package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = TileEntityCrystalPylon.class, remap = false)
public class TileEntityCrystalPylonMixin {

    // A target sharing the pylon's block has no direction to fire in, and divides by zero to NaN the position
    @ModifyArgs(
        method = "particleAttack",
        at = @At(
            value = "INVOKE",
            target = "LReika/ChromatiCraft/Render/Particle/EntityFlareFX;<init>"
                + "(LReika/ChromatiCraft/Registry/CrystalElement;Lnet/minecraft/world/World;DDDDDD)V"
        )
    )
    private void ccfixes$guardZeroLengthAttack(Args args, int sx, int sy, int sz, int x, int y, int z) {
        if (ReikaMathLibrary.py3d(x - sx, y - sy, z - sz) > 0) {
            return;
        }

        args.set(5, 0.0);
        args.set(6, 0.0);
        args.set(7, 0.0);
    }
}
