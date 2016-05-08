package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.domain.classs.Constructor;
import com.kubadziworski.domain.classs.Function;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

import static java.util.stream.Collectors.toList;

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
        List<Type> parameterTypes = ctx.functionDeclaration().functionParameter().stream()
                .map(p -> TypeResolver.getFromTypeName(p.type())).collect(toList());
        FunctionSignature signature = scope.getMethodCallSignature(ctx.functionDeclaration().functionName().getText(),parameterTypes);
        scope.addLocalVariable(new LocalVariable("this",scope.getClassType()));
        addParametersAsLocalVariables(signature);
        Statement block = getBlock(ctx);
        if(signature.getName().equals(scope.getClassName())) {
            return new Constructor(signature,block);
        }
        return new Function(signature, block);
    }

    private void addParametersAsLocalVariables(FunctionSignature signature) {
        signature.getParameters().stream()
                .forEach(param -> scope.addLocalVariable(new LocalVariable(param.getName(), param.getType())));
    }

    private Statement getBlock(FunctionContext functionContext) {
        StatementVisitor statementVisitor = new StatementVisitor(scope);
        EnkelParser.BlockContext block = functionContext.block();
        return block.accept(statementVisitor);
    }
}
