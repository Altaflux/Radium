package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.ComparisonBetweenDiferentTypesException;

import java.util.Optional;

/**
 * Created by plozano on 10/30/2016.
 */
public class IfExpression implements Expression {

    private final Expression condition;
    private final Expression trueStatement;
    private final Expression falseStatement;

    public IfExpression(Expression condition, Expression trueStatement, Expression falseStatement) {
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
        if(type.isPresent()){
            return type.get();
        }
        if(trueStatement.getType() instanceof BuiltInType || falseStatement.getType() instanceof BuiltInType){
            throw new ComparisonBetweenDiferentTypesException(trueStatement, falseStatement);
        }
        return new JavaClassType("java.lang.Object");
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
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
