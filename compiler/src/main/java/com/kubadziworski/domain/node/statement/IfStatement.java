package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Expression;

import java.util.Optional;


public class IfStatement implements Statement {


    private final Expression condition;
    private final Statement trueStatement;
    private final Statement falseStatement;

    public IfStatement(Expression condition, Statement trueStatement, Statement falseStatement) {
        this.condition = condition;
        this.trueStatement = trueStatement;
        this.falseStatement = falseStatement;
    }

    public IfStatement(Expression condition, Statement trueStatement) {
        this.condition = condition;
        this.trueStatement = trueStatement;
        this.falseStatement = null;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getTrueStatement() {
        return trueStatement;
    }

    public Optional<Statement> getFalseStatement() {
        return Optional.ofNullable(falseStatement);
    }

    @Override
    public boolean isReturnComplete() {
        return falseStatement != null && (falseStatement.isReturnComplete() && trueStatement.isReturnComplete());
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
