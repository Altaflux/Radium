package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 02.04.16.
 */
public interface Expression extends Statement {
    Type getType();
    void accept(ExpressionGenrator genrator);
    void accept(StatementGenerator generator);
}
