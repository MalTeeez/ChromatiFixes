package cc.unilock.chromatifixes.mixin.late.perfblockplace.tinkersconstruct;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tconstruct.world.gen.SlimeIslandGen;

import java.util.Random;

@Mixin(value = SlimeIslandGen.class, remap = false)
public class SlimeIslandGenMixin {

    // https://github.com/ReikaKalseki/ChromatiCraft/blob/1a5e514d61630995b6c0477368adfd7b0f91887c/Auxiliary/ChromaAux.java#L217
    // https://github.com/ReikaKalseki/Satisforestry/blob/caeb3eece31286b953b14dbfe2a8570196376835/SFAux.java#L57
    @Inject(
        method = "generate(Ljava/util/Random;IILnet/minecraft/world/World;Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;)V",
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    private void generateSlimeIsland$chromatifixes(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) {
        String biome = world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16).biomeName;
        if (biome.equals("Pink Birch Forest") || biome.equals("Luminous Cliffs Shores") || biome.equals("Luminous Cliffs")) {
            ci.cancel();
        }
    }
}
