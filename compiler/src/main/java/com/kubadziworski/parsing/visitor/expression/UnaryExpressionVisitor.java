package com.kubadziworski.parsing.visitor.expression;


import com.kubadziworski.antlr.EnkelParser.PrefixExpressionContext;
import com.kubadziworski.antlr.EnkelParser.SignExpressionContext;
import com.kubadziworski.antlr.EnkelParser.SuffixExpressionContext;
import com.kubadziworski.antlr.EnkelParser.UnaryExpressionContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.UnaryOperator;
import com.kubadziworski.domain.UnarySign;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.Reference;
import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeChecker;


public class UnaryExpressionVisitor extends EnkelParserBaseVisitor<Expression> {

    private final ExpressionVisitor expressionVisitor;

    public UnaryExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }


    public Expression visitPrefixExpression(PrefixExpressionContext ctx) {
        UnaryOperator operator = UnaryOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expression().accept(expressionVisitor);
        Reference ref = (Reference) expression;
        return new IncrementDecrementExpression(new RuleContextElementImpl(ctx), ref, true, operator);
    }


    public Expression visitSuffixExpression(SuffixExpressionContext ctx) {
        UnaryOperator operator = UnaryOperator.fromString(ctx.operation.getText());
        Expression expression = ctx.expr.accept(expressionVisitor);
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
