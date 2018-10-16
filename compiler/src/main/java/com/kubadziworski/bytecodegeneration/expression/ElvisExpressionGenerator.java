package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.util.AsmUtil;
import com.kubadziworski.domain.node.expression.ElvisExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.IFNULL;

public class ElvisExpressionGenerator {

    private final MethodVisitor methodVisitor;

    public ElvisExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(ElvisExpression elvisExpression, StatementGenerator statementGenerator) {
        Expression leftExpression  = elvisExpression.getLeftExpression();
        leftExpression.accept(statementGenerator);
        if(leftExpression.getType().isNullable().equals(Type.Nullability.NOT_NULL)) {
            return;
        }

        AsmUtil.duplicateStackValue(leftExpression.getType().getAsmType(), methodVisitor, 0);
        Label ifNullLabel = new Label();
        Label notNullLabel = new Label();

        methodVisitor.visitJumpInsn(IFNULL, ifNullLabel);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, notNullLabel);
        methodVisitor.visitLabel(ifNullLabel);

        AsmUtil.popStackValue(leftExpression.getType(), methodVisitor);
        Expression rightExpression  = elvisExpression.getRightExpression();
        rightExpression.accept(statementGenerator);

        methodVisitor.visitLabel(notNullLabel);

    }
}
