package cc.unilock.chromatifixes.mixin.late.chromaticraft;

import Reika.ChromatiCraft.Magic.Artefact.UATrades;
import cc.unilock.chromatifixes.ChromatiFixes;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Mixin(value = UATrades.class, remap = false)
public class UATradesMixin {

    @Unique
    private static final ResourceLocation chromatiFixes$jsonPath = new ResourceLocation(ChromatiFixes.MOD_ID, "commodities.json");

    @WrapOperation(
        method = "loadData",
        at = @At(value = "INVOKE", target = "Ljava/io/File;lastModified()J")
    )
    public long forceUseCachedFileCheck$chromatifixes(File instance, Operation<Long> original) {
        return System.currentTimeMillis();
    }

    @WrapOperation(
        method = "loadData",
        at = @At(value = "INVOKE", target = "Ljava/io/File;exists()Z")
    )
    public boolean forceUseCachedFileExists$chromatifixes(File instance, Operation<Boolean> original) {
        try {
            Minecraft.getMinecraft().getResourceManager().getResource(chromatiFixes$jsonPath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Redirect(
        method = "loadData",
        at = @At(value = "INVOKE", target = "LReika/DragonAPI/IO/ReikaFileReader;readJSON(Ljava/io/File;)Lcom/google/gson/JsonElement;")
    )
    public JsonElement forceUseCachedFile$chromatifixes(File instance) {
        try (
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(chromatiFixes$jsonPath).getInputStream();
            InputStreamReader reader = new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8)
        ) {
            return new com.google.gson.JsonParser().parse(reader);
        } catch (java.io.IOException | com.google.gson.JsonParseException e) {
            return com.google.gson.JsonNull.INSTANCE;
        }
    }
}
