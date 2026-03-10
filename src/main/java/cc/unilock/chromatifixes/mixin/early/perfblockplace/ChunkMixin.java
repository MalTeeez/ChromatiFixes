package cc.unilock.chromatifixes.mixin.early.perfblockplace;

import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Magic.Network.CrystalLink;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.HashSet;


/**
 * https://github.com/ReikaKalseki/ChromatiCraft/blob/master/Magic/Network/CrystalNetworker.java#L121
 *
 * This is sadly the only part where we still have to rely on the global block set, and cant narrow it down to anything specific.
 */
@Mixin(value = Chunk.class)
public class ChunkMixin {

    @Final
    @Shadow
    private World worldObj;
    @Final
    @Shadow
    private int xPosition;
    @Final
    @Shadow
    private int zPosition;
    @Final
    @Shadow
    private boolean isTerrainPopulated;

    @ModifyReturnValue(
        method = "func_150807_a",
        at = @At("RETURN"),
        require = 1
    )
    private boolean onSetBlock$CCPylonLinks$chromatifixes(boolean original, int x, int y, int z, Block block, int meta) {
        if (original && !this.worldObj.isRemote && this.isTerrainPopulated) {
            markChunkCacheCC$chromatifixes(this.worldObj, this.xPosition * 16 + x, y, this.zPosition * 16 + z);
        }
        return original;
    }

    @ModifyReturnValue(
        method = "setBlockMetadata",
        at = @At("RETURN"),
        require = 1
    )
    private boolean onSetBlockMetadata$CCPylonLinks$chromatifixes(boolean original, int x, int y, int z, int meta) {
        if (original && !this.worldObj.isRemote && this.isTerrainPopulated) {
            markChunkCacheCC$chromatifixes(this.worldObj, this.xPosition * 16 + x, y, this.zPosition * 16 + z);
        }
        return original;
    }

    @Unique
    public void markChunkCacheCC$chromatifixes(World world, int x, int y, int z) {
        WorldChunk wc = new WorldChunk(world.provider.dimensionId, new ChunkCoordIntPair(x >> 4, z >> 4));

        if (((CrystalNetworkerAccessor) CrystalNetworker.instance).getLosCache().containsKey(wc)) {
            CrystalNetworkerAccessor networker = ((CrystalNetworkerAccessor) CrystalNetworker.instance);
            Collection<CrystalLink> crystalLinks = networker.getLosCache().get(wc);
            Coordinate loc = new Coordinate(x, y, z);
            MultiMap<Integer, CrystalFlow> flows = networker.getFlows();
            HashSet<CrystalFlow> toBreak = networker.getToBreak();

            for (CrystalLink link : crystalLinks) {
                if (link == null) continue;

                CrystalLinkAccessor linkAccessor = ((CrystalLinkAccessor)(Object)link);
                linkAccessor.setNeedsCalculation(true);

                if (!flows.get(world.provider.dimensionId).isEmpty()) {
                    boolean los = linkAccessor.invokeHasLineOfSight();
                    if (!los && link.containsBlock(loc)) {
                        for (CrystalFlow path : flows.get(world.provider.dimensionId)) {
                            if (!toBreak.contains(path) && path.containsLink(link)) {
                                // No need to call p.checkLineOfSight(link) — we already confirmed LOS is broken
                                CrystalNetworkLogger.logFlowBreak(path, CrystalNetworkLogger.FlowFail.SIGHT);
                                networker.invokeSchedulePathBreak(path);
                            }
                        }
                    }
                }
            }
        }
    }
}
