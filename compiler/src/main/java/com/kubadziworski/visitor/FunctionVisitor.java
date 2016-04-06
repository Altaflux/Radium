package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.antlr.EnkelParser.TypeContext;
import com.kubadziworski.antlr.domain.scope.Scope;
import com.kubadziworski.antlr.domain.classs.Function;
import com.kubadziworski.antlr.domain.expression.Identifier;
import com.kubadziworski.antlr.domain.expression.FunctionParameter;
import com.kubadziworski.antlr.domain.type.Type;
import com.kubadziworski.antlr.domain.statement.Statement;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by kuba on 01.04.16.
 */
public class FunctionVisitor extends EnkelBaseVisitor<Function> {

    private Scope scope;
    public FunctionVisitor(Scope scope) {
        this.scope = new Scope(scope);
    }

    @Override
    public Function visitFunction(@NotNull EnkelParser.FunctionContext ctx) {
        String name = getName(ctx);
        Type returnType = getReturnType(ctx);
        List<FunctionParameter> arguments = getArguments(ctx);
        List<Statement> instructions = getStatements(ctx);
        return new Function(scope,name,returnType,arguments,instructions);
    }

    private String getName(FunctionContext functionDeclarationContext) {
        return functionDeclarationContext.functionDeclaration().functionName().getText();
    }

    private Type getReturnType(FunctionContext functionDeclarationContext) {
        TypeContext typeCtx = functionDeclarationContext.functionDeclaration().type();
        TypeVisitor typeVisitor = new TypeVisitor();
        return typeCtx.accept(typeVisitor);
    }

    private List<FunctionParameter> getArguments(FunctionContext functionDeclarationContext) {
        List<EnkelParser.FunctionArgumentContext> argsCtx = functionDeclarationContext.functionDeclaration().functionArgument();
        List<FunctionParameter> parameters = argsCtx.stream()
                .map(paramCtx -> new FunctionParameter(paramCtx.ID().getText(), paramCtx.type().accept(new TypeVisitor()), paramCtx.index))
                .peek(param -> scope.addIdentifier(new Identifier(param.getName(), param)))
                .collect(Collectors.toList());
        return parameters;
    }

    private List<Statement> getStatements(@NotNull FunctionContext ctx) {
        StatementVisitor statementVisitor = new StatementVisitor(scope);
        ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
        CompositeVisitor<Statement> compositeVisitor = new CompositeVisitor<>(statementVisitor, expressionVisitor);
        return ctx.blockStatement().stream()
                    .map(compositeVisitor::accept)
                    .collect(Collectors.toList());
    }
}
