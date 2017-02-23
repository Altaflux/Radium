package com.kubadziworski.parsing.visitor.phase;


import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.FunctionSignatureVisitor;

import java.util.List;

class MethodPhaseVisitor extends EnkelParserBaseVisitor<Scope> {
    private final Scope scope;

    MethodPhaseVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope visitClassDeclaration(EnkelParser.ClassDeclarationContext ctx) {
        List<EnkelParser.FunctionContext> methodsCtx = ctx.classBody().function();
        FunctionSignatureVisitor functionSignatureVisitor = new FunctionSignatureVisitor(scope);
        methodsCtx.stream()
                .map(method -> method.functionDeclaration().accept(functionSignatureVisitor))
                .forEach(scope::addSignature);

        return scope;
    }
}
