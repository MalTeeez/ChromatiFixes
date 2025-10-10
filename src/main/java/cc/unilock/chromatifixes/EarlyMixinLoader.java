package cc.unilock.chromatifixes;

import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

@IFMLLoadingPlugin.Name("Chromatifixes")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1002) // DragonAPI and DragonRealmCore are 1001
public class EarlyMixinLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {
    static {
        try {
            ConfigurationManager.registerConfig(ChromatiFixesConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getMixinConfig() {
        return "mixins.chromatifixes.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        List<String> mixins = new ArrayList<>();
        if (loadedCoreMods.contains("Reika.DragonRealmCore.DragonRealmASM")) {
            if (ChromatiFixesConfig.disableTickInterceptASM) {
                try {
                    Field argsField = ReikaJVMParser.class.getDeclaredField("args");
                    argsField.setAccessible(true);
                    HashSet<String> args = (HashSet<String>) argsField.get(null);
                    args.add("-DragonAPI_disable_ASM_TICKINTERCEPT");
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        List<String> transformers = new ArrayList<>();

        if (ChromatiFixesConfig.insideDevEnv) {
            transformers.add("cc.unilock.chromatifixes.asm.dragonapi.ModListASM");
            transformers.add("cc.unilock.chromatifixes.asm.dragonapi.LuaMethodASM");
            transformers.add("cc.unilock.chromatifixes.asm.chromaticraft.ChromaItemsASM");
            transformers.add("cc.unilock.chromatifixes.asm.rotarycraft.ItemRegistryASM");
            transformers.add("cc.unilock.chromatifixes.asm.rotarycraft.RotaryCraftTileEntityASM");

            if (isDragonAPIPresent() && isAM2Present()) {
                FMLLog.getLogger().info("Both DragonAPI and AM2 detected, adding AM2BytecodeTransformersASM");
                transformers.add("cc.unilock.chromatifixes.asm.dragonapi.AM2PreloadContainerASM");
            }
        }

        return transformers.toArray(new String[0]);
    }

    private boolean isDragonAPIPresent() {
        try {
            Class.forName("Reika.DragonAPI.DragonAPICore");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean isAM2Present() {
        try {
            Class.forName("am2.preloader.BytecodeTransformers");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // NO-OP
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
