package cc.unilock.chromatifixes.mixin.early.perfblockplace;

import Reika.ChromatiCraft.Magic.Network.CrystalPath;
import Reika.ChromatiCraft.Magic.Network.PathNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;

@Mixin(value = CrystalPath.class, remap = false)
public interface CrystalPathAccessor {

    @Accessor("nodes")
    ArrayList<PathNode> getNodes();
}
