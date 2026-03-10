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
        final boolean geostrata = loadedMods.contains("GeoStrata");
        final boolean waila = loadedMods.contains("Waila");
        final boolean wailaplugins = loadedMods.contains("wailaplugins");
        final boolean nei = loadedMods.contains("NotEnoughItems");
        final boolean angelica = loadedMods.contains("Angelica");
        final boolean streams = loadedMods.contains("streams");
        final boolean thaumcraft = loadedMods.contains("Thaumcraft");
        final boolean tinkersconstruct = loadedMods.contains("TConstruct");
        final boolean chisel = loadedMods.contains("chisel");

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
            mixins.add("chromaticraft.accessor.CrystalBlockAccessor");
            mixins.add("chromaticraft.accessor.TileEntityCrystalBroadcasterAccessor");
            mixins.add("chromaticraft.DungeonGeneratorMixin");
            mixins.add("chromaticraft.ProgressionManagerMixin");
            mixins.add("chromaticraft.PylonFinderMixin");
            mixins.add("chromaticraft.PylonGeneratorMixin");
            mixins.add("chromaticraft.TileEntityCrystalBroadcasterMixin");
            mixins.add("chromaticraft.TileEntityLumenAlvearyHumidityMatchingEffectMixin");
            mixins.add("chromaticraft.TileEntityLumenAlvearyTemperatureMatchingEffectMixin");
            mixins.add("chromaticraft.TileEntityPlayerDelegateMixin");
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
            mixins.add("chromaticraft.biomeeffects.CliffFogRendererCloudMixin");

            if (ChromatiFixesConfig.optimizeWorldGen) {
                mixins.add("chromaticraft.worldgen.EndOverhaulManagerMixin");
                mixins.add("chromaticraft.worldgen.EndTendrilMixin");
                mixins.add("chromaticraft.worldgen.GlowingCliffsColumnShaperMixin");
            }

            if (angelica && ChromatiFixesConfig.fixAngelicaShaderCIPCompat) {
                mixins.add("chromaticraft.client.EnderCrystalRendererMixin");
            }

            if (ChromatiFixesConfig.replaceDefaultBlockPlaceEvent) {
                if (thaumcraft) {
                    mixins.add("perfblockplace.thaumcraft.ThaumcraftWorldGeneratorCCMixin");
                    mixins.add("perfblockplace.thaumcraft.WorldGenHilltopStonesCCMixin");
                }
                if (tinkersconstruct) {
                    mixins.add("perfblockplace.tinkersconstruct.SlimeIslandGenMixin");
                }
                if (chisel) {
                    mixins.add("perfblockplace.chisel.GeneratorChiselMixin");
                }

            }

            if (ChromatiFixesConfig.forceUseCachedEDDBTrades) {
                mixins.add("chromaticraft.UATradesMixin");
            }
        }
        if (dragonapi) {
            mixins.add("dragonapi.AbstractSearchFoundPathMixin");
            if (ChromatiFixesConfig.disableClientSpecificConfigs) {
                mixins.add("dragonapi.ControlledConfigMixin");
            }
            if (ChromatiFixesConfig.allowHigherIDSpace) {
                mixins.add("dragonapi.IDTypeMixin");
            }

            mixins.add("dragonapi.ReikaFileReaderMixin");

            if (ChromatiFixesConfig.optimizeWorldGen && chisel) {
                mixins.add("dragonapi.chisel.GeneratorChiselMixin");
            }

            if (ChromatiFixesConfig.cacheLeafDecays) {
                mixins.add("dragonapi.BlockCustomLeafMixin");
            }
        }
        if (geostrata) {
            if (ChromatiFixesConfig.optimizeWorldGen) {
                mixins.add("geostrata.worldgen.ArcticSpiresGeneratorArcticSpireMixin");
                mixins.add("geostrata.worldgen.WorldGenGeoRockMixin");
                mixins.add("geostrata.worldgen.LavaRockGeneratorRedesignMixin");
                mixins.add("geostrata.worldgen.CreepvineGeneratorMixin");
                mixins.add("geostrata.worldgen.DecoGenerator$DecorationsMixin");
                mixins.add("geostrata.worldgen.DecoGenerator$DecorationsAccessor");
                mixins.add("geostrata.worldgen.CreepvineGeneratorMixin");
            }
        }
        if (satisforestry) {
            mixins.add("satisforestry.SFEventsMixin");
            mixins.add("satisforestry.TileShaftConnectionMixin");

            if (ChromatiFixesConfig.replaceDefaultBlockPlaceEvent) {
                if (thaumcraft) {
                    mixins.add("perfblockplace.thaumcraft.ThaumcraftWorldGeneratorSFMixin");
                }
                // Register this if we haven't already (in cc)
                if (!chromaticraft) {
                    if (tinkersconstruct) {
                        mixins.add("perfblockplace.tinkersconstruct.SlimeIslandGenMixin");
                    }
                }
            }
        }
        if (rotarycraft) {
            //This does not seem to work in debug dev env's
            if (wailaplugins && ChromatiFixesConfig.disableWailaFluidTooltips) {
                mixins.add("rotarycraft.BlockBasicMultiTEMixin");
            }

            if (streams && ChromatiFixesConfig.replaceDefaultBlockPlaceEvent) {
                mixins.add("perfblockplace.streams.BlockRiverMixin");
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
