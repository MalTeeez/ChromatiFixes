package cc.unilock.chromatifixes;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public class LateMixinLoader implements ILateMixinLoader {
    @Override
    public String getMixinConfig() {
        return "mixins.chromatifixes.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        final boolean cavecontrol = loadedMods.contains("CaveControl");
        final boolean chromaticraft = loadedMods.contains("ChromatiCraft");
        final boolean dragonapi = loadedMods.contains("DragonAPI");
        final boolean satisforestry = loadedMods.contains("Satisforestry");
        final boolean reactorcraft = loadedMods.contains("ReactorCraft");
        final boolean rotarycraft = loadedMods.contains("RotaryCraft");
        final boolean dragonrealmcore = loadedMods.contains("DragonRealmCore");
        final boolean waila = loadedMods.contains("Waila");
        final boolean wailaplugins = loadedMods.contains("wailaplugins");
        final boolean nei = loadedMods.contains("NotEnoughItems");

        List<String> mixins = new ArrayList<>();

        if (FMLLaunchHandler.side().isClient()) {
            if (chromaticraft) {
                mixins.add("chromaticraft.client.accessor.WarpPointAccessor");
                mixins.add("chromaticraft.client.ArtefactSpawnerMixin");
                mixins.add("chromaticraft.client.ChromaClientEventControllerMixin");
                mixins.add("chromaticraft.client.ChromaDimensionManagerMixin");
                mixins.add("chromaticraft.client.CrystalFurnaceHandlerMixin");
                mixins.add("chromaticraft.client.EnchantDecompHandlerMixin");
                mixins.add("chromaticraft.client.FabricatorHandlerMixin");
                mixins.add("chromaticraft.client.WarpPointDataMixin");
            }
            if (dragonrealmcore) {
                mixins.add("dragonrealmcore.client.DREventsMixin");
            }
        }

        if (cavecontrol) {
            mixins.add("cavecontrol.CaveLoaderMixin");
        }
        if (chromaticraft) {
            mixins.add("chromaticraft.accessor.TileEntityCrystalBroadcasterAccessor");
            mixins.add("chromaticraft.DungeonGeneratorMixin");
            mixins.add("chromaticraft.ProgressionManagerMixin");
            mixins.add("chromaticraft.PylonFinderMixin");
            mixins.add("chromaticraft.PylonGeneratorMixin");
            mixins.add("chromaticraft.TileEntityCrystalBroadcasterMixin");
            mixins.add("chromaticraft.TileEntityLumenAlvearyEffectMixins");
            mixins.add("chromaticraft.TileEntityWirelessPoweredMixin");
            if (ChromatiFixesConfig.enableShiftMazeBypassCrashFix) {
                mixins.add("chromaticraft.ShiftMazeGeneratorMixin");
            }
            if (wailaplugins && ChromatiFixesConfig.disableWailaFluidTooltips) {
                mixins.add("chromaticraft.wailatank.BlockChromaTileMixin");
                mixins.add("chromaticraft.wailatank.TileEntityCrystalTankMixin");
                mixins.add("chromaticraft.wailatank.BlockCrystalTankMixin");
            }
            mixins.add("chromaticraft.BlockDecoFlowerMixin");
            mixins.add("chromaticraft.biomeeffects.BiomeFXRendererMixin");
        }
        if (dragonapi) {
            mixins.add("dragonapi.AbstractSearchFoundPathMixin");
            if (ChromatiFixesConfig.disableClientSpecificConfigs) {
                mixins.add("dragonapi.ControlledConfigMixin");
            }
            if (ChromatiFixesConfig.allowHigherIDSpace) {
                mixins.add("dragonapi.IDTypeMixin");
            }
        }
        if (satisforestry) {
            mixins.add("satisforestry.SFEventsMixin");
            mixins.add("satisforestry.TileShaftConnectionMixin");
        }
        if (rotarycraft) {
            //This does not seem to work in debug dev env's
            if (wailaplugins && ChromatiFixesConfig.disableWailaFluidTooltips) {
                mixins.add("rotarycraft.BlockBasicMultiTEMixin");
            }
        }
        if (reactorcraft) {
            mixins.add("reactorcraft.EntityTrackerEntryMixin");
            if (wailaplugins && ChromatiFixesConfig.disableWailaFluidTooltips) {
                mixins.add("reactorcraft.BlockReactorTileMixin");
            }
        }
        if (dragonrealmcore) {
            mixins.add("dragonrealmcore.BlockT2HexGeneratorMixin");
            mixins.add("dragonrealmcore.BlockT3HexGeneratorMixin");
            if (ChromatiFixesConfig.disableTerritoryStrongholdSystem) {
                mixins.add("cavecontrol.stronghold.ControllableStrongholdGenMixin");
                mixins.add("dragonrealmcore.stronghold.DREventsMixin");
                mixins.add("dragonrealmcore.stronghold.StrongholdSeedMixCommandMixin");
            }
            if (ChromatiFixesConfig.energizationManagerFix) {
                mixins.add("dragonrealmcore.EnergizationManagerMixin");
            }
        }
        if (nei && ChromatiFixesConfig.fixNEIInvalidAccessErrorCrash) {
            mixins.add("compat.nei.NBTHelperMixin");
        }
        return mixins;
    }
}
