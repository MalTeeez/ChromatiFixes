package cc.unilock.chromatifixes;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(999)
@IFMLLoadingPlugin.Name("ChromatiFixes Core")
public class ChromatiFixesCorePlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
            "cc.unilock.chromatifixes.asm.dragonapi.ModListASM",
            "cc.unilock.chromatifixes.asm.dragonapi.LuaMethodASM",
            "cc.unilock.chromatifixes.asm.chromaticraft.ChromaItemsASM",
            "cc.unilock.chromatifixes.asm.rotarycraft.ItemRegistryASM",
            "cc.unilock.chromatifixes.asm.rotarycraft.RotaryCraftTileEntityASM",
        };
    }

    @Override
    public String getModContainerClass() { return null; }
    @Override
    public String getSetupClass() { return null; }
    @Override
    public void injectData(Map<String, Object> data) {
        System.out.println("ChromatiFixes Core Plugin loaded!");
    }
    @Override
    public String getAccessTransformerClass() { return null; }
}
