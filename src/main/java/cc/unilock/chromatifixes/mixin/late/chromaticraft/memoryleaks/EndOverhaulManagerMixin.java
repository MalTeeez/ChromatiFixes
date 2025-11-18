package cc.unilock.chromatifixes.mixin.late.chromaticraft.memoryleaks;

import Reika.ChromatiCraft.World.EndOverhaulManager;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldChunk;
import Reika.DragonAPI.Instantiable.Data.Maps.NestedMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = EndOverhaulManager.class, remap = false)
public class EndOverhaulManagerMixin {

    @WrapOperation(method = "generate", at = @At(value = "INVOKE", target = "LReika/DragonAPI/Instantiable/Data/Maps/NestedMap;getMap(Ljava/lang/Object;)Ljava/util/Map;"))
    private Map<Coordinate, BlockKey> removeMapIfNotNull(NestedMap<WorldChunk, Coordinate, BlockKey> instance, Object chunk, Operation<Map<Coordinate, BlockKey>> original) {
        Map<Coordinate, BlockKey> map = original.call(instance, chunk);
        if (map != null) {
            instance.remove((WorldChunk) chunk);
        }
        return map;
    }
}
