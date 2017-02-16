package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeResolver;

import java.util.Arrays;
import java.util.List;


public class IfExpression extends ElementImpl implements Expression {

    private final Expression condition;
    private final Expression trueStatement;
    private final Expression falseStatement;

    public IfExpression(Expression condition, Expression trueStatement, Expression falseStatement) {
        this(null, condition, trueStatement, falseStatement);
    }

    public IfExpression(NodeData element, Expression condition, Expression trueStatement, Expression falseStatement) {
        super(element);
        this.condition = condition;
        this.trueStatement = trueStatement;
        this.falseStatement = falseStatement;

    }

    @Override
    public Type getType() {
        List<Type> allTypes = Arrays.asList(trueStatement.getType(), falseStatement.getType());
        return TypeResolver.getCommonType(allTypes);
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
