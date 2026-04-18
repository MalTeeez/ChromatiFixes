package cc.unilock.chromatifixes.mixin.late.chromaticraft;

import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest;
import Reika.ChromatiCraft.Registry.ChromaStructures;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(value = ChromaAux.class, remap = false)
public class ChromaAuxMixin {

    /**
     * Check if te we retrieved from world is actually already present. Seems to be caused by some conflicts with hodgepodge.
     */
    @WrapOperation(
        method = "generateLootChest",
        at =
    @At(value = "INVOKE", target = "LReika/ChromatiCraft/Block/Worldgen/BlockLootChest$TileEntityLootChest;populateChest(Ljava/lang/String;LReika/ChromatiCraft/Registry/ChromaStructures;ILjava/util/Random;)V")
    )
    private static void generateLootChestWithCheck(BlockLootChest.TileEntityLootChest instance, String s, ChromaStructures struct, int bonus, Random r, Operation<Void> original) {
        if (instance != null) {
            instance.populateChest(s, null , bonus, r);
        }
    }
}
