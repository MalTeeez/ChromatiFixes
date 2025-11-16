package cc.unilock.chromatifixes.mixin.late.compat.nei;

import codechicken.nei.util.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(value = NBTHelper.class, remap = false)
public class NBTHelperMixin {

    @Redirect(method = "matchTagCompound", at = @At(value = "FIELD", target = "Lnet/minecraft/nbt/NBTTagCompound;tagMap:Ljava/util/Map;", opcode = Opcodes.GETFIELD))
    private static Map<?, ?> catchIllegalAndReturnWithAccessor(NBTTagCompound instance) {
        try {
            return instance.tagMap;
        } catch (IllegalAccessError ignored) {
            return ((NBTTagCompoundAccessor) instance).chromatifixes$getTagMap();
        }
    }
}
