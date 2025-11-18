package cc.unilock.chromatifixes.mixin.late.chromaticraft.memoryleaks;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Maps.NestedMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "Reika.ChromatiCraft.World.EndOverhaulManager$Tendril", remap = false)
public class EndTendrilMixin {

    @WrapOperation(method = "calculate", at = @At(value = "INVOKE", target = "LReika/DragonAPI/Instantiable/Data/Maps/NestedMap;put(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    public Object dontRestoreGeneratedChunks(NestedMap<WorldChunk, Coordinate, BlockKey> instance, Object chunk, Object location, Object value, Operation<BlockKey> original, @Local(ordinal = 0, argsOnly = true) World world) {
        if (world.getChunkProvider().chunkExists(((WorldChunk) chunk).chunk.chunkXPos, ((WorldChunk) chunk).chunk.chunkZPos)) {
            return null;
        }
        return original.call(instance, chunk, location, value);
    }
}
