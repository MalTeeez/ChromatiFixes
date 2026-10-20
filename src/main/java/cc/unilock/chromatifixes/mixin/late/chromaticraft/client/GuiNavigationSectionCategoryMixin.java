package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.DragonAPI.Instantiable.Data.Maps.RegionMap;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import cc.unilock.chromatifixes.client.LexiconZoom;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Hover and click regions are computed in unscaled coordinates; map them to screen space while zoomed
@Mixin(targets = "Reika.ChromatiCraft.GUI.Book.GuiNavigation$SectionCategory", remap = false)
public class GuiNavigationSectionCategoryMixin {
    @WrapOperation(method = "renderElements", at = @At(value = "INVOKE", target = "LReika/DragonAPI/Libraries/Rendering/ReikaGuiAPI;isMouseInBox(IIII)Z"))
    private boolean hoverZoomed(ReikaGuiAPI api, int minX, int maxX, int minY, int maxY, Operation<Boolean> original) {
        if (!LexiconZoom.drawing) {
            return original.call(api, minX, maxX, minY, maxY);
        }
        return original.call(api, LexiconZoom.toScreenX(minX), LexiconZoom.toScreenX(maxX), LexiconZoom.toScreenY(minY), LexiconZoom.toScreenY(maxY));
    }

    @WrapOperation(method = "renderElements", at = @At(value = "INVOKE", target = "LReika/DragonAPI/Instantiable/Data/Maps/RegionMap;addRegionByWH(IIIILjava/lang/Object;)Ljava/lang/Object;"))
    private Object clickRegionZoomed(RegionMap<Object> map, int x, int y, int w, int h, Object o, Operation<Object> original) {
        if (!LexiconZoom.drawing) {
            return original.call(map, x, y, w, h, o);
        }
        return original.call(map, LexiconZoom.toScreenX(x), LexiconZoom.toScreenY(y), Math.round(w * LexiconZoom.zoom), Math.round(h * LexiconZoom.zoom), o);
    }
}
