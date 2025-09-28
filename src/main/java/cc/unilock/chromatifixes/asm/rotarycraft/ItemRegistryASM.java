package cc.unilock.chromatifixes.asm.rotarycraft;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class ItemRegistryASM implements IClassTransformer {

    public ItemRegistryASM() {
        System.out.println("=== RotaryItemsASM CONSTRUCTOR CALLED ===");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("Reika.RotaryCraft.Registry.ItemRegistry".equals(transformedName)) {
            System.out.println("=== PATCHING ChromaItems to remove problematic enum constants ===");
            return removeProblematicEnumConstants(basicClass);
        }
        return basicClass;
    }

    private byte[] removeProblematicEnumConstants(byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        reader.accept(new ClassVisitor(Opcodes.ASM5, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                // Target the static initializer where enum constants are created
                if ("<clinit>".equals(name)) {
                    return new MethodVisitor(Opcodes.ASM5, mv) { // Fixed: Use consistent ASM version
                        @Override
                        public void visitLdcInsn(Object cst) {
                            // Check if this is a problematic class reference
                            if (cst instanceof Type && ((Type) cst).getSort() == Type.OBJECT) {
                                String className = ((Type) cst).getClassName();

                                if (isProblematicClass(className)) {
                                    System.out.println("Replacing problematic class reference: " + className);
                                    // Replace with a safe class (Item.class)
                                    super.visitLdcInsn(Type.getType("Lnet/minecraft/item/Item;"));
                                    return;
                                }
                            }

                            // Let other constants through normally
                            super.visitLdcInsn(cst);
                        }
                    };
                }
                return mv;
            }
        }, 0);

        return writer.toByteArray();
    }

    private boolean isProblematicClass(String className) {
        return className.equals("Reika.RotaryCraft.ModInterface.ItemScrewdriverFocus");
    }
}
