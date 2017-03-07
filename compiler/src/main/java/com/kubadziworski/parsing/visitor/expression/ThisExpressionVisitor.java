package com.kubadziworski.parsing.visitor.expression;


import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.scope.FunctionScope;

public class ThisExpressionVisitor extends EnkelParserBaseVisitor<LocalVariableReference> {

    private final FunctionScope functionScope;

    public ThisExpressionVisitor(FunctionScope functionScope) {
        this.functionScope = functionScope;
    }

    public LocalVariableReference visitThisReference(EnkelParser.ThisReferenceContext ctx) {
        return new LocalVariableReference(new RuleContextElementImpl(ctx), functionScope.getLocalVariable("this"));
    }
}
