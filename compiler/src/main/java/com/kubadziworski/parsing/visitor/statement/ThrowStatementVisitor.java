package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.ThrowStatement;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.IncompatibleTypesException;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import org.antlr.v4.runtime.misc.NotNull;


public class ThrowStatementVisitor extends EnkelBaseVisitor<ThrowStatement> {

    private final ExpressionVisitor expressionVisitor;
    private final Type throwableType = new JavaClassType("java.lang.Throwable");

    public ThrowStatementVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public ThrowStatement visitThrowStatement(@NotNull EnkelParser.ThrowStatementContext ctx) {
        EnkelParser.ExpressionContext expressionCtx = ctx.expression();
        Expression expression = expressionCtx.accept(expressionVisitor);

        if (expression.getType().inheritsFrom(throwableType) < 0) {
            throw new IncompatibleTypesException("throw statement", throwableType, expression.getType());
        }

        return new ThrowStatement(expression);
    }
}
