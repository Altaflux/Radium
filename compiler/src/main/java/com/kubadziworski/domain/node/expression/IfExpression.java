package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.type.Type;

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
        //TODO WHEN DOING SUBCLASSES THIS NEEDS TO CHANGE TO LOWEST COMMON DENOMINATOR
        return trueStatement.getType();
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
