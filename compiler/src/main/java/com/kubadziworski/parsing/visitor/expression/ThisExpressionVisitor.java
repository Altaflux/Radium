package com.kubadziworski.parsing.visitor.expression;


import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.scope.Scope;

public class ThisExpressionVisitor extends EnkelParserBaseVisitor<LocalVariableReference> {

    private final Scope scope;

    public ThisExpressionVisitor(Scope scope) {
        this.scope = scope;
    }

    public LocalVariableReference visitThisReference(EnkelParser.ThisReferenceContext ctx) {
        return new LocalVariableReference(new RuleContextElementImpl(ctx), scope.getLocalVariable("this"));
    }
}
