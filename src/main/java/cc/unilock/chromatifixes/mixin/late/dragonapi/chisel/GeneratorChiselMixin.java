package cc.unilock.chromatifixes.mixin.late.dragonapi.chisel;

import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.chisel.world.GeneratorChisel;

import java.util.Random;

@Mixin(value = GeneratorChisel.class, remap = false, priority = 1001)
public class GeneratorChiselMixin {

    @Inject(
        method = "generate",
        at = @At(value = "HEAD")
    )
    private void onGenerate$Head$chromatifixes(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) {
        WorldGenInterceptionRegistry.skipLighting = true;
    }

    @Inject(
        method = "generate",
        at = @At(value = "TAIL"),
        require = 1
    )
    private void onGenerate$Tail$chromatifixes(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) {
        WorldGenInterceptionRegistry.skipLighting = false;
    }
}
