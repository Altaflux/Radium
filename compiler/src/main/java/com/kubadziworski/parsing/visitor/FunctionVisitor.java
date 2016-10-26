package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.BlockContext;
import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.domain.Constructor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.node.expression.EmptyExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.parsing.visitor.statement.StatementVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 01.04.16.
 */
public class FunctionVisitor extends EnkelBaseVisitor<Function> {

    private final Scope scope;

    public FunctionVisitor(Scope scope) {
        this.scope = new Scope(scope);
    }

    @Override
    public Function visitFunction(@NotNull FunctionContext ctx) {
        FunctionSignature signature = ctx.functionDeclaration().accept(new FunctionSignatureVisitor(scope));
        //TODO do better
        if (!Modifier.isStatic(signature.getModifiers())) {
            scope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        }
        addParametersAsLocalVariables(signature);

        Block block;
        if (ctx.blockStatement() != null) {
            StatementVisitor visitor = new StatementVisitor(scope);
            EnkelParser.StatementContext blockStatementContext = ctx.blockStatement().statement();
            Statement expression = blockStatementContext.accept(visitor);
            block = new Block(scope, Collections.singletonList(expression));
        } else {
            block = getBlock(ctx);
        }
        if (signature.getName().equals(scope.getFullClassName())) {
            return new Constructor(signature, block);
        }

        block = addAutoReturnStatement(signature, block);
        verifyBlockReturn(signature, block);

        return new Function(signature, block);
    }


    private Block addAutoReturnStatement(FunctionSignature signature, Block incomingBlock) {
        Block block = incomingBlock;
        if (!block.getStatements().isEmpty()) {
            Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
            if (lastStatement instanceof Expression && ((Expression) lastStatement).getType().equals(signature.getReturnType())) {
                List<Statement> statements = new ArrayList<>(block.getStatements().subList(0, block.getStatements().size() - 1));
                ReturnStatement returnStatement = new ReturnStatement((Expression) lastStatement);
                statements.add(returnStatement);
                block = new Block(block.getScope(), statements);

            } else if (!(lastStatement instanceof ReturnStatement)) {
                List<Statement> statements = new ArrayList<>(block.getStatements().subList(0, block.getStatements().size()));
                ReturnStatement returnStatement = new ReturnStatement(new EmptyExpression(BultInType.VOID));
                statements.add(returnStatement);
                block = new Block(block.getScope(), statements);
            }
        }
        return block;
    }

    private void verifyBlockReturn(FunctionSignature signature, Block block) {
        if (!signature.getReturnType().equals(BultInType.VOID)) {

            if (block.getStatements().isEmpty()) {
                throw new RuntimeException("No return specified for method with return type: " + signature.getReturnType());
            }
            Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
            if (lastStatement instanceof ReturnStatement) {
                if (!((ReturnStatement) lastStatement).getExpression().getType().equals(signature.getReturnType())) {
                    throw new RuntimeException("The return type of the expression is not the same as the function signature: " + signature.getReturnType());
                }
                return;
            } else {
                throw new RuntimeException("No return specified for method with return type: " + signature.getReturnType());
            }
        }

        if (!block.getStatements().isEmpty()) {
            Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
            if (lastStatement instanceof ReturnStatement &&
                    ((ReturnStatement) lastStatement).getExpression().getType() != BultInType.VOID) {
                throw new RuntimeException("The return type of the expression is not the same as the function signature: " + signature.getReturnType() + " " +
                        "Expression return type: " + ((ReturnStatement) lastStatement).getExpression().getType());
            }
        }
    }

    private void addParametersAsLocalVariables(FunctionSignature signature) {
        signature.getParameters()
                .forEach(param -> scope.addLocalVariable(new LocalVariable(param.getName(), param.getType())));
    }

    private Block getBlock(FunctionContext functionContext) {
        StatementVisitor statementVisitor = new StatementVisitor(scope);
        BlockContext block = functionContext.block();
        return (Block) block.accept(statementVisitor);
    }
}
