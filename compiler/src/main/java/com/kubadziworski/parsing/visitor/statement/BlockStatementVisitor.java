package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.BlockContext;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.Statement;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class BlockStatementVisitor extends EnkelBaseVisitor<Block>{
    private final Scope scope;

    public BlockStatementVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Block visitBlock(@NotNull BlockContext ctx) {
        List<EnkelParser.BlockStatementContext> blockStatementsCtx = ctx.blockStatement();
        Scope newScope = new Scope(scope);
        StatementVisitor statementVisitor = new StatementVisitor(newScope);
        List<Statement> statements = blockStatementsCtx.stream()
                .map(smtt -> smtt.statement().accept(statementVisitor))
                .collect(Collectors.toList());
        return new Block(newScope, statements);
    }
}