package cc.unilock.chromatifixes.event.perfblockplace;


import Reika.RotaryCraft.TileEntities.Weaponry.TileEntityEMP;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent;

public class RotaryCraftEvents {

    // https://github.com/ReikaKalseki/RotaryCraft/blob/master/RotaryEventManager.java#L272
    @SubscribeEvent
    public void applyEMPEffectsPlace$chromatifixes(BlockEvent.PlaceEvent event) {
        TileEntityEMP.resetCoordinate(event.world, event.x, event.y, event.z);
    }
    @SubscribeEvent
    public void applyEMPEffectsBreak$chromatifixes(BlockEvent.BreakEvent event) {
        TileEntityEMP.resetCoordinate(event.world, event.x, event.y, event.z);
    }

}
