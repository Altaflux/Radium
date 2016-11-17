package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Parameter;

import java.util.Optional;

public class TryCatchStatement extends ElementImpl implements Statement {

    private final Statement statement;
    private final Block finallyBlock;

    public TryCatchStatement(NodeData context, Statement statement, Block finallyBlock) {
        super(context);
        this.statement = statement;
        this.finallyBlock = finallyBlock;
    }

    public TryCatchStatement(Statement statement, Block finallyBlock) {
        this(null, statement, finallyBlock);
    }

    public Statement getStatement() {
        return statement;
    }

    public Optional<Block> getFinallyBlock() {
        return Optional.ofNullable(finallyBlock);
    }

    @Override
    public boolean isReturnComplete() {
        if (finallyBlock != null) {
            if (finallyBlock.isReturnComplete()) {
                return true;
            }
        }
        return statement.isReturnComplete();

    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public static class CatchBlock {
        private final Block block;
        private final Parameter parameter;

        public CatchBlock(Block block, Parameter parameter) {
            this.block = block;
            this.parameter = parameter;
        }

        public boolean isReturnComplete() {
            return block.isReturnComplete();
        }

        public Block getBlock() {
            return block;
        }

        public Parameter getParameter() {
            return parameter;
        }

    }
}
