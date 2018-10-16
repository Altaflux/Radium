package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeResolver;

import java.util.Arrays;

public class ElvisExpression extends ElementImpl implements Expression {

    private final Expression leftExpression;
    private final Expression rightExpression;

    public ElvisExpression(Expression leftExpression, Expression rightExpression) {
        this(null, leftExpression, rightExpression);
    }

    public ElvisExpression(NodeData nodeData, Expression leftExpression, Expression rightExpression) {
        super(nodeData);
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @Override
    public Type getType() {
       return TypeResolver.getCommonType(Arrays.asList(rightExpression.getType(), leftExpression.getType()));
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }
}
