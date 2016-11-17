package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;

/**
 * Created by kuba on 28.03.16.
 */
public class PrintStatement extends ElementImpl implements Statement {

    private final Expression expression;

    public PrintStatement(NodeData element, Expression expression) {
        super(element);
        this.expression = expression;
    }


    public PrintStatement(Expression expression) {
        this(null, expression);
    }

    public Expression getExpression() {
        return expression;
    }


    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
