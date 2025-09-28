package cc.unilock.chromatifixes.asm.rotarycraft;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import cpw.mods.fml.common.FMLLog;

public class RotaryCraftTileEntityASM implements IClassTransformer {

    public RotaryCraftTileEntityASM() {
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("Reika.RotaryCraft.Base.TileEntity.RotaryCraftTileEntity".equals(transformedName)) {
            FMLLog.getLogger().info("PATCHING RotaryCraftTileEntity to remove @ModDependent annotation");
            return removeOpenComputersDependencies(basicClass);
        }
        return basicClass;
    }

    private byte[] removeOpenComputersDependencies(byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        reader.accept(new ClassVisitor(Opcodes.ASM5, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                    String[] exceptions) {
                // Skip the getOCNetworkVisibility method entirely
                if ("getOCNetworkVisibility".equals(name)) {
                    return null;
                }

                return super.visitMethod(access, name, desc, signature, exceptions);
            }

            @Override
            public void visit(int version, int access, String name, String signature, String superName,
                    String[] interfaces) {
                // Remove li.cil.oc.api.network.Visibility from imports by not referencing it
                // This happens automatically when we remove the method that uses it
                super.visit(version, access, name, signature, superName, interfaces);
            }
        }, 0);

        return writer.toByteArray();
    }
}
