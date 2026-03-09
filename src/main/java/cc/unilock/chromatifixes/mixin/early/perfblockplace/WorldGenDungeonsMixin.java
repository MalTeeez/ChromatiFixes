package cc.unilock.chromatifixes.mixin.early.perfblockplace;

import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(WorldGenDungeons.class)
public class WorldGenDungeonsMixin {

    // https://github.com/ReikaKalseki/ChromatiCraft/blob/1a5e514d61630995b6c0477368adfd7b0f91887c/Auxiliary/ChromaAux.java#L183
    @Inject(
        method = "generate",
        at = @At("TAIL"),
        require = 1
    )
    private void onGenerateDungeon$chromatifixes(World world, Random rand, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        String biomeName = world.getBiomeGenForCoords(x, z).biomeName;
        if (biomeName.equals("Rainbow Forest")) {
            TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(x, y, z);
            if (spawner == null) return;
            MobSpawnerBaseLogic lgc = spawner.func_145881_a();
            lgc.activatingRangeFromPlayer = lgc.getEntityNameToSpawn().equals("CaveSpider") && lgc.getSpawnerY() < 55 ? 12 : 6;
            lgc.minSpawnDelay *= 2;
            lgc.maxSpawnDelay *= 4;
        }
    }

}
