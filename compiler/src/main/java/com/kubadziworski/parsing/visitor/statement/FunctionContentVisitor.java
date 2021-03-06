package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.FunctionScope;


public class FunctionContentVisitor extends EnkelParserBaseVisitor<Statement> {

    private final FunctionScope scope;

    public FunctionContentVisitor(FunctionScope scope) {
        this.scope = scope;
    }

    @Override
    public Statement visitFunctionContent(EnkelParser.FunctionContentContext ctx) {

        EnkelParser.BlockContext blockContext = ctx.block();
        StatementVisitor visitor = new StatementVisitor(scope);
        if (blockContext != null) {
            return blockContext.accept(visitor);
        } else {
            EnkelParser.BlockStatementContext blockStatementContext = ctx.blockStatement();
            return blockStatementContext.accept(visitor);
        }
    }
}
