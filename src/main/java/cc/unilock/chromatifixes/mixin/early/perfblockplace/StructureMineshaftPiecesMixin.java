package cc.unilock.chromatifixes.mixin.early.perfblockplace;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(StructureMineshaftPieces.Corridor.class)
public class StructureMineshaftPiecesMixin {

    @WrapOperation(
        method = "addComponentParts",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;setEntityName(Ljava/lang/String;)V"),
        require = 1
    )
    private void wrapSetEntityName$chromatifixes(MobSpawnerBaseLogic lgc, String name, Operation<Void> original, World world, Random rand, StructureBoundingBox box) {
        original.call(lgc, name);
        String biomeName = world.getBiomeGenForCoords(lgc.getSpawnerX(), lgc.getSpawnerZ()).biomeName;
        if (biomeName.equals("Rainbow Forest")) {
            lgc.activatingRangeFromPlayer = lgc.getEntityNameToSpawn().equals("CaveSpider") && lgc.getSpawnerY() < 55 ? 12 : 6;
            lgc.minSpawnDelay *= 2;
            lgc.maxSpawnDelay *= 4;
        }
    }
}
