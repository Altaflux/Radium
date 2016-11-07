package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.type.Type;


public class BlockExpression extends Block implements Expression {
    private final Type type;

    public BlockExpression(Block block) {
        super(block.getScope(), block.getStatements());
        Statement statement = block.getStatements().get(block.getStatements().size() - 1);
        if (statement instanceof Expression) {
            type = ((Expression) statement).getType();
        } else {
            throw new RuntimeException("Last statement is not an expression");
        }
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
