package com.kubadziworski.domain.node.expression.trycatch;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.BlockExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeResolver;
import org.apache.commons.collections4.ListUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class TryCatchExpression implements Expression {

    private final BlockExpression statement;
    private final List<CatchBlock> catchBlocks;
    private final Block finallyBlock;

    public TryCatchExpression(BlockExpression statement, List<CatchBlock> catchBlocks, Block finallyBlock) {
        this.statement = statement;
        this.catchBlocks = catchBlocks;
        this.finallyBlock = finallyBlock;
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
    public Type getType() {
        List<Type> allTypes =
                ListUtils.sum(Collections.singletonList(statement.getType()),
                        catchBlocks.stream().map(CatchBlock::getType).collect(Collectors.toList()));
        return TypeResolver.getCommonType(allTypes);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
