package com.kubadziworski.domain.statement;

import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.expression.Expression;
import com.sun.corba.se.spi.orbutil.fsm.State;
import org.apache.commons.math3.analysis.function.Exp;

/**
 * Created by kuba on 11.04.16.
 */
public class ReturnStatement implements Statement {

    private Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public Expression getExpression() {
        return expression;
    }
}
