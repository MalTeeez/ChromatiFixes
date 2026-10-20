package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.ChromatiCraft.Base.ChromaBookGui;
import Reika.ChromatiCraft.Base.GuiScrollingPage;
import Reika.ChromatiCraft.GUI.Book.GuiNavigation;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import cc.unilock.chromatifixes.client.LexiconPane;
import cc.unilock.chromatifixes.client.LexiconZoom;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Drag with the left mouse button or use the mouse wheel (shift for horizontal) to scroll, on top of the existing WASD/arrow key scrolling.
// Ctrl+wheel zooms the navigation map (see GuiNavigationMixin).
@Mixin(GuiScrollingPage.class)
public abstract class GuiScrollingPageMixin extends ChromaBookGui implements LexiconPane {
    @Shadow(remap = false)
    protected static int offsetX;
    @Shadow(remap = false)
    protected static int offsetY;
    @Shadow(remap = false) @Final @Mutable
    protected int paneWidth;
    @Shadow(remap = false) @Final @Mutable
    protected int paneHeight;

    @Unique
    private static final int WHEEL_STEP = 24;

    @Unique
    private boolean chromatifixes$dragging;
    @Unique
    private int chromatifixes$dragStartX, chromatifixes$dragStartY, chromatifixes$dragStartOffsetX, chromatifixes$dragStartOffsetY;

    private GuiScrollingPageMixin(ChromaGuis t, EntityPlayer ep, int x, int y) {
        super(t, ep, x, y);
    }

    @Shadow(remap = false)
    protected abstract boolean isScrollEnabled();

    @Override
    public void chromatifixes$setPaneSize(int width, int height) {
        paneWidth = width;
        paneHeight = height;
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        if (button == 0) {
            chromatifixes$dragging = true;
            chromatifixes$dragStartX = x;
            chromatifixes$dragStartY = y;
            chromatifixes$dragStartOffsetX = offsetX;
            chromatifixes$dragStartOffsetY = offsetY;
        }
        super.mouseClicked(x, y, button);
    }

    // Applied every frame from the live mouse position: GUI mouse events only arrive at 20 TPS, which made dragging choppy.
    // Anchored to the drag start so rounding at zoom != 1 doesn't drift. Offsets are clamped later in drawScreen.
    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void applyDrag(int x, int y, float f, CallbackInfo ci) {
        if (!chromatifixes$dragging) {
            return;
        }
        if (!Mouse.isButtonDown(0) || !isScrollEnabled()) {
            chromatifixes$dragging = false;
            return;
        }
        float zoom = chromatifixes$zoom();
        offsetX = chromatifixes$dragStartOffsetX - Math.round((x - chromatifixes$dragStartX) / zoom);
        offsetY = chromatifixes$dragStartOffsetY - Math.round((y - chromatifixes$dragStartY) / zoom);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel == 0 || !isScrollEnabled()) {
            return;
        }
        if (GuiScreen.isCtrlKeyDown() && (Object) this instanceof GuiNavigation) {
            chromatifixes$zoom(wheel > 0 ? LexiconZoom.STEP : -LexiconZoom.STEP);
            return;
        }
        int step = Math.round((wheel < 0 ? WHEEL_STEP : -WHEEL_STEP) / chromatifixes$zoom());
        if (GuiScreen.isShiftKeyDown()) {
            offsetX += step;
        } else {
            offsetY += step;
        }
    }

    // Scroll the background at the same on-screen speed as the (zoomed) content
    @Definition(id = "offsetX", field = "LReika/ChromatiCraft/Base/GuiScrollingPage;offsetX:I")
    @Expression("offsetX % 256")
    @ModifyExpressionValue(method = "drawScreen", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int backgroundU(int u) {
        return Math.floorMod(Math.round(offsetX * chromatifixes$zoom()), 256);
    }

    @Definition(id = "offsetY", field = "LReika/ChromatiCraft/Base/GuiScrollingPage;offsetY:I")
    @Expression("offsetY % 256")
    @ModifyExpressionValue(method = "drawScreen", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int backgroundV(int v) {
        return Math.floorMod(Math.round(offsetY * chromatifixes$zoom()), 256);
    }

    // Zoom around the cursor (clamped to the pane): shift the offset so the content point under it stays put
    @Unique
    private void chromatifixes$zoom(float delta) {
        float before = LexiconZoom.zoom;
        float after = Math.max(LexiconZoom.MIN, Math.min(1.0F, before + delta));
        if (after == before) {
            return;
        }
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        int anchorX = Math.max(0, Math.min(paneWidth - LexiconZoom.MARGIN, mouseX - leftX - LexiconZoom.MARGIN));
        int anchorY = Math.max(0, Math.min(paneHeight - LexiconZoom.MARGIN, mouseY - topY - LexiconZoom.MARGIN));
        offsetX += Math.round(anchorX / before - anchorX / after);
        offsetY += Math.round(anchorY / before - anchorY / after);
        LexiconZoom.zoom = after;
    }

    // Only the navigation map zooms; offsetX/Y are shared with the progress pages
    @Unique
    private float chromatifixes$zoom() {
        return (Object) this instanceof GuiNavigation ? LexiconZoom.zoom : 1.0F;
    }
}
