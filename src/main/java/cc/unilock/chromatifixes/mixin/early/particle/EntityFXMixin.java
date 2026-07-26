package cc.unilock.chromatifixes.mixin.early.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityFX.class)
public abstract class EntityFXMixin extends Entity {

    public EntityFXMixin(World world) {
        super(world);
    }

    // The constructor seeds lastTickPos but not prevPos, which renderParticle interpolates from. A particle drawn
    // before its first onUpdate therefore streaks in from the world origin.
    @Inject(method = "<init>(Lnet/minecraft/world/World;DDD)V", at = @At("RETURN"))
    private void ccfixes$seedPreviousPosition(World world, double x, double y, double z, CallbackInfo ci) {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    }
}
