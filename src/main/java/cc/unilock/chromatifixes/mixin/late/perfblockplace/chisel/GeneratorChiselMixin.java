package cc.unilock.chromatifixes.mixin.late.perfblockplace.chisel;

import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.chisel.world.GeneratorChisel;

import java.util.Random;

@Mixin(value = GeneratorChisel.class, remap = false)
public class GeneratorChiselMixin {

    /**
     * https://github.com/ReikaKalseki/ChromatiCraft/blob/1a5e514d61630995b6c0477368adfd7b0f91887c/Auxiliary/ChromaAux.java#L206
     *
     * Although the original only prevents generation that would replace cliffstone, here we prevent all chisel gen in the Glowing Cliffs.
     *
     * Generally done since chisel gen below ground is not very noticeable (especially for this biome)
     * and checking the individual blocks would be way more expensive and complicated, completely negating why we are doing this.
     */
    @Inject(
        method = "generate",
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    public void onGenerateChiselGen$chromatifixes(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) {
        if (BiomeGlowingCliffs.isGlowingCliffs(world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16))) {
            ci.cancel();
        }
    }
}
