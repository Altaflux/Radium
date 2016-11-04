package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.bytecodegeneration.statement.BlockStatementGenerator;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.TryCatchStatement;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class TryCatchStatementVisitor extends EnkelBaseVisitor<TryCatchStatement> {

    private final StatementVisitor statementVisitor;
    private final Scope scope;

    public TryCatchStatementVisitor(StatementVisitor statementVisitor, Scope scope) {
        this.statementVisitor = statementVisitor;
        this.scope = scope;
    }

    public TryCatchStatement visitTryStatement(@NotNull EnkelParser.TryStatementContext ctx) {
        Block block = (Block) ctx.block().accept(statementVisitor);

        Block finallyBlock = null;
        if (!ctx.finallyBlock().isEmpty()) {
            finallyBlock = (Block) ctx.finallyBlock().get(0).accept(statementVisitor);
            finallyBlock.getScope().addLocalVariable(new LocalVariable("$$", new JavaClassType("java.lang.Throwable"), false));
        }
        List<TryCatchStatement.CatchBlock> catchBlocks = ctx.catchBlock().stream()
                .map(this::processCatchBlock).collect(Collectors.toList());
        return new TryCatchStatement(block, catchBlocks, finallyBlock);
    }

    private TryCatchStatement.CatchBlock processCatchBlock(EnkelParser.CatchBlockContext context) {

        Scope newScope = new Scope(scope);
        String varName = context.name().getText();
        Type varType = TypeResolver.getFromTypeContext(context.type(), scope);

        Parameter parameter = new Parameter(varName, varType, null);
        newScope.addLocalVariable(new LocalVariable(varName, varType, true));

        BlockStatementVisitor statementGenerator = new BlockStatementVisitor(newScope);
        Block block = context.block().accept(statementGenerator);

        return new TryCatchStatement.CatchBlock(block, parameter);
    }

}
