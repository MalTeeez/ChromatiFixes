package cc.unilock.chromatifixes.mixin.late.chromaticraft.worldgen;

import Reika.ChromatiCraft.World.IWG.GlowingCliffsAuxGenerator;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GlowingCliffsAuxGenerator.Island.class, remap = false)
public class GlowingCliffsAuxGeneratorMixin {

    @WrapOperation(
        method = "placeBlocks",
        at = @At(value = "INVOKE", target = "LReika/DragonAPI/Instantiable/Data/Immutable/BlockKey;place(Lnet/minecraft/world/World;IIII)V")
    )
    public void placeWithNoDapiLighting(BlockKey instance, World world, int x, int y, int z, int flags, Operation<Void> original) {
        try {
            WorldGenInterceptionRegistry.skipLighting = true;
            original.call(instance, world, x, y, z, flags);
        } finally {
            WorldGenInterceptionRegistry.skipLighting = false;
        }
    }
}
