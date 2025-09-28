package cc.unilock.chromatifixes.asm.dragonapi;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class ModListASM implements IClassTransformer {

    public ModListASM() {
        System.out.println("=== ModListASM CONSTRUCTOR CALLED ===");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("Reika.DragonAPI.ModList".equals(transformedName)) {
            System.out.println("=== PATCHING ModList.isLoaded() to add debugging ===");
            //return addLoggingToIsLoaded(basicClass);
        }
        return basicClass;
    }

    private byte[] addLoggingToIsLoaded(byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        reader.accept(new ClassVisitor(Opcodes.ASM5, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                // Target the isLoaded() method
                if ("isLoaded".equals(name) && "()Z".equals(desc)) {
                    return new MethodVisitor(Opcodes.ASM5, mv) {
                        @Override
                        public void visitCode() {
                            super.visitCode();

                            // Add logging at the beginning:
                            // System.out.println("ModList.isLoaded() called for: " + this.name() + " -> " + condition);

                            // Get stack trace to see WHO called this
                            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                            mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                            mv.visitInsn(Opcodes.DUP);
                            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

                            // Add mod name
                            mv.visitLdcInsn("ModList.isLoaded() called for: ");
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/ModList", "name", "()Ljava/lang/String;", false);
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                            // Add result
                            mv.visitLdcInsn(" -> ");
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                            mv.visitFieldInsn(Opcodes.GETFIELD, "Reika/DragonAPI/ModList", "condition", "Z");
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false);

                            // Add caller info
                            mv.visitLdcInsn(" [Called from: ");
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                            // Get stack trace element
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
                            mv.visitInsn(Opcodes.ICONST_2); // Index 2 (caller of isLoaded)
                            mv.visitInsn(Opcodes.AALOAD);
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StackTraceElement", "toString", "()Ljava/lang/String;", false);
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                            mv.visitLdcInsn("]");
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                        }
                    };
                }
                return mv;
            }
        }, 0);

        return writer.toByteArray();
    }
}
