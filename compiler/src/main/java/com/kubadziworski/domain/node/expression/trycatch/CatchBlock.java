package com.kubadziworski.domain.node.expression.trycatch;


import com.kubadziworski.domain.node.expression.BlockExpression;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.type.Type;

public class CatchBlock {

    private final BlockExpression block;
    private final Parameter parameter;

    public CatchBlock(BlockExpression block, Parameter parameter) {
        this.block = block;
        this.parameter = parameter;
    }

    public boolean isReturnComplete() {
        return block.isReturnComplete();
    }

    public Type getType() {
        return block.getType();
    }

    public BlockExpression getBlock() {
        return block;
    }

    public Parameter getParameter() {
        return parameter;
    }
}
