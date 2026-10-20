package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.ChromatiCraft.Base.GuiScrollingPage;
import Reika.ChromatiCraft.GUI.Book.GuiNavigation;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import cc.unilock.chromatifixes.client.LexiconPane;
import cc.unilock.chromatifixes.client.LexiconZoom;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Zoomable navigation map: sections are drawn scaled around their origin inside the pane,
// with the pane treated as larger so clipping lets the extra sections through up to the real pane edges
@Mixin(GuiNavigation.class)
public abstract class GuiNavigationMixin extends GuiScrollingPage {
    @Unique
    private int chromatifixes$baseMaxX;
    @Unique
    private int chromatifixes$baseMaxY;

    private GuiNavigationMixin(ChromaGuis g, EntityPlayer ep, int x, int y, int w, int h) {
        super(g, ep, x, y, w, h);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void storeScrollLimits(CallbackInfo ci) {
        chromatifixes$baseMaxX = maxX;
        chromatifixes$baseMaxY = maxY;
    }

    // Runs before GuiScrollingPage clamps the offsets. Also floors the limits at 0: with few sections they are
    // negative, which GuiScrollingPage treats as "no upper limit", so dragging could fling the map away.
    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void updateScrollLimits(int x, int y, float f, CallbackInfo ci) {
        maxX = Math.max(0, chromatifixes$baseMaxX + paneWidth - LexiconZoom.widen(paneWidth));
        maxY = Math.max(0, chromatifixes$baseMaxY + paneHeight - LexiconZoom.widen(paneHeight));
    }

    @WrapOperation(method = "drawScreen", at = @At(value = "INVOKE", target = "LReika/ChromatiCraft/GUI/Book/GuiNavigation;drawSections(II)V", remap = false))
    private void drawSectionsZoomed(GuiNavigation self, int x, int y, Operation<Void> original) {
        if (LexiconZoom.zoom == 1.0F) {
            original.call(self, x, y);
            return;
        }
        LexiconZoom.originX = leftX + LexiconZoom.MARGIN;
        LexiconZoom.originY = topY + LexiconZoom.MARGIN;
        LexiconZoom.drawing = true;
        int width = paneWidth, height = paneHeight;
        // Every clipping check reads these (partly through synthetic accessors), so widen the pane itself while drawing
        ((LexiconPane) this).chromatifixes$setPaneSize(LexiconZoom.widen(width), LexiconZoom.widen(height));
        GL11.glPushMatrix();
        GL11.glTranslatef(LexiconZoom.originX, LexiconZoom.originY, 0);
        GL11.glScalef(LexiconZoom.zoom, LexiconZoom.zoom, 1);
        GL11.glTranslatef(-LexiconZoom.originX, -LexiconZoom.originY, 0);
        try {
            original.call(self, x, y);
        } finally {
            GL11.glPopMatrix();
            ((LexiconPane) this).chromatifixes$setPaneSize(width, height);
            LexiconZoom.drawing = false;
        }
    }
}
