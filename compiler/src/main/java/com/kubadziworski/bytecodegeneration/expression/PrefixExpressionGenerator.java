package com.kubadziworski.bytecodegeneration.expression;


import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.expression.Reference;
import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.scope.Scope;
import org.objectweb.asm.MethodVisitor;

public class PrefixExpressionGenerator {

    private final MethodVisitor methodVisitor;
    private final ExpressionGenerator expressionGenerator;
    private final Scope scope;

    public PrefixExpressionGenerator(MethodVisitor methodVisitor, ExpressionGenerator expressionGenerator, Scope scope) {
        this.methodVisitor = methodVisitor;
        this.expressionGenerator = expressionGenerator;
        this.scope = scope;
    }


    public void generate(IncrementDecrementExpression incrementDecrementExpression) {
        Reference reference = incrementDecrementExpression.getReference(); //x

        if (reference instanceof LocalVariableReference) {
            reference.accept(expressionGenerator);
        } else if (reference instanceof FieldReference) {
            ((FieldReference) reference).acceptDup(expressionGenerator);
        }

        int dupsCode;
        if (reference instanceof LocalVariableReference) {
            dupsCode = reference.getType().getDupCode();
        } else {
            dupsCode = reference.getType().getDupX1Code();
        }

        int operationOpCode;
        if (incrementDecrementExpression.getOperator().equals(ArithmeticOperator.INCREMENT)) {
            operationOpCode = reference.getType().getAddOpcode();
        } else {
            operationOpCode = reference.getType().getSubstractOpcode();
        }

        if (incrementDecrementExpression.isPrefix()) {
            expressionGenerator.generate(new Value(reference.getType(), "1")); //ICONST_1
            methodVisitor.visitInsn(operationOpCode);
            methodVisitor.visitInsn(dupsCode);

        } else {
            methodVisitor.visitInsn(dupsCode);
            expressionGenerator.generate(new Value(reference.getType(), "1")); //ICONST_1
            methodVisitor.visitInsn(operationOpCode);
        }

        if (reference instanceof LocalVariableReference) {
            int varIndex = scope.getLocalVariableIndex(reference.getName());
            methodVisitor.visitVarInsn(reference.getType().getStoreVariableOpcode(), varIndex);

        } else if (reference instanceof FieldReference) {

            String descriptor = ((FieldReference) reference).getField().getType().getDescriptor();
            methodVisitor.visitFieldInsn(org.objectweb.asm.Opcodes.PUTFIELD, ((FieldReference) reference).getField().getOwnerInternalName(), ((FieldReference) reference).getField().getName(), descriptor);
        } else {
            throw new RuntimeException("Reference of unknown type: " + reference.getClass().getSimpleName());
        }
    }


}
