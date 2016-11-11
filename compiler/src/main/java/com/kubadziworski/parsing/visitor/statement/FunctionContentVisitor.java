package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Scope;
import org.antlr.v4.runtime.misc.NotNull;


public class FunctionContentVisitor extends EnkelBaseVisitor<Statement> {

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
            EnkelParser.StatementContext blockStatementContext = ctx.blockStatement().statement();
            return blockStatementContext.accept(visitor);
        }
    }
}
