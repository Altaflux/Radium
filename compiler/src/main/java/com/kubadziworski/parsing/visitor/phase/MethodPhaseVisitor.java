package com.kubadziworski.parsing.visitor.phase;


import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.FunctionSignatureVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

class MethodPhaseVisitor extends EnkelBaseVisitor<Scope> {
    private final Scope scope;

    MethodPhaseVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope visitClassDeclaration(@NotNull EnkelParser.ClassDeclarationContext ctx) {
        List<EnkelParser.FunctionContext> methodsCtx = ctx.classBody().function();
        FunctionSignatureVisitor functionSignatureVisitor = new FunctionSignatureVisitor(scope);
        methodsCtx.stream()
                .map(method -> method.functionDeclaration().accept(functionSignatureVisitor))
                .forEach(signature -> {
                    if (signature.getName().equals(scope.getClassName())) {
                        scope.addConstructor(signature);
                    } else {
                        scope.addSignature(signature);
                    }
                });

        return scope;
    }
}
