package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;


public class ThrowStatement extends ElementImpl implements Statement {
    private final Expression expression;

    public ThrowStatement(NodeData context, Expression expression) {
        super(context);
        this.expression = expression;
    }

    public ThrowStatement(Expression expression) {
        this(null, expression);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public boolean isReturnComplete() {
        return true;
    }
}
