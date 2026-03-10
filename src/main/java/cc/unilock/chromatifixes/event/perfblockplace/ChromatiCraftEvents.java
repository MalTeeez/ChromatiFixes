package cc.unilock.chromatifixes.event.perfblockplace;

import Reika.ChromatiCraft.Block.BlockFakeSky;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalBroadcaster;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent;

public class ChromatiCraftEvents {

    // https://github.com/ReikaKalseki/ChromatiCraft/blob/1a5e514d61630995b6c0477368adfd7b0f91887c/ChromaticEventManager.java#L679
    @SubscribeEvent
    public void updateFakeSkyBlocks$Place$chromatifixes(BlockEvent.PlaceEvent event) {
        BlockFakeSky.updateColumn(event.world, event.x, event.y, event.z);
    }
    @SubscribeEvent
    public void updateFakeSkyBlocks$Break$chromatifixes(BlockEvent.BreakEvent event) {
        BlockFakeSky.updateColumn(event.world, event.x, event.y, event.z);
    }

    /**
     * https://github.com/ReikaKalseki/ChromatiCraft/blob/1a5e514d61630995b6c0477368adfd7b0f91887c/ChromaticEventManager.java#L810
     *
     * Since this a very rare situation in general gameplay, and usually only caused by players building, we only trigger
     * it on block place and break events, which should be the main cause of not enough air-space around an already built
     * broadcaster. Also, if something does obstruct too much air-space around a broadcaster, it is unlikely to be noticed
     * by the player.
      */
    @SubscribeEvent
    public void reloadBroadcastAirCache$Place$chromatifixes(BlockEvent.PlaceEvent event) {
        TileEntityCrystalBroadcaster.updateAirCaches(event.world, event.x, event.y, event.z);
    }
    @SubscribeEvent
    public void reloadBroadcastAirCache$Break$chromatifixes(BlockEvent.BreakEvent event) {
        TileEntityCrystalBroadcaster.updateAirCaches(event.world, event.x, event.y, event.z);
    }
}
