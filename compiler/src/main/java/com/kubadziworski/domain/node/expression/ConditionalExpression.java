package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.exception.MixedComparisonNotAllowedException;

/**
 * Created by kuba on 12.04.16.
 */
public class ConditionalExpression extends ElementImpl implements Expression {

    private final CompareSign compareSign;
    private final Expression leftExpression;
    private final Expression rightExpression;
    private final Type type;
    private final boolean isPrimitiveComparison;

    public ConditionalExpression(Expression leftExpression, Expression rightExpression, CompareSign compareSign) {
        this(null, leftExpression, rightExpression, compareSign);
    }

    public ConditionalExpression(NodeData element, Expression leftExpression, Expression rightExpression, CompareSign compareSign) {
        super(element);
        this.type = PrimitiveTypes.BOOLEAN_TYPE;
        this.compareSign = compareSign;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        boolean leftExpressionIsPrimitive = leftExpression.getType().isPrimitive();
        boolean rightExpressionIsPrimitive = rightExpression.getType().isPrimitive();

        if(leftExpressionIsPrimitive && !rightExpressionIsPrimitive){
            throw new MixedComparisonNotAllowedException(leftExpression.getType(), rightExpression.getType());
        }
        isPrimitiveComparison = leftExpressionIsPrimitive;
    }

    public CompareSign getCompareSign() {
        return compareSign;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    public boolean isPrimitiveComparison() {
        return isPrimitiveComparison;
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

