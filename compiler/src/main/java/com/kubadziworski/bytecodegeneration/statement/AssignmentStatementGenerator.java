package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.expression.Reference;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Optional;

public class AssignmentStatementGenerator {
    private final MethodVisitor methodVisitor;
    private final ExpressionGenerator expressionGenerator;
    private final Scope scope;

    public AssignmentStatementGenerator(MethodVisitor methodVisitor, ExpressionGenerator expressionGenerator, Scope scope) {
        this.methodVisitor = methodVisitor;
        this.expressionGenerator = expressionGenerator;
        this.scope = scope;
    }

    public void generate(Assignment assignment) {
        String varName = assignment.getVarName();
        Expression expression = assignment.getAssignmentExpression();
        Type type = expression.getType();


        Field field;
        String descriptor;
        Optional<Expression> preExpression = assignment.getPreExpression();
        if (preExpression.isPresent()) {
            field = scope.getField(preExpression.get().getType(), varName);
            descriptor = field.getType().getDescriptor();
            preExpression.get().accept(expressionGenerator);
        } else {

            if (scope.isLocalVariableExists(varName)) {
                int index = scope.getLocalVariableIndex(varName);
                LocalVariable localVariable = scope.getLocalVariable(varName);
                Type localVariableType = localVariable.getType();
                castIfNecessary(type, localVariableType);
                methodVisitor.visitVarInsn(type.getStoreVariableOpcode(), index);
                return;
            }

            field = scope.getField(varName);
            descriptor = field.getType().getDescriptor();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        }

        expression.accept(expressionGenerator);
        castIfNecessary(type, field.getType());
        methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, field.getOwnerInternalName(), field.getName(), descriptor);
    }

    public void generate(Reference reference) {

        if (reference instanceof LocalVariableReference) {
            int varIndex = scope.getLocalVariableIndex(reference.geName());
            methodVisitor.visitVarInsn(reference.getType().getStoreVariableOpcode(), varIndex);

        } else if (reference instanceof FieldReference) {
            String descriptor = ((FieldReference) reference).getField().getType().getDescriptor();
            methodVisitor.visitFieldInsn(org.objectweb.asm.Opcodes.PUTFIELD, ((FieldReference) reference).getField().getOwnerInternalName(), ((FieldReference) reference).getField().getName(), descriptor);
        } else {
            throw new RuntimeException("Reference of unknown type: " + reference.getClass().getSimpleName());
        }
    }

    private void castIfNecessary(Type expressionType, Type variableType) {
        if (!expressionType.equals(variableType)) {
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, variableType.getInternalName());
        }
    }
}