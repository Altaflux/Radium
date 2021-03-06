package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.ThrowStatement;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.IncompatibleTypesException;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;


public class ThrowStatementVisitor extends EnkelParserBaseVisitor<ThrowStatement> {

    private final ExpressionVisitor expressionVisitor;
    private final Type throwableType = ClassTypeFactory.createClassType("java.lang.Throwable");

    public ThrowStatementVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public ThrowStatement visitThrowStatement(EnkelParser.ThrowStatementContext ctx) {
        EnkelParser.ExpressionContext expressionCtx = ctx.expression();
        Expression expression = expressionCtx.accept(expressionVisitor);

        if (expression.getType().inheritsFrom(throwableType) < 0) {
            throw new IncompatibleTypesException("throw statement", throwableType, expression.getType());
        }

        return new ThrowStatement(new RuleContextElementImpl(ctx), expression);
    }
}
