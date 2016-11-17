package com.kubadziworski.domain.node.expression.arthimetic;

import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 10.04.16.
 */
public abstract class ArthimeticExpression extends ElementImpl implements Expression {

    private final Expression leftExpression;
    private final Expression rightExpression;
    private final Type type;

    protected ArthimeticExpression(Expression leftExpression, Expression rightExpression) {
        this(null, leftExpression, rightExpression);
    }

    protected ArthimeticExpression(NodeData nodeData, Expression leftExpression, Expression rightExpression) {
        super(nodeData);
        this.type = getCommonType(leftExpression,rightExpression);
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }


    private static Type getCommonType(Expression leftExpression, Expression rightExpression) {
        if(rightExpression.getType() == DefaultTypes.STRING) return DefaultTypes.STRING;
        return leftExpression.getType();
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    @Override
    public Type getType() {
        return type;
    }
}
