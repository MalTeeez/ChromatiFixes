package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.ChromatiCraft.Base.ChromaBookGui;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.GUI.Book.GuiStructure;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import cc.unilock.chromatifixes.ChromatiFixesConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

// Mouse wheel / arrow keys flip subpages, mouse back button (and optionally right click) acts as the X button.
// Goes through the existing buttons, so their sounds and NEI-opened page handling still apply.
@Mixin(GuiBookSection.class)
public abstract class GuiBookSectionMixin extends ChromaBookGui {
    @Unique
    private static final int BACK = 50, NEXT = 13, PREV = 14;

    @Shadow(remap = false)
    protected int subpage;

    private GuiBookSectionMixin(ChromaGuis t, EntityPlayer ep, int x, int y) {
        super(t, ep, x, y);
    }

    @Shadow(remap = false)
    protected abstract int getMaxSubpage();

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        boolean pressed = Mouse.getEventButtonState();
        int button = Mouse.getEventButton();
        if (ChromatiFixesConfig.lexiconQoL) {
            if (wheel != 0) {
                chromatifixes$press(wheel < 0 ? NEXT : PREV);
                return;
            }
            if (pressed && button == 3) {
                chromatifixes$press(BACK);
                return;
            }
        }
        // The structure page already uses right click to reset its 3D view rotation
        if (ChromatiFixesConfig.rightClickLexiconBack && pressed && button == 1 && !((Object) this instanceof GuiStructure)) {
            chromatifixes$press(BACK);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (!ChromatiFixesConfig.lexiconQoL) {
            return;
        }
        if (keyCode == Keyboard.KEY_RIGHT) {
            chromatifixes$press(NEXT);
        } else if (keyCode == Keyboard.KEY_LEFT) {
            chromatifixes$press(PREV);
        }
    }

    // Only pages with subpages have the < > buttons, so this is a no-op elsewhere.
    // The buttons are never disabled at the ends; skip those presses so the wheel doesn't keep clicking.
    @Unique
    private void chromatifixes$press(int id) {
        if (id == NEXT && subpage >= getMaxSubpage() || id == PREV && subpage <= 0) {
            return;
        }
        for (Object o : buttonList) {
            GuiButton button = (GuiButton) o;
            if (button.id == id && button.enabled) {
                button.func_146113_a(mc.getSoundHandler());
                actionPerformed(button);
                return;
            }
        }
    }
}
