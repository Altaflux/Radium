package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.type.RadiumBuiltIns;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.UnitType;


public class BlockExpression extends Block implements Expression {
    private final Type type;
    private final Block block;

    public BlockExpression(Block block) {
        this(null, block);
    }

    public BlockExpression(NodeData nodeData, Block block) {
        super(nodeData, block.getScope(), block.getStatements());
        this.block = block;

        if (block.getStatements().isEmpty()) {
            type = UnitType.INSTANCE;
        } else {
            Statement statement = block.getStatements().get(block.getStatements().size() - 1);
            if (super.isReturnComplete()) {
                type = RadiumBuiltIns.NOTHING_TYPE;
            } else {
                if (statement instanceof Expression) {
                    type = ((Expression) statement).getType();
                } else {
                    type = UnitType.INSTANCE;
                }
            }
        }
    }


    public Block getStatementBlock() {
        return block;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
