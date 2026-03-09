package cc.unilock.chromatifixes.mixin.late.perfblockplace.thaumcraft;

import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.common.lib.world.WorldGenHilltopStones;

import java.util.Random;

@Mixin(value = WorldGenHilltopStones.class, remap = false)
public class WorldGenHilltopStonesCCMixin {

    // https://github.com/ReikaKalseki/ChromatiCraft/blob/1a5e514d61630995b6c0477368adfd7b0f91887c/Auxiliary/ChromaAux.java#L209
    @Inject(
        method = "generate",
        at = @At("TAIL"),
        remap = false
    )
    private void onGenerateTCHilltopStones$chromatifixes(World world, Random rand, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        if (BiomeGlowingCliffs.isGlowingCliffs(world.getBiomeGenForCoords(i, k))) {
            TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(i, j + 1, k);
            if (spawner != null) {
                spawner.func_145881_a().setEntityName("ChromatiCraft.GlowCloud");
            }
        }
    }
}
