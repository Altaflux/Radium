package com.kubadziworski.parsing.visitor.expression;


import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.*;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.arthimetic.Addition;
import com.kubadziworski.domain.node.expression.arthimetic.Substraction;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.util.PropertyAccessorsUtil;

import java.util.Collections;
import java.util.Optional;


public class UnaryExpressionVisitor extends EnkelBaseVisitor<Expression> {

    private final ExpressionVisitor expressionVisitor;

    public UnaryExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

//    public UnaryExpression visitSuffixExpression(SuffixExpressionContext ctx) {
//        ArithmeticOperator operator = ArithmeticOperator.fromString(ctx.operation.getText());
//        Expression expression = ctx.expr.accept(expressionVisitor);
//        Reference ref = (Reference) expression;
//        return new UnaryExpression(ref, false, operator);
//    }

    public Expression visitSuffixExpression(SuffixExpressionContext ctx) {
        ArithmeticOperator operator = ArithmeticOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expr.accept(expressionVisitor);
        if (expression instanceof PropertyAccessorCall) {

            Field field = ((PropertyAccessorCall) expression).getField();
            FunctionSignature signature = PropertyAccessorsUtil.getSetterFunctionSignatureForField(field).get();
            Expression operation;

            if (operator.equals(ArithmeticOperator.INCREMENT)) {
                operation = new Addition(new DupExpression(expression), new Value(expression.getType(), "1"));
            } else {
                operation = new Substraction(new DupExpression(expression), new Value(expression.getType(), "1"));
            }
            Argument argument = new Argument(operation, Optional.empty());
            return new FunctionCall(signature, Collections.singletonList(argument), ((PropertyAccessorCall) expression).getOwner());
        }
        Reference ref = (Reference) expression;
        return new UnaryExpression(ref, false, operator);
    }

    public Expression visitPrefixExpression(PrefixExpressionContext ctx) {
        ArithmeticOperator operator = ArithmeticOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expression().accept(expressionVisitor);

        if (expression instanceof PropertyAccessorCall) {

            Field field = ((PropertyAccessorCall) expression).getField();
            FunctionSignature signature = PropertyAccessorsUtil.getSetterFunctionSignatureForField(field).get();
            Expression operation;

            if (operator.equals(ArithmeticOperator.INCREMENT)) {
                operation = new Addition(expression, new Value(expression.getType(), "1"));
            } else {
                operation = new Substraction(expression, new Value(expression.getType(), "1"));
            }
            Argument argument = new Argument(operation, Optional.empty());
            return new DupExpression(new FunctionCall(signature, Collections.singletonList(argument), ((PropertyAccessorCall) expression).getOwner()));
        }

        Reference ref = (Reference) expression;
        return new UnaryExpression(ref, true, operator);
    }
}
