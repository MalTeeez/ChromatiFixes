package cc.unilock.chromatifixes.mixin.late.geostrata.worldgen;

import Reika.GeoStrata.World.DecoGenerator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(value = DecoGenerator.Decorations.class, remap = false)
public interface DecoGenerator$DecorationsAccessor {

    @Invoker("generate")
    boolean invokeGenerate(World world, int x, int y, int z, Random rand);
}
