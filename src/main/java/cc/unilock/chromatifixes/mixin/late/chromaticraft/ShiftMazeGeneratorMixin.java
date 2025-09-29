package cc.unilock.chromatifixes.mixin.late.chromaticraft;

import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.ShiftMaze.BlockShiftLock;
import Reika.ChromatiCraft.World.Dimension.Structure.ShiftMazeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ShiftMazeGenerator.class, remap = false)
public class ShiftMazeGeneratorMixin {

    @Shadow
    private List<Coordinate> toggleDoors;

    @Inject(method = "openStructure", at = @At(value = "FIELD", target = "LReika/ChromatiCraft/World/Dimension/Structure/ShiftMazeGenerator;toggleDoors:Ljava/util/List;"), cancellable = true)
    public void safeOpenStructure(World world, CallbackInfo ci) {
        for (Coordinate c : this.toggleDoors) {
            TileEntity te = c.getTileEntity(world);
            if (te instanceof BlockChromaDoor.TileEntityChromaDoor) {
                ((BlockChromaDoor.TileEntityChromaDoor) te).open(-1);
            } else {
                world.setBlockMetadataWithNotify(c.xCoord, c.yCoord, c.zCoord, BlockShiftLock.Passability.OPEN.ordinal(), 2);
            }
        }
        ci.cancel();
    }
}
