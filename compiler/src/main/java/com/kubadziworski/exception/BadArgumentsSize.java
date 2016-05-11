package com.kubadziworski.exception;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.ExpressionContext;
import com.kubadziworski.domain.scope.FunctionSignature;

import java.util.List;

/**
 * Created by kuba on 06.04.16.
 */
public class BadArgumentsSize extends RuntimeException {
    public BadArgumentsSize(FunctionSignature function, List<ExpressionContext> calledParameters) {
        super("Bad arguments amount");
    }
}
