package com.kubadziworski.parsing.visitor.expression.function;

import com.kubadziworski.antlr.EnkelParser.ParameterContext;
import com.kubadziworski.antlr.EnkelParser.ParameterWithDefaultValueContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.util.TypeResolver;


/**
 * Created by kuba on 09.05.16.
 */
public class ParameterExpressionVisitor extends EnkelParserBaseVisitor<Parameter> {

    private final ExpressionVisitor expressionVisitor;
    private final FunctionScope scope;

    public ParameterExpressionVisitor(ExpressionVisitor expressionVisitor, FunctionScope scope) {
        this.expressionVisitor = expressionVisitor;
        this.scope = scope;
    }

    @Override
    public Parameter visitParameter(ParameterContext ctx) {
        String name = ctx.SimpleName().getText();
        Type type = TypeResolver.getFromTypeContext(ctx.type(), scope);
        return new Parameter(name, type, null);
    }

    @Override
    public Parameter visitParameterWithDefaultValue(ParameterWithDefaultValueContext ctx) {
        String name = ctx.SimpleName().getText();
        Type type = TypeResolver.getFromTypeContext(ctx.type(), scope);
        Expression defaultValue = ctx.defaultValue.accept(expressionVisitor);
        return new Parameter(name, type, defaultValue);
    }
}
