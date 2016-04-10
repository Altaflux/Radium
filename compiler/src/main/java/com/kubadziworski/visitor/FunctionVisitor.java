package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.antlr.EnkelParser.TypeContext;
import com.kubadziworski.domain.classs.Function;
import com.kubadziworski.domain.expression.FunctionParameter;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
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
        return TypeResolver.getFromTypeName(typeCtx);
    }

    private List<FunctionParameter> getArguments(FunctionContext functionDeclarationContext) {
        List<EnkelParser.FunctionArgumentContext> argsCtx = functionDeclarationContext.functionDeclaration().functionArgument();
        List<FunctionParameter> parameters = argsCtx.stream()
                .map(paramCtx -> new FunctionParameter(paramCtx.ID().getText(), TypeResolver.getFromTypeName(paramCtx.type())))
                .peek(param -> scope.addLocalVariable(new LocalVariable(param.getName(), param.getType())))
                .collect(Collectors.toList());
        return parameters;
    }

    private List<Statement> getStatements(@NotNull FunctionContext ctx) {
        StatementVisitor statementVisitor = new StatementVisitor(scope);
        return ctx.blockStatement().stream()
                    .map(block -> block.accept(statementVisitor))
                    .collect(Collectors.toList());
    }
}
