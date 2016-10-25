package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.node.expression.FunctionCall;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.ReflectionUtils;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.Optional;

public class ReferenceExpressionGenerator {
    private final MethodVisitor methodVisitor;
    private final Scope scope;
    private final ExpressionGenerator expressionGenerator;

    public ReferenceExpressionGenerator(MethodVisitor methodVisitor, Scope scope, ExpressionGenerator expressionGenerator) {
        this.methodVisitor = methodVisitor;
        this.scope = scope;
        this.expressionGenerator = expressionGenerator;
    }

    public void generate(LocalVariableReference localVariableReference) {
        String varName = localVariableReference.getName();
        int index = scope.getLocalVariableIndex(varName);
        Type type = localVariableReference.getType();
        methodVisitor.visitVarInsn(type.getLoadVariableOpcode(), index);
    }


    public void generate(FieldReference fieldReference) {
        String varName = fieldReference.getName();
        Type type = fieldReference.getType();
        String ownerInternalName = fieldReference.getOwnerInternalName();
        String descriptor = type.getDescriptor();

        Expression owner = fieldReference.getOwner();
        owner.accept(expressionGenerator);
        methodVisitor.visitFieldInsn(fieldReference.getField().getInvokeOpcode(), ownerInternalName, varName, descriptor);
    }

    public void generateDup(FieldReference fieldReference) {
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