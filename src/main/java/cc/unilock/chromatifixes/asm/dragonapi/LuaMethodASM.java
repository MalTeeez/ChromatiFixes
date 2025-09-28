package cc.unilock.chromatifixes.asm.dragonapi;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class LuaMethodASM implements IClassTransformer {

    public LuaMethodASM() {
        System.out.println("=== LuaMethodASM CONSTRUCTOR CALLED ===");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("Reika.DragonAPI.ModInteract.Lua.LuaMethod".equals(transformedName)) {
            System.out.println("=== PATCHING LuaMethod to remove computer mod dependent methods ===");
            return removeComputerMethods(basicClass);
        }
        return basicClass;
    }

    private byte[] removeComputerMethods(byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        reader.accept(new ClassVisitor(Opcodes.ASM5, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                // Remove invokeCC method
                if ("invokeCC".equals(name)) {
                    System.out.println("Removing invokeCC method (ComputerCraft dependency)");
                    return null; // Don't visit this method
                }

                // Remove invokeOC method
                if ("invokeOC".equals(name)) {
                    System.out.println("Removing invokeOC method (OpenComputers dependency)");
                    return null; // Don't visit this method
                }

                return super.visitMethod(access, name, desc, signature, exceptions);
            }

            @Override
            public void visitEnd() {
                System.out.println("LuaMethod class processing complete - removed computer mod methods");
                super.visitEnd();
            }
        }, 0);

        return writer.toByteArray();
    }
}
