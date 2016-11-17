package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;


public class IfStatement extends ElementImpl implements Statement {

    private final Expression condition;
    private final Statement trueStatement;

    public IfStatement(NodeData element, Expression condition, Statement trueStatement) {
        super(element);
        this.condition = condition;
        this.trueStatement = trueStatement;
    }

    public IfStatement(Expression condition, Statement trueStatement) {
        this(null, condition, trueStatement);
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getTrueStatement() {
        return trueStatement;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
