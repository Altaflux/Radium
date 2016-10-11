package com.kubadziworski.parsing.visitor.expression;


import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.scope.Scope;

public class ThisExpressionVisitor extends EnkelBaseVisitor<LocalVariableReference> {

    private final Scope scope;

    public ThisExpressionVisitor(Scope scope) {
        this.scope = scope;
    }

    public LocalVariableReference visitThisReference(EnkelParser.ThisReferenceContext ctx) {
        return new LocalVariableReference(scope.getLocalVariable("this"));
    }
}
