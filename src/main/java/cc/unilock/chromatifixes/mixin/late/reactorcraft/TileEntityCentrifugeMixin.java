package cc.unilock.chromatifixes.mixin.late.reactorcraft;

import Reika.ReactorCraft.TileEntities.Processing.TileEntityCentrifuge;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = TileEntityCentrifuge.class, remap = false)
public class TileEntityCentrifugeMixin {
    /**
     * @author gamrguy
     * @reason Items could only exit top/bottom, which are taken by the fluid input and power shaft; allow every side
     */
    @Overwrite
    public boolean canItemExitToSide(ForgeDirection dir) {
        return true;
    }
}
