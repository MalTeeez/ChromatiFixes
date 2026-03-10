package cc.unilock.chromatifixes.mixin.late.dragonapi;

import Reika.DragonAPI.Base.BlockCustomLeaf;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Random;

@Mixin(value = BlockCustomLeaf.class, remap = false)
public abstract class BlockCustomLeafMixin {

    @Unique private static final int CACHE_TTL = 180;         // ticks - normal connected cache lifetime
    @Unique private static final int HOTSPOT_TTL = 300;       // ticks - how long a chunk stays in fast-decay mode (15 seconds)
    @Unique private static final int HOTSPOT_CACHE_TTL = 2;   // ticks - reduced cache TTL during fast-decay mode

    // chunk key -> world time of last "connected" result
    @Unique
    private static final HashMap<Long, Long> chromatiFixes$chunkCache = new HashMap<>();

    // chunk key -> world time when fast-decay mode expires
    @Unique
    private static final HashMap<Long, Long> chromatiFixes$hotspotMap = new HashMap<>();

    @Unique
    private static long chromatiFixes$packChunk(int chunkX, int chunkZ) {
        long key = ((chunkX & 0xFFFFFFFFL)) << 32 | (chunkZ & 0xFFFFFFFFL);
        // Wang hash mix to reduce bucket clustering
        key = (key ^ (key >>> 30)) * 0xbf58476d1ce4e5b9L;
        key = (key ^ (key >>> 27)) * 0x94d049bb133111ebL;
        return key ^ (key >>> 31);
    }

    @Inject(
        method = "decay",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void onDecay$chromatifixes(World world, int x, int y, int z, Random par5Random, CallbackInfoReturnable<Boolean> cir) {
        if (world.provider.dimensionId != 0) return;
        long key = chromatiFixes$packChunk(x >> 4, z >> 4);
        long now = world.getTotalWorldTime();

        // Determine effective TTL - use short TTL if chunk is in fast-decay mode
        Long hotspotExpiry = chromatiFixes$hotspotMap.get(key);
        boolean isHotspot = hotspotExpiry != null && now < hotspotExpiry;
        if (!isHotspot && hotspotExpiry != null) {
            chromatiFixes$hotspotMap.remove(key); // clean up expired hotspot
        }
        int effectiveTTL = isHotspot ? HOTSPOT_CACHE_TTL : CACHE_TTL;

        Long cached = chromatiFixes$chunkCache.get(key);
        if (cached != null && now - cached < effectiveTTL) {
            cir.setReturnValue(false);
            return;
        }
    }

    @Inject(
        method = "decay",
        at = @At("RETURN"),
        remap = false
    )
    private void afterDecay$chromatifixes(World world, int x, int y, int z, Random par5Random, CallbackInfoReturnable<Boolean> cir) {
        if (world.provider.dimensionId != 0) return;
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        long now = world.getTotalWorldTime();
        boolean didDecay = cir.getReturnValue();
        if (didDecay) {
            // Invalidate cache and mark hotspot for this chunk and all 8 neighbours
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    long key = chromatiFixes$packChunk(chunkX + dx, chunkZ + dz);
                    chromatiFixes$chunkCache.remove(key);
                    // Extend hotspot window - if already a hotspot, push expiry further out
                    Long existing = chromatiFixes$hotspotMap.get(key);
                    long newExpiry = now + HOTSPOT_TTL;
                    if (existing == null || newExpiry > existing) {
                        chromatiFixes$hotspotMap.put(key, newExpiry);
                    }
                }
            }
        } else {
            chromatiFixes$chunkCache.put(chromatiFixes$packChunk(chunkX, chunkZ), now);
        }
    }

    // Also set an area to a hotspot to catch on to initial decay start faster.
    @Inject(
        method = "onNeighborBlockChange",
        at = @At("HEAD"),
        remap = false
    )
    private void onNeighborBlockChange$chromatifixes(World world, int x, int y, int z, Block neighborID, CallbackInfo ci) {
        if (world == null || world.provider == null) return;
        if (world.provider.dimensionId != 0) return;
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        long now = world.getTotalWorldTime();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {

                long key = chromatiFixes$packChunk(chunkX + dx, chunkZ + dz);
                chromatiFixes$chunkCache.remove(key);
                Long existing = chromatiFixes$hotspotMap.get(key);
                long newExpiry = now + HOTSPOT_TTL;

                if (existing == null || newExpiry > existing) {
                    chromatiFixes$hotspotMap.put(key, newExpiry);
                }
            }
        }
    }
}
