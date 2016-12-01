package com.kubadziworski.bytecodegeneration.intrinsics;


import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.CallableMember;
import org.objectweb.asm.MethodVisitor;

public abstract class IntrinsicMethod {

    public Expression toExpression(CallableMember functionCall, MethodVisitor methodVisitor) {
        throw new UnsupportedOperationException("Operation not supported");
    }
}