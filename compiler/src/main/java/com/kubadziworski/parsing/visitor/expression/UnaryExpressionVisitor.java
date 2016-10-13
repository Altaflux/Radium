package com.kubadziworski.parsing.visitor.expression;


import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.*;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.Reference;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;


public class UnaryExpressionVisitor extends EnkelBaseVisitor<UnaryExpression> {

    private final ExpressionVisitor expressionVisitor;

    public UnaryExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    public UnaryExpression visitSuffixExpression(SuffixExpressionContext ctx) {
        ArithmeticOperator operator = ArithmeticOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expr.accept(expressionVisitor);
        Reference ref = (Reference) expression;
        return new UnaryExpression(ref, false, operator);
    }

    public UnaryExpression visitPrefixExpression(PrefixExpressionContext ctx) {
        ArithmeticOperator operator = ArithmeticOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expression().accept(expressionVisitor);
        Reference ref = (Reference) expression;
        return new UnaryExpression(ref, true, operator);
    }
}
