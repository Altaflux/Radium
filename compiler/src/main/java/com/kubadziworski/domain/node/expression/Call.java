package com.kubadziworski.domain.node.expression;

import com.kubadziworski.domain.scope.FunctionSignature;

import java.util.List;

/**
 * Created by kuba on 05.05.16.
 */
public interface Call extends Expression {
    List<Argument> getArguments();

    String getIdentifier();

    int getInvokeOpcode();

    FunctionSignature getFunctionSignature();
}
