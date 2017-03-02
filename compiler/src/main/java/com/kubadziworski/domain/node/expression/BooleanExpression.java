package com.kubadziworski.domain.node.expression;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.exception.IncompatibleTypesException;
import com.kubadziworski.util.TypeChecker;

public class BooleanExpression extends ElementImpl implements Expression {

    private final Expression leftExpression;
    private final Expression rightExpression;
    private final boolean and;

    public BooleanExpression(NodeData element, Expression leftExpression, Expression rightExpression, boolean and) {
        super(element);
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.and = and;

//        if (!leftExpression.getType().equals(PrimitiveTypes.BOOLEAN_TYPE) ||
//                !rightExpression.equals(PrimitiveTypes.BOOLEAN_TYPE)) {
//            throw new IncompatibleTypesException("boolean operation", leftExpression.getType(),
//                    rightExpression.getType());
//        }
        if (!TypeChecker.isBool(leftExpression.getType()) || !TypeChecker.isBool(rightExpression.getType())) {
            throw new IncompatibleTypesException("boolean operation", leftExpression.getType(),
                    rightExpression.getType());
        }
    }

    public BooleanExpression(Expression leftExpression, Expression rightExpression, boolean and) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.and = and;
    }

    @Override
    public Type getType() {
        return PrimitiveTypes.BOOLEAN_TYPE;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    public boolean isAnd() {
        return and;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
