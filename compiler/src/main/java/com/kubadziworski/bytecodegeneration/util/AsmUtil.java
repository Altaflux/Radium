package com.kubadziworski.bytecodegeneration.util;


import com.kubadziworski.domain.type.intrinsic.NullType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AsmUtil {

    public static void duplicateStackValue(Type type, MethodVisitor methodVisitor, int position) {
        int size = type.getSize();

        switch (position) {
            case 0: {
                if (size == 1) {
                    methodVisitor.visitInsn(Opcodes.DUP);
                } else if (size == 2) {
                    methodVisitor.visitInsn(Opcodes.DUP2);
                }
                break;
            }
            case 1: {
                if (size == 1) {
                    methodVisitor.visitInsn(Opcodes.DUP_X1);
                } else if (size == 2) {
                    methodVisitor.visitInsn(Opcodes.DUP2_X1);
                }
                break;
            }
            case 2: {
                if (size == 1) {
                    methodVisitor.visitInsn(Opcodes.DUP_X2);
                } else if (size == 2) {
                    methodVisitor.visitInsn(Opcodes.DUP2_X2);
                }
                break;
            }
        }
    }

    public static void popStackValue(com.kubadziworski.domain.type.Type type, MethodVisitor methodVisitor) {

        if (type.equals(NullType.INSTANCE)) {
            methodVisitor.visitInsn(Opcodes.POP);
            return;
        }

        switch (type.getAsmType().getSize()) {
            case 1: {
                methodVisitor.visitInsn(Opcodes.POP);
                break;
            }
            case 2: {
                methodVisitor.visitInsn(Opcodes.POP2);
                break;
            }
        }
    }
}
