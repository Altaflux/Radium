package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.util.AsmUtil;
import com.kubadziworski.domain.node.expression.DupExpression;
import com.kubadziworski.domain.node.expression.Expression;
import org.objectweb.asm.MethodVisitor;


public class DupExpressionGenerator {
    private final MethodVisitor methodVisitor;

    public DupExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(DupExpression dupExpression, StatementGenerator statementGenerator) {
        Expression expression = dupExpression.getExpression();
        expression.accept(statementGenerator);

        AsmUtil.duplicateStackValue(expression.getType().getAsmType(), methodVisitor, dupExpression.getDupShift());

    }
}
