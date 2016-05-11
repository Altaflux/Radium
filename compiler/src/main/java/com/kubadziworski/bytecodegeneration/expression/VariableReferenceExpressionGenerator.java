package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.domain.node.expression.VariableReference;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;

public class VariableReferenceExpressionGenerator {
    private final MethodVisitor methodVisitor;
    private final Scope scope;

    public VariableReferenceExpressionGenerator(MethodVisitor methodVisitor, Scope scope) {
        this.methodVisitor = methodVisitor;
        this.scope = scope;
    }

    public void generate(VariableReference variableReference) {
        String varName = variableReference.geName();
        int index = scope.getLocalVariableIndex(varName);
        LocalVariable localVariable = scope.getLocalVariable(varName);
        Type type = localVariable.getType();
        methodVisitor.visitVarInsn(type.getLoadVariableOpcode(), index);
    }
}