package com.kubadziworski.domain.node.expression.arthimetic;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;

/**
 * Created by kuba on 10.04.16.
 */
public class Addition extends ArthimeticExpression {

    public Addition(Expression leftExpress, Expression rightExpress) {
        this(null, leftExpress, rightExpress);
    }

    public Addition(NodeData nodeData, Expression leftExpress, Expression rightExpress) {
        super(leftExpress, rightExpress);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
