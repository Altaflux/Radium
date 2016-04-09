package com.kubadziworski.antlr.domain.expression;

import com.kubadziworski.antlr.domain.type.Type;
import org.springframework.expression.Expression;

/**
 * Created by kuba on 09.04.16.
 */
public class VarReference extends com.kubadziworski.antlr.domain.expression.Expression {
    private final String varName;

    public VarReference(String varName,Type type) {
        super(type);
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }
}
