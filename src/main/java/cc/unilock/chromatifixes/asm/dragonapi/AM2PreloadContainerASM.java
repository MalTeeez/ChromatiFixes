package cc.unilock.chromatifixes.asm.dragonapi;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;

import java.lang.reflect.Field;

public class AM2PreloadContainerASM implements IClassTransformer {

    public AM2PreloadContainerASM() {
        // This is a very dirty way of modifying another mods asm transformer before it runs its own transform() - in case
        // you know of another method to do so, you may apply it in the transform function below, if you manage to load
        // it with AM2PreloaderContainer / BytecodeTransformer.
        try {
            Class<?> clazz = Class.forName("am2.preloader.AM2PreloaderContainer");
            Field field = clazz.getDeclaredField("foundDragonAPI");
            field.setAccessible(true);
            boolean foundDragonAPI = field.getBoolean(null);
            if (!foundDragonAPI) {
                FMLLog.getLogger().info("Modifying foundDragonAPI in am2.preloader.AM2PreloaderContainer since DragonAPI is actually loaded. " +
                    "We are probably in a development environment.");
                field.setBoolean(null, true);
            }
        } catch (ClassNotFoundException e) {
            FMLLog.getLogger().warn("Failed get am2.preloader.AM2PreloaderContainer, even though am2 is presumably present. " +
                "Expect a crash due to: java.lang.NoSuchFieldError: field_149433_c.");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            FMLLog.getLogger().error("Failed to modify am2.preloader.AM2PreloaderContainer, even though am2 is presumably present. " +
                "Expect a crash due to: java.lang.NoSuchFieldError: field_149433_c.");
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return basicClass;
    }
}
