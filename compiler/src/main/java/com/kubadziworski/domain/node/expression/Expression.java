package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 02.04.16.
 */
public interface Expression extends Statement {
    Type getType();
    void accept(ExpressionGenerator genrator);
    @Override
    void accept(StatementGenerator generator);
}
