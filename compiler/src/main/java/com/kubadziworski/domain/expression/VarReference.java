package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 09.04.16.
 */
public class VarReference extends Expression {
    private final String varName;

    public VarReference(String varName,Type type) {
        super(type);
        this.varName = varName;
    }

    public String geName() {
        return varName;
    }

    @Override
    public void accept(ExpressionGenrator genrator) {
        genrator.generate(this);
    }
}
