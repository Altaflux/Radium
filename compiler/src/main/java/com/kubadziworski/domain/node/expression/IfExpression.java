package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.ComparisonBetweenDiferentTypesException;

import java.util.Optional;


public class IfExpression extends ElementImpl implements Expression {

    private final Expression condition;
    private final Expression trueStatement;
    private final Expression falseStatement;

    public IfExpression(Expression condition, Expression trueStatement, Expression falseStatement) {
        this(null, condition, trueStatement, falseStatement);
    }

    public IfExpression(NodeData element , Expression condition, Expression trueStatement, Expression falseStatement) {
        super(element);
        this.condition = condition;
        this.trueStatement = trueStatement;
        this.falseStatement = falseStatement;

        if (!falseStatement.getType().equals(trueStatement.getType())) {
            throw new RuntimeException("True and false expressions do not match type: " +
                    trueStatement.getType() + " : " + falseStatement.getType());
        }
    }

    @Override
    public Type getType() {
        Optional<Type> type = trueStatement.getType().nearestDenominator(falseStatement.getType());
        if (type.isPresent()) {
            return type.get();
        }
        if (trueStatement.getType().isPrimitive() == falseStatement.getType().isPrimitive()) {
            throw new ComparisonBetweenDiferentTypesException(trueStatement, falseStatement);
        }
        if (trueStatement.getType().isPrimitive() && falseStatement.getType().isPrimitive()) {
            throw new ComparisonBetweenDiferentTypesException(trueStatement, falseStatement);
        }
        return ClassTypeFactory.createClassType("radium.Any");
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    @Override
    public boolean isReturnComplete() {
        return (falseStatement.isReturnComplete() && trueStatement.isReturnComplete());
    }
    public Expression getCondition() {
        return condition;
    }

    public Expression getTrueStatement() {
        return trueStatement;
    }

    public Expression getFalseStatement() {
        return falseStatement;
    }
}
