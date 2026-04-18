package cc.unilock.chromatifixes.mixin.late.chromaticraft;

import Reika.ChromatiCraft.World.Nether.NetherDiorama;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Catch a null lgc before setting the attributes on it. I know this is messy, but still better than an overwrite and an added AT.
 */
@Mixin(value = NetherDiorama.class, remap = false)
public class NetherDioramaMixin {

    @WrapWithCondition(
        method = "generateSpawner",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;activatingRangeFromPlayer:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private static boolean generateSpawner$setActivationRange(MobSpawnerBaseLogic lgc, int value) {
        return lgc != null;
    }

    @WrapWithCondition(
        method = "generateSpawner",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;maxNearbyEntities:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private static boolean generateSpawner$setMaxNearbyEntities(MobSpawnerBaseLogic lgc, int value) {
        return lgc != null;
    }

    @WrapWithCondition(
        method = "generateSpawner",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;minSpawnDelay:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private static boolean generateSpawner$setMinSpawnDelay(MobSpawnerBaseLogic lgc, int value) {
        return lgc != null;
    }

    @WrapWithCondition(
        method = "generateSpawner",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;maxSpawnDelay:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private static boolean generateSpawner$setMaxSpawnDelay(MobSpawnerBaseLogic lgc, int value) {
        return lgc != null;
    }

    @WrapWithCondition(
        method = "generateSpawner",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/tileentity/MobSpawnerBaseLogic;spawnDelay:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private static boolean generateSpawner$setSpawnDelay(MobSpawnerBaseLogic lgc, int value) {
        return lgc != null;
    }
}
