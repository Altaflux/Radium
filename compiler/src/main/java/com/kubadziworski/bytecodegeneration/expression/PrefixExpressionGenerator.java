package com.kubadziworski.bytecodegeneration.expression;


import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.expression.Reference;
import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.node.expression.prefix.PrefixExpression;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.Scope;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static com.kubadziworski.domain.type.BultInType.DOUBLE;
import static com.kubadziworski.domain.type.BultInType.LONG;

public class PrefixExpressionGenerator {

    private final MethodVisitor methodVisitor;
    private final ExpressionGenerator expressionGenerator;
    private final Scope scope;

    public PrefixExpressionGenerator(MethodVisitor methodVisitor, ExpressionGenerator expressionGenerator, Scope scope) {
        this.methodVisitor = methodVisitor;
        this.expressionGenerator = expressionGenerator;
        this.scope = scope;
    }


    public void generate(PrefixExpression prefixExpression) {
        Reference reference = prefixExpression.getReference(); //x
        reference.acceptDup(expressionGenerator);

        int dupsCode;
        if (reference.getType() == DOUBLE || reference.getType() == LONG) {
            if (reference instanceof LocalVariableReference) {
                dupsCode = Opcodes.DUP2;
            } else {
                dupsCode = Opcodes.DUP2_X1;
            }

        } else {
            if (reference instanceof LocalVariableReference) {
                dupsCode = Opcodes.DUP;
            } else {
                dupsCode = Opcodes.DUP_X1;
            }

        }

        int operationOpCode;
        if (prefixExpression.getOperator().equals(ArithmeticOperator.INCREMENT)) {
            operationOpCode = reference.getType().getAddOpcode();
        } else {
            operationOpCode = reference.getType().getSubstractOpcode();
        }

        if (prefixExpression.isPrefix()) {
            expressionGenerator.generate(new Value(reference.getType(), prefixExpression.getOperator().getIncremental())); //ICONST_1
            methodVisitor.visitInsn(operationOpCode);
            methodVisitor.visitInsn(dupsCode);

        } else {
            methodVisitor.visitInsn(dupsCode);
            expressionGenerator.generate(new Value(reference.getType(), prefixExpression.getOperator().getIncremental())); //ICONST_1
            methodVisitor.visitInsn(operationOpCode);
        }

        if (reference instanceof LocalVariableReference) {
            int varIndex = scope.getLocalVariableIndex(reference.geName());
            methodVisitor.visitVarInsn(reference.getType().getStoreVariableOpcode(), varIndex);

        } else {
            Field field = scope.getField(reference.geName());
            String descriptor = field.getType().getDescriptor();
            methodVisitor.visitFieldInsn(org.objectweb.asm.Opcodes.PUTFIELD, field.getOwnerInternalName(), field.getName(), descriptor);
        }
    }


}