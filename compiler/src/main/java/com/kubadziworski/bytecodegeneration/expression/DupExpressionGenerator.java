package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.DupExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;


public class DupExpressionGenerator{
    private final MethodVisitor methodVisitor;

    public DupExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(DupExpression dupExpression, StatementGenerator statementGenerator) {
        Expression expression = dupExpression.getExpression();
        expression.accept(statementGenerator);

        Type type = expression.getType();
        System.out.println("Using dup for type: " + type);
        int dupCode = type.getDupX1Code();
        methodVisitor.visitInsn(dupCode);
    }
}
