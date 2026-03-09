package cc.unilock.chromatifixes.mixin.late.perfblockplace.thaumcraft;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import java.util.HashSet;
import java.util.Random;

@Mixin(value = ThaumcraftWorldGenerator.class, remap = false)
public class ThaumcraftWorldGeneratorCCMixin {

    // https://github.com/ReikaKalseki/ChromatiCraft/blob/1a5e514d61630995b6c0477368adfd7b0f91887c/Auxiliary/ChromaAux.java#L179
    @Inject(
        method = "generateTotem",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void generateTCTotem$chromatifixesCC(World world, Random random, int chunkX, int chunkZ, boolean auraGen, boolean newGen, CallbackInfoReturnable<Boolean> cir) {
        if (ChromatiCraft.isRainbowForest(world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "createNodeAt",
        at = @At("TAIL"),
        remap = false
    )
    private static void onCreateTCNodeAt$chromatifixesCC(World world, int x, int y, int z, NodeType nt, NodeModifier nm, AspectList al, CallbackInfo ci) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof INode node)) return;

        if (ChromatiCraft.isRainbowForest(biome) && !(te instanceof TileEntityCrystalPylon)) {
            // https://github.com/ReikaKalseki/ChromatiCraft/blob/1a5e514d61630995b6c0477368adfd7b0f91887c/Auxiliary/ChromaAux.java#L190
            node.setNodeType(NodeType.NORMAL);
            node.setNodeModifier(NodeModifier.BRIGHT);
            if (world.rand.nextInt(4) == 0) {
                float f = 2 + world.rand.nextFloat() * 4;
                AspectList aspects = node.getAspects();
                for (Aspect a : new HashSet<>(aspects.aspects.keySet())) {
                    aspects.aspects.put(a, (int) (f * aspects.getAmount(a)));
                }
            }
        } else if (BiomeGlowingCliffs.isGlowingCliffs(biome)) {
            // https://github.com/ReikaKalseki/ChromatiCraft/blob/1a5e514d61630995b6c0477368adfd7b0f91887c/Auxiliary/ChromaAux.java#L220
            node.setNodeType(NodeType.NORMAL);
            node.setNodeModifier(NodeModifier.BRIGHT);
            if (world.rand.nextInt(4) == 0) {
                float f = 1 + world.rand.nextFloat() * 2;
                AspectList aspects = node.getAspects();
                for (Aspect a : new HashSet<>(aspects.aspects.keySet())) {
                    aspects.aspects.put(a, (int) (f * aspects.getAmount(a)));
                }
            }
        }
    }
}
