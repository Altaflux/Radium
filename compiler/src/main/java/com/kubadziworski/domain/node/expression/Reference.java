package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;

/**
 * Created by kuba on 13.05.16.
 */
public interface Reference extends Expression {

    String getName();

    @Override
    void accept(StatementGenerator generator);

}
