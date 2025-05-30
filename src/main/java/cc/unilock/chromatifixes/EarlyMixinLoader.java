package cc.unilock.chromatifixes;

import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.lang.reflect.Field;
import java.util.*;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1002) // DragonAPI and DragonRealmCore are 1001
public class EarlyMixinLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {
    static {
        try {
            ConfigurationManager.registerConfig(ChromatiFixesConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public String getMixinConfig() {
        return "mixins.chromatifixes.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return Collections.emptyList();
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
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
