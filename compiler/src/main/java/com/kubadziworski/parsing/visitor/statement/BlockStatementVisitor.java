package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.BlockContext;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.exception.UnreachableStatementException;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockStatementVisitor extends EnkelBaseVisitor<Block> {
    private final Scope scope;

    public BlockStatementVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Block visitBlock(@NotNull BlockContext ctx) {
        List<EnkelParser.BlockStatementContext> blockStatementsCtx = ctx.blockStatement();
        Scope newScope = new Scope(scope);
        StatementVisitor statementVisitor = new StatementVisitor(newScope);

        List<Statement> statements = new ArrayList<>();
        boolean hasReturnCompleted = false;
        for (EnkelParser.BlockStatementContext statementContext : blockStatementsCtx) {
            if (hasReturnCompleted) {
                throw new UnreachableStatementException(statementContext.start.getLine());
            }

            Statement statement = statementContext.statement().accept(statementVisitor);
            statements.add(statementContext.statement().accept(statementVisitor));
            hasReturnCompleted = statement.isReturnComplete();
        }
        return new Block(new RuleContextElementImpl(ctx), newScope, statements);
    }
}