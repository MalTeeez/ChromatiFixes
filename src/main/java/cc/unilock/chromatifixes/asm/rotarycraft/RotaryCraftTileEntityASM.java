package cc.unilock.chromatifixes.asm.rotarycraft;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class RotaryCraftTileEntityASM implements IClassTransformer {

    public RotaryCraftTileEntityASM() {
        System.out.println("=== RotaryCraftTileEntityASM CONSTRUCTOR CALLED ===");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("Reika.RotaryCraft.Base.TileEntity.RotaryCraftTileEntity".equals(transformedName)) {
            System.out.println("=== PATCHING RotaryCraftTileEntity to remove @ModDependent annotation ===");
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
                    System.out.println("Removing getOCNetworkVisibility method entirely");
                    return null; // Don't visit this method at all
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

        byte[] result = writer.toByteArray();
        verifyMethodRemoved(result);
        return result;
    }

    private void verifyMethodRemoved(byte[] modifiedClass) {
        ClassReader verifyReader = new ClassReader(modifiedClass);

        verifyReader.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if ("getOCNetworkVisibility".equals(name)) {
                    System.out.println("ERROR: getOCNetworkVisibility method still exists in modified class!");
                } else {
                    System.out.println("Method found in modified class: " + name);
                }
                return null; // We don't need to visit the method body
            }
        }, ClassReader.SKIP_CODE);

        System.out.println("Verification complete for RotaryCraftTileEntity");
    }



    // private byte[] removeModDependentAnnotation(byte[] basicClass) {
    //     ClassReader reader = new ClassReader(basicClass);
    //     ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

    //     reader.accept(new ClassVisitor(Opcodes.ASM5, writer) {
    //         @Override
    //         public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    //             MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

    //             // Target the getOCNetworkVisibility method
    //             if ("getOCNetworkVisibility".equals(name)) {
    //                 return new MethodVisitor(Opcodes.ASM5, mv) {
    //                     @Override
    //                     public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    //                         // Skip @ModDependent annotation
    //                         if (desc.contains("ModDependent")) {
    //                             System.out.println("Removing @ModDependent annotation from getOCNetworkVisibility method");
    //                             return null; // Don't visit this annotation
    //                         }
    //                         return super.visitAnnotation(desc, visible);
    //                     }
    //                 };
    //             }
    //             return mv;
    //         }
    //     }, 0);

    //     return writer.toByteArray();
    // }
}
