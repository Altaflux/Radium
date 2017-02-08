package com.kubadziworski.bytecodegeneration.inline;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;


public class MethodCallInliner extends LocalVariablesSorter {

    private final String oldClass;
    private final String newClass;
    private final MethodNode mn;
    private List<CatchBlock> blocks = new ArrayList<>();
    private boolean inlining;
    private final String oldInternalName;

    MethodCallInliner(int access, String desc, MethodVisitor mv, MethodNode mn,
                      String oldClass, String oldInternalName, String newClass) {
        super(Opcodes.ASM5, access, desc, mv);
        this.oldClass = oldClass;
        this.newClass = newClass;
        this.mn = mn;
        this.oldInternalName = oldInternalName;

    }

    public void visitMethodInsn(int opcode,
                                String owner, String name, String desc, boolean itf) {
        if (!canBeInlined(owner, name, desc)) {
            mv.visitMethodInsn(opcode,
                    owner, name, desc, itf);
            return;
        }
        Map<String, String> map = Collections.singletonMap(
                oldClass, newClass);
        Remapper remapper = new SimpleRemapper(map);
        Label end = new Label();
        inlining = true;
        mn.instructions.resetLabels();


        mn.accept(new InliningAdapter(this, end, opcode == Opcodes.INVOKESTATIC ?
                Opcodes.ACC_STATIC : 0, desc, remapper));
        inlining = false;
        super.visitLabel(end);
    }

    private boolean canBeInlined(String owner, String name, String desc) {
        return owner.equals(oldInternalName) && mn.desc.equals(desc) && name.equals(mn.name);
    }

    public void visitTryCatchBlock(Label start,
                                   Label end, Label handler, String type) {
        if (!inlining) {
            blocks.add(new CatchBlock(start, end,
                    handler, type));
        } else {
            super.visitTryCatchBlock(start, end,
                    handler, type);
        }
    }

    public void visitMaxs(int stack, int locals) {
        for (CatchBlock b : blocks) {
            super.visitTryCatchBlock(b.start, b.end,
                    b.handler, b.type);
        }
        super.visitMaxs(stack, locals);
    }

    private static class CatchBlock {
        private final Label start;
        private final Label end;
        private final Label handler;
        private final String type;

        private CatchBlock(Label start, Label end, Label handler, String type) {
            this.start = start;
            this.end = end;
            this.handler = handler;
            this.type = type;
        }
    }

    public static class InliningAdapter extends RemappingMethodAdapter {
        private final LocalVariablesSorter lvs;
        private final Label end;

        private InliningAdapter(LocalVariablesSorter mv,
                                Label end, int acc, String desc,
                                Remapper remapper) {
            super(acc | Opcodes.ACC_STATIC, "()V", mv, remapper);
            this.lvs = mv;
            this.end = end;
            int offset = (acc & Opcodes.ACC_STATIC) != 0 ?
                    0 : 1;
            Type[] args = Type.getArgumentTypes(desc);
            for (int i = args.length - 1; i >= 0; i--) {
                super.visitVarInsn(args[i].getOpcode(
                        Opcodes.ISTORE), i + offset);
            }
            if (offset > 0) {
                super.visitVarInsn(Opcodes.ASTORE, 0);
            }
        }

        public void visitInsn(int opcode) {
            if (opcode == Opcodes.RETURN) {
                super.visitJumpInsn(Opcodes.GOTO, end);
            } else {
                super.visitInsn(opcode);
            }
        }

        public void visitMaxs(int stack, int locals) {
        }

        protected int newLocalMapping(Type type) {
            return lvs.newLocal(type);
        }
    }
}
