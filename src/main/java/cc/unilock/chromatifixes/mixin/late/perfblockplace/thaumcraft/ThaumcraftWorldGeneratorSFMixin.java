package cc.unilock.chromatifixes.mixin.late.perfblockplace.thaumcraft;

import Reika.Satisforestry.Satisforestry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;
import thaumcraft.common.tiles.TileNode;

import java.util.Random;

@Mixin(value = ThaumcraftWorldGenerator.class, remap = false)
public class ThaumcraftWorldGeneratorSFMixin {

    // https://github.com/ReikaKalseki/Satisforestry/blob/caeb3eece31286b953b14dbfe2a8570196376835/SFAux.java#L51
    @Inject(
        method = "generateTotem",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void generateTCTotem$chromatifixesSF(World world, Random random, int chunkX, int chunkZ, boolean auraGen, boolean newGen, CallbackInfoReturnable<Boolean> cir) {
        if (Satisforestry.isPinkForest(world.getBiomeGenForCoords(chunkX * 16, chunkZ * 16))) {
            cir.setReturnValue(false);
        }
    }

    // https://github.com/ReikaKalseki/Satisforestry/blob/caeb3eece31286b953b14dbfe2a8570196376835/SFAux.java#L60
    @Inject(
        method = "createNodeAt",
        at = @At("TAIL"),
        remap = false
    )
    private static void onCreateNodeAt(World world, int x, int y, int z, NodeType nt, NodeModifier nm, AspectList al, CallbackInfo ci) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileNode node)) return;

        if (Satisforestry.isPinkForest(world.getBiomeGenForCoords(x, z))) {
            node.setNodeType(NodeType.NORMAL);
        }
    }
}
