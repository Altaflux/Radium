package com.kubadziworski.parsing;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.Constructor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.exception.MissingReturnStatementException;
import com.kubadziworski.parsing.visitor.statement.StatementVisitor;

import java.util.Collections;


public class FunctionGenerator {

    private final FunctionScope scope;

    public FunctionGenerator(FunctionScope scope) {
        this.scope = scope;
    }

    public Function generateFunction(FunctionSignature signature, EnkelParser.FunctionContentContext ctx, boolean isConstructor) {
        addParametersAsLocalVariables(signature);
        Statement statement = getBlock(ctx);
        return generateFunction(signature, statement, isConstructor);
    }

    public Function generateFunction(FunctionSignature signature, Statement statement, boolean isConstructor) {


        if (statement instanceof Block) {
            return generateFunction(signature, (Block) statement, isConstructor);
        }
        if (!signature.getReturnType().equals(VoidType.INSTANCE) && statement instanceof Expression) {
            ReturnStatement returnStatement = new ReturnStatement((Expression) statement);
            return generateFunction(signature, new Block(scope, Collections.singletonList(returnStatement)), isConstructor);
        }
        return generateFunction(signature, new Block(scope, Collections.singletonList(statement)), isConstructor);
    }

    public Function generateFunction(FunctionSignature signature, EnkelParser.BlockContext ctx, boolean isConstructor) {
        addParametersAsLocalVariables(signature);
        return generateFunction(signature, getBlock(ctx), isConstructor);
    }

    private Function generateFunction(FunctionSignature signature, Block block, boolean isConstructor) {
        if (isConstructor) {
            return new Constructor(signature, block);
        }

        verifyBlockReturn(signature, block);
        return new Function(signature, block);
    }


    private void verifyBlockReturn(FunctionSignature signature, Block block) {
        if (!signature.getReturnType().equals(VoidType.INSTANCE)) {

            if (block.getStatements().isEmpty()) {
                throw new RuntimeException("No return specified for method with return type: " + signature.getReturnType());
            }
            Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
            if (lastStatement instanceof ReturnStatement) {

                if (!(((ReturnStatement) lastStatement).getExpression().getType().inheritsFrom(signature.getReturnType()) > -1)) {
                    throw new RuntimeException("The return type of the expression is not the same as the function signature: " + signature.getReturnType());
                }
                return;
            } else {
                if (!block.isReturnComplete()) {
                    throw new MissingReturnStatementException(signature);
                }
            }
        }

        if (!block.getStatements().isEmpty()) {
            Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
            if (lastStatement instanceof ReturnStatement &&
                    (!((ReturnStatement) lastStatement).getExpression().getType().equals(VoidType.INSTANCE)
                            && !((ReturnStatement) lastStatement).getExpression().getType().equals(UnitType.CONCRETE_INSTANCE))) {
                throw new RuntimeException("The return type of the expression is not the same as the function signature: " + signature.getReturnType() + " " +
                        "Expression return type: " + ((ReturnStatement) lastStatement).getExpression().getType());

            }
        }
    }

    public void addParametersAsLocalVariables(FunctionSignature signature) {
        signature.getParameters()
                .forEach(param -> scope.addLocalVariable(new LocalVariable(param.getName(), param.getType(), false, param.isVisible())));
    }

    private Statement getBlock(EnkelParser.FunctionContentContext functionContentContext) {
        StatementVisitor statementVisitor = new StatementVisitor(scope);
        return functionContentContext.accept(statementVisitor);
    }

    private Block getBlock(EnkelParser.BlockContext functionContentContext) {
        StatementVisitor statementVisitor = new StatementVisitor(scope);
        return (Block) functionContentContext.accept(statementVisitor);
    }
}
