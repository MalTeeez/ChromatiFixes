package cc.unilock.chromatifixes;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "chromatifixes")
public class ChromatiFixesConfig {
    @Config.Comment("Disables the shader found near Data Towers from ChromatiCraft")
    @Config.DefaultBoolean(false)
    public static boolean disableArtefactShader;

    @Config.Comment("Disables the \"heat\" shader in the Nether from DragonRealmCore")
    @Config.DefaultBoolean(false)
    public static boolean disableNetherShader;

    @Config.Comment("Forces DragonAPI's \"client-specific\" config options to use the same keys across all systems")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean disableClientSpecificConfigs;

    @Config.Comment("Disables DragonRealmCore's TickIntercept ASM (pauses the server when no players are online; disabled by default in DragonRealmCore config)")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean disableTickInterceptASM;

    @Config.Comment("Disables the TerritoryZone-based Stronghold generation system from DragonRealmCore")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean disableTerritoryStrongholdSystem;

    @Config.Comment("Prevents crashes in `EnergizationManager#getEnergizationLevel`; breaks DragonRealmCore's \"Atmospheric Permeability Mediation\" mechanic")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean energizationManagerFix;

    @Config.Comment("Whether or not to enable fix of server crash when using bypass panel of shifting maze. Make sure to disable this if using MMNHUtils (or disable their check)")
    @Config.DefaultBoolean(value = true)
    public static boolean enableShiftMazeBypassCrashFix;

    @Config.Comment("Allow registering potions with a higher ID than normal, only use with mods that expand the ID range, such as EndlessIDs. EXPERIMENTAL")
    @Config.DefaultBoolean(value = false)
    public static boolean allowHigherIDSpace;

    @Config.Comment("Enable heavy patches on Reikas mods to allow running inside a dev env, will break mod integration. DO NOT USE OUTSIDE OF DEV ENV.")
    @Config.DefaultBoolean(value = false)
    public static boolean insideDevEnv;

    @Config.Comment("Disable some generic IFluidHandler tooltips on TEs from RC, ReC & CC. Intended for usage if you have a nicer provider such as WailaPlugins.")
    @Config.DefaultBoolean(value = false)
    public static boolean disableWailaFluidTooltips;
}
