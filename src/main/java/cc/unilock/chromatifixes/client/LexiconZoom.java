package cc.unilock.chromatifixes.client;

// Shared between the Lexicon navigation mixins (mixin classes can't reference each other)
public final class LexiconZoom {
    public static final float MIN = 0.5F;
    public static final float STEP = 0.125F;
    // GuiNavigation draws its sections this far inside the pane corner; zooming is done around that point
    public static final int MARGIN = 11;

    public static float zoom = 1.0F;

    // True while GuiNavigation draws its sections under the zoom transform; its clipping and hit-tests are in unscaled coordinates
    public static boolean drawing;
    public static int originX;
    public static int originY;

    private LexiconZoom() {}

    // Pane size in unscaled coordinates that ends at the same screen edge as the real pane when zoomed
    public static int widen(int size) {
        return MARGIN + Math.round((size - MARGIN) / zoom);
    }

    public static int toScreenX(int x) {
        return originX + Math.round((x - originX) * zoom);
    }

    public static int toScreenY(int y) {
        return originY + Math.round((y - originY) * zoom);
    }
}
