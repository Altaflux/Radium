package com.kubadziworski.domain.node.expression.arthimetic;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.type.Type;


public class PureArithmeticExpression extends ArthimeticExpression {

    private final Type returnType;
    private final ArithmeticOperator operator;

    public PureArithmeticExpression(Expression leftExpress, Expression rightExpress, Type returnType, ArithmeticOperator operator) {
        this(null, leftExpress, rightExpress, returnType, operator);
    }

    public PureArithmeticExpression(NodeData nodeData, Expression leftExpress, Expression rightExpress, Type returnType, ArithmeticOperator operator) {
        super(nodeData, leftExpress, rightExpress);
        this.returnType = returnType;
        this.operator = operator;
    }

    @Override
    public Type getType() {
        return returnType;
    }

    public ArithmeticOperator getOperator() {
        return operator;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
