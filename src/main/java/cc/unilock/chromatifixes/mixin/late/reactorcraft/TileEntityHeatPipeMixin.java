package cc.unilock.chromatifixes.mixin.late.reactorcraft;

import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.ReactorCraft.Auxiliary.ReactorTyped;
import Reika.ReactorCraft.Base.TileEntityNuclearBoiler;
import Reika.ReactorCraft.Registry.ReactorType;
import Reika.ReactorCraft.TileEntities.TileEntityHeatPipe;
import Reika.RotaryCraft.Auxiliary.Interfaces.HeatConduction;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityHeatPipe.class, remap = false)
public abstract class TileEntityHeatPipeMixin {
    @Shadow @Final private static double HEAT_CAPACITY;
    @Shadow private double heatEnergy;
    @Shadow private Proportionality<ReactorType> reactorTypes;

    @Shadow private void ventHeat(World world, int x, int y, int z) {}
    @Shadow private void balanceWith(TileEntityHeatPipe other) {}
    @Shadow protected abstract boolean canConnectToMachine(Block id, int meta, ForgeDirection dir, TileEntity te);

    // Reika left the 32-tick vent call commented out; without it pipes never cool or show their temperature
    @Inject(method = "updateEntity", at = @At("TAIL"))
    private void ventPeriodically(World world, int x, int y, int z, int meta, CallbackInfo ci) {
        if (!world.isRemote && ((TileEntityHeatPipe) (Object) this).getTicksExisted() % 32 == 0) {
            ventHeat(world, x, y, z);
        }
    }

    // heatEnergy is already loaded from NBT by now; only initialize freshly placed pipes
    @Inject(method = "onFirstTick", at = @At("HEAD"), cancellable = true)
    private void keepSavedHeat(World world, int x, int y, int z, CallbackInfo ci) {
        if (heatEnergy != 0) {
            ci.cancel();
        }
    }

    /**
     * @author gamrguy, thegamemaster1234
     * @reason Original sets the neighbor to (transfer amount) degrees, voiding its heat, and adds negative reactor typing.
     * Heat is exchanged in degrees (one pipe degree = HEAT_CAPACITY) because reactor blocks conduct among themselves
     * degree-for-degree; pricing them by heatEnergyPerDegree() (x50 for cores, x1 for the CPU) lets a pipe touching
     * a core and a CPU pump heat forever.
     */
    @Overwrite
    private void balanceHeat(World world, int x, int y, int z) {
        TileEntityHeatPipe self = (TileEntityHeatPipe) (Object) this;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = self.getAdjacentTileEntity(dir);
            if (te instanceof TileEntityHeatPipe pipe) {
                balanceWith(pipe);
            } else if (te instanceof WorldRift rift) {
                if (rift.getTileEntityFrom(dir) instanceof TileEntityHeatPipe pipe) {
                    balanceWith(pipe);
                }
            } else if (te != null && canConnectToMachine(te.getBlockType(), te.getBlockMetadata(), dir, te)) {
                HeatConduction hc = (HeatConduction) te;
                double ours = TileEntityHeatPipe.getTemperatureForPipe(self, true);
                double theirs = TileEntityHeatPipe.getNetTemperature(hc);
                boolean intake = theirs > ours;
                if (intake ? !hc.allowHeatExtraction() : !(ours > theirs && hc.allowExternalHeating())) {
                    continue;
                }

                hc.setTemperature((int) (theirs + (ours - theirs) / 4) + hc.getAmbientTemperature());
                // Measure what actually moved so truncation or a clamping setTemperature can't create heat
                double moved = TileEntityHeatPipe.getNetTemperature(hc) - theirs;
                heatEnergy -= moved * HEAT_CAPACITY;

                if (intake) {
                    if (te instanceof ReactorTyped typed && typed.getReactorType() != null) {
                        reactorTypes.addValue(typed.getReactorType(), -moved);
                    }
                } else if (te instanceof TileEntityNuclearBoiler boiler) {
                    // Original re-adds the pipe's whole accumulated typing every tick; add only what was delivered
                    for (ReactorType type : reactorTypes.getElements()) {
                        boiler.setReactorType(type, moved * reactorTypes.getFraction(type));
                    }
                }
            }
        }
    }
}
