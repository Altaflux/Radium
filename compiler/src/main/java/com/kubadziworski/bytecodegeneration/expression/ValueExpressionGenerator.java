package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.type.NullType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeResolver;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ValueExpressionGenerator {
    private final MethodVisitor methodVisitor;

    public ValueExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(Value value) {
        Type type = value.getType();
        if (type.equals(NullType.INSTANCE)) {
            methodVisitor.visitInsn(Opcodes.ACONST_NULL);

        } else {
            String stringValue = value.getValue();
            Object transformedValue = TypeResolver.getValueFromString(stringValue, type);
            methodVisitor.visitLdcInsn(transformedValue);
        }
    }
}