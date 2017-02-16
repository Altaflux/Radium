package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Scope;
import org.antlr.v4.runtime.misc.NotNull;


public class FunctionContentVisitor extends EnkelParserBaseVisitor<Statement> {

    private final Scope scope;

    public FunctionContentVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Statement visitFunctionContent(@NotNull EnkelParser.FunctionContentContext ctx) {

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
