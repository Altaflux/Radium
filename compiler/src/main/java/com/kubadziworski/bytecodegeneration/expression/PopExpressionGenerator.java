package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.util.AsmUtil;
import com.kubadziworski.domain.node.expression.PopExpression;
import org.objectweb.asm.MethodVisitor;

public class PopExpressionGenerator  {

    private final MethodVisitor methodVisitor;


    public PopExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(PopExpression popExpression,  StatementGenerator expressionGenerator) {

        popExpression.getOwner().accept(expressionGenerator);
        AsmUtil.popStackValue(popExpression.getType(), methodVisitor);
    }
}
