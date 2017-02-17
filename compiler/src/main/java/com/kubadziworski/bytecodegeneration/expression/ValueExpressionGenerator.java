package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.NullType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

public class ValueExpressionGenerator {
    private final InstructionAdapter methodVisitor;

    public ValueExpressionGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(Value value) {
        Type type = value.getType();
        if (type.equals(NullType.INSTANCE)) {
            methodVisitor.visitInsn(Opcodes.ACONST_NULL);

        } else {
            Object valueObj = value.getValue();
            methodVisitor.visitLdcInsn(valueObj);
        }
    }
}