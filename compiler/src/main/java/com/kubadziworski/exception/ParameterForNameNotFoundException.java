package com.kubadziworski.exception;

import com.kubadziworski.domain.expression.FunctionParameter;

import java.util.List;

/**
 * Created by kuba on 17.04.16.
 */
public class ParameterForNameNotFoundException extends RuntimeException {
    public ParameterForNameNotFoundException(String name, List<FunctionParameter> parameters) {
    }
}
