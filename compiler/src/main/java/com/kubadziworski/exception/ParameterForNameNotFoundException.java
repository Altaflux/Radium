package com.kubadziworski.exception;

import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.expression.RParameter;

import java.util.List;

/**
 * Created by kuba on 17.04.16.
 */
public class ParameterForNameNotFoundException extends RuntimeException {
    public ParameterForNameNotFoundException(String name, List<Parameter> parameters) {
    }

    public ParameterForNameNotFoundException(String name, List<RParameter> parameters, String foo) {
    }
}
