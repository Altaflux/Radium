package com.kubadziworski.parsing.visitor.expression;


import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.*;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.Reference;
import com.kubadziworski.domain.node.expression.prefix.PrefixExpression;


public class IncDecExpressionVisitor extends EnkelBaseVisitor<PrefixExpression> {

    private final ExpressionVisitor expressionVisitor;

    public IncDecExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    public PrefixExpression visitSuffixExpression(SuffixExpressionContext ctx) {
        ArithmeticOperator operator = ArithmeticOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expr.accept(expressionVisitor);
        Reference ref = (Reference) expression;
        return new PrefixExpression(ref, false, operator);
    }

    public PrefixExpression visitPrefixExpression(PrefixExpressionContext ctx) {
        ArithmeticOperator operator = ArithmeticOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expression().accept(expressionVisitor);
        Reference ref = (Reference) expression;
        return new PrefixExpression(ref, true, operator);
    }
}
