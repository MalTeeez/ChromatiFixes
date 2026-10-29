package cc.unilock.chromatifixes.mixin.late.reactorcraft;

import Reika.ReactorCraft.Registry.ReactorBlocks;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamGrate;
import Reika.ReactorCraft.TileEntities.PowerGen.TileEntityTurbineCore;
import cc.unilock.chromatifixes.mixin.late.reactorcraft.accessor.TileEntitySteamGrateAccessor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityTurbineCore.class, remap = false)
public class TileEntityTurbineCoreMixin {
    // Steam below the intake lives 2 ticks and is removed by its block tick before tile entities update. A turbine
    // ticking before its grate only saw steam every other tick and ran at a quarter of the speed, depending on tile
    // order. Letting the grate place its steam first makes every turbine behave like the grate ticked first
    @Inject(method = "intakeSteam", at = @At("HEAD"))
    private void placeGrateSteamFirst(World world, int x, int y, int z, int meta, CallbackInfoReturnable<Boolean> cir) {
        if (world.isRemote) return;
        TileEntity te = world.getTileEntity(x, y - 2, z);
        if (!(te instanceof TileEntitySteamGrate)) return;
        TileEntitySteamGrateAccessor grate = (TileEntitySteamGrateAccessor) te;
        if (grate.invokeCanMakeSteam(world, x, y - 2, z)) {
            grate.setSteam(grate.getSteam() - 1);
            world.setBlock(x, y - 1, z, ReactorBlocks.STEAM.getBlockInstance(), grate.invokeGetSteamMetadata(), 3);
        }
    }
}
