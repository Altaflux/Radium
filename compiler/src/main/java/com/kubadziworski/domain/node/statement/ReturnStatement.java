package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;

/**
 * Created by kuba on 11.04.16.
 */
public class ReturnStatement extends ElementImpl implements Statement {

    private final Expression expression;

    public ReturnStatement(NodeData ctx, Expression expression) {
        super(ctx);
        this.expression = expression;
    }

    public ReturnStatement(Expression expression) {
       this(null, expression);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    @Override
    public boolean isReturnComplete() {
        return true;
    }

    public Expression getExpression() {
        return expression;
    }
}
