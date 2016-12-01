package com.kubadziworski.bytecodegeneration.intrinsics;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.commons.InstructionAdapter;

public class ToString extends IntrinsicMethod {

    @Override
    public Expression toExpression(CallableMember functionCall, InstructionAdapter methodVisitor) {

        return new IntrinsicExpression() {
            @Override
            public Type getType() {
                return functionCall.getType();
            }

            @Override
            public void accept(StatementGenerator generator) {
                functionCall.getOwner().accept(generator);
                org.objectweb.asm.Type type = stringValueOfType(functionCall.getOwner().getType().getAsmType());

                methodVisitor.invokestatic("java/lang/String", "valueOf", "(" + type.getDescriptor() + ")Ljava/lang/String;", false);
            }

            org.objectweb.asm.Type stringValueOfType(org.objectweb.asm.Type type) {
                int sort = type.getSort();
                return sort == org.objectweb.asm.Type.OBJECT || sort == org.objectweb.asm.Type.ARRAY
                        ? org.objectweb.asm.Type.getType(Object.class)
                        : sort == org.objectweb.asm.Type.BYTE || sort == org.objectweb.asm.Type.SHORT ? org.objectweb.asm.Type.INT_TYPE : type;
            }
        };
    }
}
