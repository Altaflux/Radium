package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 02.04.16.
 */
public abstract class Expression {
    private Type type;

    public Expression(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public  abstract void accept(ExpressionGenrator genrator);

}
