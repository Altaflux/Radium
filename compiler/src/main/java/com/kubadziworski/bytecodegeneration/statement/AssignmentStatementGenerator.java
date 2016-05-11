package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;

public class AssignmentStatementGenerator {
    private final MethodVisitor methodVisitor;
    private final Scope scope;

    public AssignmentStatementGenerator(MethodVisitor methodVisitor, Scope scope) {
        this.methodVisitor = methodVisitor;
        this.scope = scope;
    }

    public void generate(Assignment assignment) {
        String varName = assignment.getVarName();
        Type type = assignment.getExpression().getType();
        int index = scope.getLocalVariableIndex(varName);
        methodVisitor.visitVarInsn(type.getStoreVariableOpcode(), index);
    }
}