package cc.unilock.chromatifixes.mixin.late.geostrata.worldgen;

import Reika.GeoStrata.World.ArcticSpiresGenerator;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ArcticSpiresGenerator.class, remap = false)
public class ArcticSpiresGeneratorMixin {

    /**
     * Don't set the biome if the chunk isn't loaded, to avoid loading more cascading chunks, and trying to set
     * the biome for a chunk that doesn't exist yet.
     * <p>
     * This mixin goes with {@link ArcticSpiresGeneratorArcticSpireMixin}, which, without this check leads to not-generated chunks failing on biome set.
     */
    @WrapOperation(
        method = "generateCluster(Lnet/minecraft/world/World;IILjava/util/Random;ID)I",
        at = @At(value = "INVOKE", target = "LReika/DragonAPI/Libraries/World/ReikaWorldHelper;setBiomeForXZ(Lnet/minecraft/world/World;IILnet/minecraft/world/biome/BiomeGenBase;Z)V")
    )
    private void setBiomeForXZ$chromatifixes(World world, int dx, int dz, BiomeGenBase biome, boolean applyEnvironment, Operation<Void> original) {
        if (world.getChunkProvider().chunkExists(dx >> 4, dz >> 4)) {
            original.call(world, dx, dz, biome, applyEnvironment);
        }
    }
}
