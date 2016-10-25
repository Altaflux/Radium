package com.kubadziworski.parsing.visitor.expression;


import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.*;
import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.arthimetic.Addition;
import com.kubadziworski.domain.node.expression.arthimetic.Substraction;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.PropertyAccessorsUtil;

import java.util.Collections;
import java.util.Optional;


public class UnaryExpressionVisitor extends EnkelBaseVisitor<Expression> {

    private final ExpressionVisitor expressionVisitor;

    public UnaryExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }


    public Expression visitPrefixExpression(PrefixExpressionContext ctx) {
        ArithmeticOperator operator = ArithmeticOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expression().accept(expressionVisitor);
        if (expression instanceof PropertyAccessorCall) {
            Field field = ((PropertyAccessorCall) expression).getField();
            FunctionSignature signature = PropertyAccessorsUtil.getSetterFunctionSignatureForField(field).get();
            Expression operation;

            if (operator.equals(ArithmeticOperator.INCREMENT)) {
                operation = new DupExpression(new Addition((expression), new Value(expression.getType(), "1")));
            } else {
                operation = new DupExpression(new Substraction((expression), new Value(expression.getType(), "1")));
            }
            Argument argument = new Argument(operation, null);

            return new FakeReturnExpression(new FunctionCall(signature, Collections.singletonList(argument),
                    ((PropertyAccessorCall) expression).getOwner()), expression.getType());
        }
        Reference ref = (Reference) expression;
        return new UnaryExpression(ref, true, operator);
    }


    public Expression visitSuffixExpression(SuffixExpressionContext ctx) {
        ArithmeticOperator operator = ArithmeticOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expr.accept(expressionVisitor);
        if (expression instanceof PropertyAccessorCall) {
            Field field = ((PropertyAccessorCall) expression).getField();
            FunctionSignature signature = PropertyAccessorsUtil.getSetterFunctionSignatureForField(field).get();
            Expression operation;

            if (operator.equals(ArithmeticOperator.INCREMENT)) {
                operation = (new Addition((expression), new Value(expression.getType(), "1")));
            } else {
                operation = (new Substraction((expression), new Value(expression.getType(), "1")));
            }
            Argument argument = new Argument(operation, null);
            return new ComposedExpression(expression,
                    new FakeReturnExpression(new FunctionCall(signature, Collections.singletonList(argument),
                            ((PropertyAccessorCall) expression).getOwner()), expression.getType()));
        }
        Reference ref = (Reference) expression;
        return new UnaryExpression(ref, false, operator);
    }

    private static class ComposedExpression implements Expression {
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
        public void accept(ExpressionGenerator generator) {
            preExpression.accept(generator);
            expression.accept(generator);
        }

        @Override
        public void accept(StatementGenerator generator) {
            preExpression.accept(generator);
            expression.accept(generator);
        }
    }

    private static class FakeReturnExpression implements Expression {
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
        public void accept(ExpressionGenerator generator) {
            expression.accept(generator);
        }

        @Override
        public void accept(StatementGenerator generator) {
            expression.accept(generator);
        }
    }
}
