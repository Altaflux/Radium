package com.kubadziworski.bytecodegeneration.intrinsics;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.domain.type.intrinsic.primitive.AbstractPrimitiveType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

public class Equals extends IntrinsicMethod {

    @Override
    public Expression toExpression(final CallableMember call, final InstructionAdapter v) {

        return new IntrinsicExpression() {

            @Override
            public Type getType() {
                return call.getType();
            }

            @Override
            public void accept(StatementGenerator generator) {
                Type owner = call.getOwner().getType();
                if (owner instanceof TypeProjection) {
                    owner = ((TypeProjection) owner).getInternalType();
                }

                Type compareValue = call.getArguments().get(0).getType();

                if (compareValue instanceof TypeProjection) {
                    compareValue = ((TypeProjection) compareValue).getInternalType();
                }

                Type topType = PrimitiveTypes.getBiggerDenominator((AbstractPrimitiveType) owner, compareValue);
                org.objectweb.asm.Type asmTopType = topType.getAsmType();

                call.getOwner().accept(generator);
                PrimitiveTypesWrapperFactory.coerce(topType, owner, v);

                //We need to extract the expression of the argument as the argumentGenerator will try to coerce it
                Argument argument = call.getArguments().get(0);
                argument.getExpression().accept(generator);

                PrimitiveTypesWrapperFactory.coerce(topType, compareValue, v);

                if (asmTopType.getSort() == org.objectweb.asm.Type.OBJECT) {
                    v.invokestatic("radium/jvm/internal/Intrinsics", "areEqual", "(Ljava/lang/Object;Ljava/lang/Object;)Z", false);
                    return;
                }
                if (asmTopType.getSort() == org.objectweb.asm.Type.FLOAT || asmTopType.getSort() == org.objectweb.asm.Type.DOUBLE) {
                    callCompareFloatOrDouble(v, asmTopType);
                } else if (asmTopType.getSort() == org.objectweb.asm.Type.LONG) {
                    compareLong(v);
                } else {
                    compareIntType(v);
                }

            }

            private void callCompareFloatOrDouble(MethodVisitor methodVisitor, org.objectweb.asm.Type type) {
                methodVisitor.visitInsn(type == org.objectweb.asm.Type.FLOAT_TYPE ? Opcodes.FCMPL : Opcodes.DCMPL);
                callCompareSign(methodVisitor);
            }

            private void callCompareSign(MethodVisitor methodVisitor) {
                Label label = new Label();
                Label label2 = new Label();
                methodVisitor.visitInsn(Opcodes.LCMP);

                methodVisitor.visitJumpInsn(Opcodes.IFNE, label);

                methodVisitor.visitInsn(Opcodes.ICONST_1);
                methodVisitor.visitJumpInsn(Opcodes.GOTO, label2);
                methodVisitor.visitLabel(label);
                methodVisitor.visitInsn(Opcodes.ICONST_0);
                methodVisitor.visitLabel(label2);
            }

            private void compareLong(MethodVisitor methodVisitor) {
                methodVisitor.visitInsn(Opcodes.LCMP);
                callCompareSign(methodVisitor);
            }


            private void compareIntType(MethodVisitor methodVisitor) {

                Label label = new Label();
                Label label2 = new Label();

                methodVisitor.visitJumpInsn(Opcodes.IF_ICMPNE, label);
                methodVisitor.visitInsn(Opcodes.ICONST_1);
                methodVisitor.visitJumpInsn(Opcodes.GOTO, label2);
                methodVisitor.visitLabel(label);
                methodVisitor.visitInsn(Opcodes.ICONST_0);
                methodVisitor.visitLabel(label2);
            }
        };
    }
}


