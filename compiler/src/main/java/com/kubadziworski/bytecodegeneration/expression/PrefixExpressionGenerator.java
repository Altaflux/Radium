package com.kubadziworski.bytecodegeneration.expression;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.util.AsmUtil;
import com.kubadziworski.domain.UnaryOperator;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.expression.Reference;
import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.scope.Scope;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class PrefixExpressionGenerator {

    private final MethodVisitor methodVisitor;


    public PrefixExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }


    public void generate(IncrementDecrementExpression incrementDecrementExpression, Scope scope, StatementGenerator statementGenerator) {
        Reference reference = incrementDecrementExpression.getReference(); //x

        if (reference instanceof LocalVariableReference) {
            reference.accept(statementGenerator);
        } else if (reference instanceof FieldReference) {
            ((FieldReference) reference).acceptDup(statementGenerator);
        }


        int operationOpCode;
        if (incrementDecrementExpression.getOperator().equals(UnaryOperator.INCREMENT)) {
            operationOpCode = reference.getType().getAsmType().getOpcode(Opcodes.IADD);
        } else {
            operationOpCode = reference.getType().getAsmType().getOpcode(Opcodes.ISUB);
        }

        if (incrementDecrementExpression.isPrefix()) {
            statementGenerator.generate(new Value(reference.getType(), "1")); //ICONST_1
            methodVisitor.visitInsn(operationOpCode);
            AsmUtil.duplicateStackValue(reference.getType().getAsmType(), methodVisitor, reference instanceof LocalVariableReference ? 0 : 1);

        } else {
            AsmUtil.duplicateStackValue(reference.getType().getAsmType(), methodVisitor, reference instanceof LocalVariableReference ? 0 : 1);
            statementGenerator.generate(new Value(reference.getType(), "1")); //ICONST_1
            methodVisitor.visitInsn(operationOpCode);
        }

        if (reference instanceof LocalVariableReference) {
            int varIndex = scope.getLocalVariableIndex(reference.getName());
            methodVisitor.visitVarInsn(reference.getType().getAsmType().getOpcode(Opcodes.ISTORE), varIndex);

        } else if (reference instanceof FieldReference) {

            String descriptor = ((FieldReference) reference).getField().getType().getAsmType().getDescriptor();
            methodVisitor.visitFieldInsn(org.objectweb.asm.Opcodes.PUTFIELD,
                    ((FieldReference) reference).getField().getOwner().getAsmType().getInternalName(),
                    ((FieldReference) reference).getField().getName(), descriptor);
        } else {
            throw new RuntimeException("Reference of unknown type: " + reference.getClass().getSimpleName());
        }
    }


}
