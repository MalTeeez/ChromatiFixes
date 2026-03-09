package cc.unilock.chromatifixes.mixin.late.perfblockplace.streams;

import Reika.RotaryCraft.TileEntities.Engine.TileEntityHydroEngine;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import streams.block.BlockRiver;

@Mixin(value = BlockRiver.class, remap = false)
public class BlockRiverMixin {

    @Inject(
        method = "breakBlock(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V",
        at = @At(value = "INVOKE", target = "Lfarseek/world/package$;setBlockAt(Lscala/Tuple3;Lnet/minecraft/block/Block;IZLnet/minecraft/world/IBlockAccess;Lfarseek/world/BlockWriteAccess;Lfarseek/world/CoordinateSystem;)Z", shift = At.Shift.AFTER),
        remap = false
    )
    private void onBreakBlockStream$chromatifixes(World w, int x, int y, int z, Block block, int data, CallbackInfo ci) {
        updateStreamHydros$chromatifixes(w, x, y, z);
    }

    @Inject(
        method = "onNeighborBlockChange",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;scheduleBlockUpdate(IIILnet/minecraft/block/Block;I)V", shift = At.Shift.AFTER),
        remap = false
    )
    private void onNeighborBlockChangeStream$chromatifixes(World w, int x, int y, int z, Block block, CallbackInfo ci) {
        updateStreamHydros$chromatifixes(w, x, y, z);
    }

    @Unique
    private void updateStreamHydros$chromatifixes(World world, int x, int y, int z) {
        for (int i = -1; i <= 1; i++) {
            for (int k = -1; k <= 1; k++) {
                int chunkXPos = (x + i) >> 4;
                int chunkZPos = (z + k) >> 4;
                for (TileEntity te : world.getChunkFromChunkCoords(chunkXPos + i, chunkZPos + k).chunkTileEntityMap.values()) {
                    if (!te.isInvalid() && te instanceof TileEntityHydroEngine) {
                        ((TileEntityHydroEngine)te).invalidateStream();
                    }
                }
            }
        }
    }
}
