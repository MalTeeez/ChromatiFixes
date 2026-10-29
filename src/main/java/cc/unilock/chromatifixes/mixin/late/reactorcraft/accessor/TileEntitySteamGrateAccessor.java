package cc.unilock.chromatifixes.mixin.late.reactorcraft.accessor;

import Reika.ReactorCraft.TileEntities.PowerGen.TileEntitySteamGrate;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = TileEntitySteamGrate.class, remap = false)
public interface TileEntitySteamGrateAccessor {
    @Accessor("steam")
    int getSteam();

    @Accessor("steam")
    void setSteam(int steam);

    @Invoker("canMakeSteam")
    boolean invokeCanMakeSteam(World world, int x, int y, int z);

    @Invoker("getSteamMetadata")
    int invokeGetSteamMetadata();
}
