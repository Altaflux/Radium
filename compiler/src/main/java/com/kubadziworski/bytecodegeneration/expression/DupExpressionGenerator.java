package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.domain.node.expression.DupExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;


public class DupExpressionGenerator {

    private final MethodVisitor methodVisitor;
    private final ExpressionGenerator expressionGenerator;

    public DupExpressionGenerator(MethodVisitor methodVisitor, ExpressionGenerator expressionGenerator) {
        this.methodVisitor = methodVisitor;
        this.expressionGenerator = expressionGenerator;
    }

    public void generate(DupExpression dupExpression) {
        Expression expression = dupExpression.getExpression();
        expression.accept(expressionGenerator);

        Type type = expression.getType();
        int dupCode = type.getDupCode();
        methodVisitor.visitInsn(dupCode);
    }
}
