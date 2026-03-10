package cc.unilock.chromatifixes.mixin.early.perfblockplace;

import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Magic.Network.CrystalLink;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.HashSet;

@Mixin(value = CrystalNetworker.class, remap = false)
public interface CrystalNetworkerAccessor {

    @Accessor("losCache")
    MultiMap<WorldChunk, CrystalLink> getLosCache();

    @Accessor("flows")
    MultiMap<Integer, CrystalFlow> getFlows();

    @Accessor("toBreak")
    HashSet<CrystalFlow> getToBreak();

    @Invoker("schedulePathBreak")
    void invokeSchedulePathBreak(CrystalFlow flow);
}
