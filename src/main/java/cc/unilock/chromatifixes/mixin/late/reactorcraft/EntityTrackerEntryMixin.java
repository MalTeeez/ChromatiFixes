package cc.unilock.chromatifixes.mixin.late.reactorcraft;

import Reika.ReactorCraft.Entities.EntityNeutron;
import Reika.ReactorCraft.Entities.EntityPlasma;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.network.play.server.S14PacketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {

    @Shadow
    public Entity myEntity;

    @WrapOperation(method = "sendLocationToAllClients", at = @At(value = "NEW", target = "(IBBB)Lnet/minecraft/network/play/server/S14PacketEntity$S15PacketEntityRelMove;"))
    public S14PacketEntity.S15PacketEntityRelMove doNotSendRelMovePackets(int p_i45203_1_, byte p_i45203_2_, byte p_i45203_3_, byte p_i45203_4_, Operation<S14PacketEntity.S15PacketEntityRelMove> original) {
        if (this.myEntity instanceof EntityNeutron || this.myEntity instanceof EntityPlasma) {
            return null;
        } else {
            return original.call(p_i45203_1_, p_i45203_2_, p_i45203_3_, p_i45203_4_);
        }
    }
}
