package cc.unilock.chromatifixes.mixin.early.perfblockplace;

import Reika.ChromatiCraft.Magic.Network.CrystalLink;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = CrystalLink.class, remap = false)
public interface CrystalLinkAccessor {

    @Accessor("needsCalculation")
    void setNeedsCalculation(boolean value);

    @Invoker("hasLineOfSight")
    boolean invokeHasLineOfSight();

    @Invoker("recalculateLOS")
    void invokeRecalculateLOS();
}
