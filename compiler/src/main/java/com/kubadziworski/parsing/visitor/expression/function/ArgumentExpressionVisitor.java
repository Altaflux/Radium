package com.kubadziworski.parsing.visitor.expression.function;

import com.kubadziworski.antlr.EnkelParser.ArgumentContext;
import com.kubadziworski.antlr.EnkelParser.NamedArgumentContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;

/**
 * Created by kuba on 09.05.16.
 */
public class ArgumentExpressionVisitor extends EnkelParserBaseVisitor<ArgumentHolder> {

    private final ExpressionVisitor expressionVisitor;

    public ArgumentExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public ArgumentHolder visitArgument(ArgumentContext ctx) {
        Expression value = ctx.expression().accept(expressionVisitor);
        return new ArgumentHolder(value, null);
    }

    @Override
    public ArgumentHolder visitNamedArgument(NamedArgumentContext ctx) {
        Expression value = ctx.expression().accept(expressionVisitor);
        return new ArgumentHolder(value, ctx.name().getText());
    }

}
