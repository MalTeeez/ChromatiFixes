package cc.unilock.chromatifixes.mixin.late.compat.nei;

import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = NBTTagCompound.class)
public interface NBTTagCompoundAccessor {

    @Accessor("tagMap")
    Map<?, ?> chromatifixes$getTagMap();
}
