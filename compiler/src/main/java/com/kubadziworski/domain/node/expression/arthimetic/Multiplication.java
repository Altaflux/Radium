package com.kubadziworski.domain.node.expression.arthimetic;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;

/**
 * Created by kuba on 10.04.16.
 */
public class Multiplication extends ArthimeticExpression {

    public Multiplication(Expression leftExpress, Expression rightExpress) {
        this(null, leftExpress, rightExpress);
    }

    public Multiplication(NodeData nodeData, Expression leftExpress, Expression rightExpress) {
        super(nodeData, leftExpress, rightExpress);
    }


    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
