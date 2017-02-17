package com.kubadziworski.parsing.visitor.expression;


import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.PrefixExpressionContext;
import com.kubadziworski.antlr.EnkelParser.SignExpressionContext;
import com.kubadziworski.antlr.EnkelParser.SuffixExpressionContext;
import com.kubadziworski.antlr.EnkelParser.UnaryExpressionContext;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.UnaryOperator;
import com.kubadziworski.domain.UnarySign;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.PropertyAccessorsUtil;
import com.kubadziworski.util.TypeChecker;

import java.util.Collections;
import java.util.List;


public class UnaryExpressionVisitor extends EnkelParserBaseVisitor<Expression> {

    private final ExpressionVisitor expressionVisitor;

    public UnaryExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }


    public Expression visitPrefixExpression(PrefixExpressionContext ctx) {
        UnaryOperator operator = UnaryOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expression().accept(expressionVisitor);
        if (expression instanceof PropertyAccessorCall) {
            Field field = ((PropertyAccessorCall) expression).getField();
            FunctionSignature signature = PropertyAccessorsUtil.getSetterFunctionSignatureForField(field).get();
            Expression operation;

            List<ArgumentHolder> arguments = Collections.singletonList(new ArgumentHolder(new Value(expression.getType(), "1"), null));
            if (operator.equals(UnaryOperator.INCREMENT)) {

                FunctionSignature plusSignature = expression.getType().getMethodCallSignature("plus", arguments);
                FunctionCall functionCall = new FunctionCall(plusSignature, plusSignature.createArgumentList(arguments), expression);
                operation = new DupExpression(functionCall, 1);
            } else {
                FunctionSignature plusSignature = expression.getType().getMethodCallSignature("minus", arguments);
                FunctionCall functionCall = new FunctionCall(plusSignature, plusSignature.createArgumentList(arguments), expression);
                operation = new DupExpression(functionCall, 1);
            }
            ArgumentHolder argument = new ArgumentHolder(operation, null);

            return new FakeReturnExpression(new FunctionCall(signature, signature.createArgumentList(Collections.singletonList(argument)),
                    ((PropertyAccessorCall) expression).getOwner()), expression.getType());
        }
        Reference ref = (Reference) expression;
        return new IncrementDecrementExpression(new RuleContextElementImpl(ctx), ref, true, operator);
    }


    public Expression visitSuffixExpression(SuffixExpressionContext ctx) {
        UnaryOperator operator = UnaryOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expr.accept(expressionVisitor);
        if (expression instanceof PropertyAccessorCall) {
            Field field = ((PropertyAccessorCall) expression).getField();
            FunctionSignature signature = PropertyAccessorsUtil.getSetterFunctionSignatureForField(field).get();
            Expression operation;

            List<ArgumentHolder> arguments = Collections.singletonList(new ArgumentHolder(new Value(expression.getType(), "1"), null));
            if (operator.equals(UnaryOperator.INCREMENT)) {
                FunctionSignature plusSignature = expression.getType().getMethodCallSignature("plus", arguments);
                operation = new FunctionCall(plusSignature, plusSignature.createArgumentList(arguments), expression);
            } else {
                FunctionSignature plusSignature = expression.getType().getMethodCallSignature("minus", arguments);
                operation = new FunctionCall(plusSignature, plusSignature.createArgumentList(arguments), expression);
            }
            ArgumentHolder argument = new ArgumentHolder(operation, null);
            return new ComposedExpression(expression,
                    new FakeReturnExpression(new FunctionCall(signature, signature.createArgumentList(Collections.singletonList(argument)),
                            ((PropertyAccessorCall) expression).getOwner()), expression.getType()));
        }
        Reference ref = (Reference) expression;
        return new IncrementDecrementExpression(new RuleContextElementImpl(ctx), ref, false, operator);
    }

    @Override
    public Expression visitUnaryExpression(UnaryExpressionContext ctx) {
        UnarySign unarySign = UnarySign.fromString(ctx.operation.getText());
        Expression expression = ctx.expression().accept(expressionVisitor);
        return new UnaryExpression(new RuleContextElementImpl(ctx), unarySign, expression);
    }

    @Override
    public Expression visitSignExpression(SignExpressionContext ctx) {
        UnarySign unarySign = UnarySign.fromString(ctx.operation.getText());
        Expression expression = ctx.expression().accept(expressionVisitor);
        if (expression instanceof Value) {
            if (TypeChecker.isNumber(expression.getType())) {
                if (unarySign.equals(UnarySign.ADD)) {
                    return expression;
                }

                return new Value(new RuleContextElementImpl(ctx), expression.getType(), negateNumber((Number) ((Value) expression).getValue()));
            }
        }

        return new UnaryExpression(new RuleContextElementImpl(ctx), unarySign, expression);
    }

    private static Number negateNumber(Number number) {
        if (number instanceof Integer) {
            return -number.intValue();
        }
        if (number instanceof Long) {
            return -number.longValue();
        }
        if (number instanceof Float) {
            return -number.floatValue();
        }
        if (number instanceof Double) {
            return -number.doubleValue();
        }
        if (number instanceof Short) {
            return -number.shortValue();
        }
        if (number instanceof Byte) {
            return -number.byteValue();
        }

        throw new RuntimeException("Unrecognized Number type");
    }

    private static class ComposedExpression extends ElementImpl implements Expression {
        private final Expression preExpression;
        private final Expression expression;

        ComposedExpression(Expression preExpression, Expression expression) {
            this.preExpression = preExpression;
            this.expression = expression;
        }

        @Override
        public Type getType() {
            return expression.getType();
        }


        @Override
        public void accept(StatementGenerator generator) {
            preExpression.accept(generator);
            expression.accept(generator);
        }
    }

    private static class FakeReturnExpression extends ElementImpl implements Expression {
        private final Expression expression;
        private final Type type;

        FakeReturnExpression(Expression expression, Type type) {
            this.expression = expression;
            this.type = type;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public void accept(StatementGenerator generator) {
            expression.accept(generator);
        }
    }
}
