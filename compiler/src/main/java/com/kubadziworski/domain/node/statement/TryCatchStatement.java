package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Parameter;

import java.util.List;
import java.util.Optional;

public class TryCatchStatement implements Statement {

    private final Statement statement;
    private final List<CatchBlock> catchBlocks;
    private final Block finallyBlock;

    public TryCatchStatement(Statement statement, List<CatchBlock> catchBlocks, Block finallyBlock) {
        this.statement = statement;
        this.catchBlocks = catchBlocks;
        this.finallyBlock = finallyBlock;
    }

    public Statement getStatement() {
        return statement;
    }

    public List<CatchBlock> getCatchBlocks() {
        return catchBlocks;
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
        if (!statement.isReturnComplete()) {
            return false;
        }
        if (catchBlocks.isEmpty() && !statement.isReturnComplete()) {
            return false;
        }

        int result = catchBlocks.stream().mapToInt(catchBlock -> {
            if (catchBlock.isReturnComplete()) {
                return 1;
            }
            return 0;
        }).min().orElse(0);
        return result == 1;
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
