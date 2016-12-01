package com.kubadziworski.bytecodegeneration.intrinsics;


import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.CallableMember;
import org.objectweb.asm.commons.InstructionAdapter;

public abstract class IntrinsicMethod {

    public Expression toExpression(CallableMember functionCall, InstructionAdapter methodVisitor) {
        throw new UnsupportedOperationException("Operation not supported");
    }
}