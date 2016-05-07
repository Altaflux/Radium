package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.expression.Call;
import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.expression.FunctionCall;
import com.kubadziworski.domain.expression.VarReference;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.Type;
import org.apache.commons.math3.analysis.function.Exp;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by kuba on 05.05.16.
 */
public class SuperCall implements Call {
    public static final String SUPER_IDETIFIER = "super";
    private List<Expression> arguments;

    public SuperCall() {
        this(Collections.emptyList());
    }

    public SuperCall(List<Expression> arguments) {
        this.arguments = arguments;
    }

    @Override
    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public String getIdentifier() {
        return SUPER_IDETIFIER;
    }

    @Override
    public Type getType() {
        return BultInType.VOID;
    }

    @Override
    public void accept(ExpressionGenrator genrator) {
        genrator.generate(this);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
