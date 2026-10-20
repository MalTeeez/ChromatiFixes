package cc.unilock.chromatifixes.mixin.late.chromaticraft.client;

import Reika.ChromatiCraft.Base.ChromaBookGui;
import cc.unilock.chromatifixes.ChromatiFixesConfig;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChromaBookGui.class)
public abstract class ChromaBookGuiMixin extends GuiScreen {
    @Unique
    private static boolean chromatifixes$navigating;

    // Only on ESC: goTo() also closes the screen, and saving there would hijack page navigation
    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_ESCAPE && ChromatiFixesConfig.alwaysRememberLexiconPage) {
            ChromaBookGui.lastGui = (ChromaBookGui) (Object) this;
        }
    }

    // The cached cursor position is only set by goTo(), so opening the book from the item warped the cursor to a stale/0,0 spot
    @Inject(method = "goTo(LReika/ChromatiCraft/Registry/ChromaGuis;LReika/ChromatiCraft/Registry/ChromaResearch;Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At("HEAD"), remap = false)
    private static void markNavigating(CallbackInfo ci) {
        chromatifixes$navigating = true;
    }

    @WrapWithCondition(method = "initGui", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;setCursorPosition(II)V", remap = false))
    private boolean onlyRestoreCursorWhenNavigating(int x, int y) {
        return chromatifixes$navigating || !ChromatiFixesConfig.lexiconQoL;
    }

    @Inject(method = "initGui", at = @At("TAIL"))
    private void clearNavigating(CallbackInfo ci) {
        chromatifixes$navigating = false;
    }
}
