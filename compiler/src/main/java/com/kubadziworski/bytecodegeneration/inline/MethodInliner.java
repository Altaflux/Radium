package com.kubadziworski.bytecodegeneration.inline;

import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MethodInliner extends LocalVariablesSorter {

    private boolean inlining;
    private final String currentClass;
    private final List<CatchBlock> blocks = new ArrayList<>();

    public MethodInliner(int access, String desc, MethodVisitor mv, String currentClass) {
        super(Opcodes.ASM5, access, desc, mv);
        this.currentClass = currentClass;
    }

    public void visitMethodInsn(int opcode,
                                String owner, String name, String desc, boolean itf) {
        if (!canBeInlined(owner, name, desc)) {
            mv.visitMethodInsn(opcode,
                    owner, name, desc, itf);
            return;
        }

        Type type = ClassTypeFactory.createClassType(owner.replace('/', '.'));
        MethodNode methodNode = type.getInliner().getMethodNode(type, name, desc);

        Remapper remapper = new SimpleRemapper(Collections.singletonMap(
                type.getName(), currentClass));
        Label end = new Label();
        inlining = true;
        methodNode.instructions.resetLabels();

        methodNode.accept(new MethodRemapper(new InliningAdapter(this, end, opcode == Opcodes.INVOKESTATIC ?
                Opcodes.ACC_STATIC : 0, desc), remapper));

        inlining = false;
        super.visitLabel(end);
    }

    private boolean canBeInlined(String owner, String name, String desc) {
        try {
            Type type = ClassTypeFactory.createClassType(owner.replace('/', '.'));
            return type.getFunctionSignatures().stream()
                    .filter(functionSignature -> functionSignature.getName().equals(name))
                    .filter(functionSignature -> DescriptorFactory.getMethodDescriptor(functionSignature).equals(desc))
                    .findFirst().map(functionSignature -> functionSignature.getModifiers().contains(Modifier.INLINE))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
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


    protected int newLocalMapping(org.objectweb.asm.Type type) {
        return super.newLocalMapping(type);
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


    private static class InliningAdapter extends LocalVariablesSorter {
        private final LocalVariablesSorter lvs;
        private final Label end;

        private InliningAdapter(LocalVariablesSorter mv,
                                Label end, int acc, String desc) {
            super(Opcodes.ASM5, acc | Opcodes.ACC_STATIC, "()V", mv);
            this.lvs = mv;
            this.end = end;
            int offset = (acc & Opcodes.ACC_STATIC) != 0 ?
                    0 : 1;
            org.objectweb.asm.Type[] args = org.objectweb.asm.Type.getArgumentTypes(desc);
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

        public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            return null;
        }

        public void visitMaxs(int stack, int locals) {

        }

        protected int newLocalMapping(org.objectweb.asm.Type type) {
            return lvs.newLocal(type);
        }
    }

}
