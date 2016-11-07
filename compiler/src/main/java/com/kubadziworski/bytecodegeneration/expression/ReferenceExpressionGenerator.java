package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ReferenceExpressionGenerator {
    private final MethodVisitor methodVisitor;


    public ReferenceExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(LocalVariableReference localVariableReference, Scope scope) {
        String varName = localVariableReference.getName();
        int index = scope.getLocalVariableIndex(varName);
        Type type = localVariableReference.getType();
        methodVisitor.visitVarInsn(type.getLoadVariableOpcode(), index);
    }


    public void generate(FieldReference fieldReference, StatementGenerator expressionGenerator) {
        String varName = fieldReference.getName();
        Type type = fieldReference.getType();
        String ownerInternalName = fieldReference.getOwnerInternalName();
        String descriptor = type.getDescriptor();

        Expression owner = fieldReference.getOwner();
        owner.accept(expressionGenerator);
        methodVisitor.visitFieldInsn(fieldReference.getField().getInvokeOpcode(), ownerInternalName, varName, descriptor);
    }

    public void generateDup(FieldReference fieldReference, StatementGenerator expressionGenerator) {
        String varName = fieldReference.getName();
        Type type = fieldReference.getType();
        String ownerInternalName = fieldReference.getOwnerInternalName();
        String descriptor = type.getDescriptor();

        Expression owner = fieldReference.getOwner();
        owner.accept(expressionGenerator);
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, ownerInternalName, varName, descriptor);
    }
}