package com.kubadziworski.parsing.visitor.expression.function;

import com.kubadziworski.antlr.EnkelParser.NamedArgumentsListContext;
import com.kubadziworski.antlr.EnkelParser.UnnamedArgumentsListContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by kuba on 09.05.16.
 */
public class ArgumentExpressionsListVisitor extends EnkelParserBaseVisitor<List<ArgumentHolder>> {
    private final ExpressionVisitor expressionVisitor;

    public ArgumentExpressionsListVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public List<ArgumentHolder> visitUnnamedArgumentsList(UnnamedArgumentsListContext ctx) {
        ArgumentExpressionVisitor argumentExpressionVisitor = new ArgumentExpressionVisitor(expressionVisitor);
        return ctx.argument().stream()
                .map(a -> a.accept(argumentExpressionVisitor)).collect(toList());
    }

    @Override
    public List<ArgumentHolder> visitNamedArgumentsList(NamedArgumentsListContext ctx) {
        ArgumentExpressionVisitor argumentExpressionVisitor = new ArgumentExpressionVisitor(expressionVisitor);
        return ctx.namedArgument().stream()
                .map(a -> a.accept(argumentExpressionVisitor)).collect(toList());
    }
}
