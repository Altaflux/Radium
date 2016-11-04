package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Parameter;

public class CatchBlock implements Statement {
    private final Block block;
    private final Parameter parameter;

    public CatchBlock(Block block, Parameter parameter) {
        this.block = block;
        this.parameter = parameter;
    }

    public Block getBlock() {
        return block;
    }

    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public void accept(StatementGenerator generator) {

    }
}
