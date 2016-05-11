package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeResolver;
import org.objectweb.asm.MethodVisitor;

public class ValueExpressionGenerator {
    private final MethodVisitor methodVisitor;

    public ValueExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(Value value) {
        Type type = value.getType();
        String stringValue = value.getValue();
        Object transformedValue = TypeResolver.getValueFromString(stringValue, type);
        methodVisitor.visitLdcInsn(transformedValue);
    }
}