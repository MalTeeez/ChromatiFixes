package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.ChromatiCraft.Base.ChromaBookGui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;

// Only on ESC: goTo() also closes the screen, and saving there would hijack page navigation
@Mixin(ChromaBookGui.class)
public abstract class ChromaBookGuiMixin extends GuiScreen {
    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_ESCAPE) {
            ChromaBookGui.lastGui = (ChromaBookGui) (Object) this;
        }
    }
}
