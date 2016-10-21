package com.kubadziworski.parsing.visitor.expression.function;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.ParameterContext;
import com.kubadziworski.antlr.EnkelParser.ParameterWithDefaultValueContext;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeResolver;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import org.antlr.v4.runtime.misc.NotNull;


/**
 * Created by kuba on 09.05.16.
 */
public class ParameterExpressionVisitor extends EnkelBaseVisitor<Parameter> {

    private final ExpressionVisitor expressionVisitor;
    private final Scope scope;

    public ParameterExpressionVisitor(ExpressionVisitor expressionVisitor, Scope scope) {
        this.expressionVisitor = expressionVisitor;
        this.scope = scope;
    }

    @Override
    public Parameter visitParameter(@NotNull ParameterContext ctx) {
        String name = ctx.ID().getText();
        Type type = TypeResolver.getFromTypeContext(ctx.type(), scope);
        return new Parameter(name, type, null);
    }

    @Override
    public Parameter visitParameterWithDefaultValue(@NotNull ParameterWithDefaultValueContext ctx) {
        String name = ctx.ID().getText();
        Type type = TypeResolver.getFromTypeContext(ctx.type(), scope);
        Expression defaultValue = ctx.defaultValue.accept(expressionVisitor);
        return new Parameter(name, type, defaultValue);
    }
}
