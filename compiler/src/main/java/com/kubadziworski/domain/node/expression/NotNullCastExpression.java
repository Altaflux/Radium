package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;


public class NotNullCastExpression extends ElementImpl implements Expression {
    private final Expression expression;
    private final Type returnType;

    public NotNullCastExpression(Expression expression) {
        this(null, expression);
    }

    public NotNullCastExpression(NodeData nodeData, Expression expression) {
        super(nodeData);
        this.expression = expression;
        this.returnType = new TypeProjection(expression.getType(), Type.Nullability.NOT_NULL);
    }

    @Override
    public Type getType() {
        return returnType;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
