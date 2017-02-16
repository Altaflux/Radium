package com.kubadziworski.bytecodegeneration.expression;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.util.AsmUtil;
import com.kubadziworski.domain.UnaryOperator;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.scope.Scope;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;

public class PrefixExpressionGenerator {

    private final MethodVisitor methodVisitor;


    public PrefixExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }


    public void generate(IncrementDecrementExpression incrementDecrementExpression, Scope scope, StatementGenerator statementGenerator) {
        Reference reference = incrementDecrementExpression.getReference(); //x
        Type asmType = reference.getType().getAsmType();

        //Use IINC instruction if possible
        if (reference instanceof LocalVariableReference && asmType.getSort() == Type.INT) {
            incLocalVariable(incrementDecrementExpression, statementGenerator, scope);
            return;
        }

        boolean isStaticField = (reference instanceof FieldReference && (((FieldReference) reference).getOwner() instanceof EmptyExpression) &&
                (Modifier.isStatic(((FieldReference) reference).getField().getModifiers())));

        if (reference instanceof LocalVariableReference || reference instanceof FieldReference && isStaticField) {
            reference.accept(statementGenerator);
        } else if (reference instanceof FieldReference) {
            Expression owner = ((FieldReference) reference).getOwner();
            owner.accept(statementGenerator);
            methodVisitor.visitInsn(Opcodes.DUP);
            FieldReference nReference = new FieldReference(((FieldReference) reference).getField(), new EmptyExpression(owner.getType()));
            nReference.accept(statementGenerator);
        }


        int operationOpCode = incrementDecrementExpression.getOperator().equals(UnaryOperator.INCREMENT) ? asmType.getOpcode(Opcodes.IADD) : Opcodes.ISUB;
        if (incrementDecrementExpression.isPrefix()) {
            statementGenerator.generate(new Value(reference.getType(), "1"));
            methodVisitor.visitInsn(operationOpCode);
            AsmUtil.duplicateStackValue(asmType, methodVisitor,
                    (reference instanceof LocalVariableReference || isStaticField) ? 0 : 1);

        } else {
            AsmUtil.duplicateStackValue(asmType, methodVisitor,
                    (reference instanceof LocalVariableReference || isStaticField) ? 0 : 1);
            statementGenerator.generate(new Value(reference.getType(), "1")); //ICONST_1
            methodVisitor.visitInsn(operationOpCode);
        }

        if (reference instanceof LocalVariableReference) {
            int varIndex = scope.getLocalVariableIndex(reference.getName());
            methodVisitor.visitVarInsn(asmType.getOpcode(Opcodes.ISTORE), varIndex);

        } else if (reference instanceof FieldReference) {
            FieldReference fieldReference = (FieldReference) reference;
            FieldAssignment assignment = new FieldAssignment(new EmptyExpression(fieldReference.getOwner().getType()),
                    fieldReference.getField(), new EmptyExpression(fieldReference.getType()));

            assignment.accept(statementGenerator);
        } else {
            throw new RuntimeException("Reference of unknown type: " + reference.getClass().getSimpleName());
        }
    }


    private void incLocalVariable(IncrementDecrementExpression incrementDecrementExpression, StatementGenerator generator, Scope scope) {
        Reference reference = incrementDecrementExpression.getReference();
        int varIndex = scope.getLocalVariableIndex(reference.getName());
        int incremental = incrementDecrementExpression.getOperator().equals(UnaryOperator.INCREMENT) ? 1 : -1;

        if (incrementDecrementExpression.isPrefix()) {
            methodVisitor.visitIincInsn(varIndex, incremental);
            reference.accept(generator);
        } else {
            reference.accept(generator);
            methodVisitor.visitIincInsn(varIndex, incremental);
        }
    }
}
