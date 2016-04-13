package com.kubadziworski.domain.statement;

import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.expression.ConditionalExpression;
import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.scope.Scope;
import com.sun.corba.se.spi.orbutil.fsm.State;

import java.util.List;

/**
 * Created by kuba on 12.04.16.
 */
public class IfStatement implements Statement {


    private final Expression condition;
    private final Statement trueStatement;
    private final Statement falseStatement;

    public IfStatement(Expression condition, Statement trueStatement, Statement falseStatement) {

        this.condition = condition;
        this.trueStatement = trueStatement;
        this.falseStatement = falseStatement;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getTrueStatement() {
        return trueStatement;
    }

    public Statement getFalseStatement() {
        return falseStatement;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
