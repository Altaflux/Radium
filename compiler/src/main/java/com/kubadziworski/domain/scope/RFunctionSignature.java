package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.RParameter;
import com.kubadziworski.domain.type.rtype.TypeReference;
import com.kubadziworski.exception.ParameterForNameNotFoundException;

import java.util.Collections;
import java.util.List;

/**
 * Created by plozano on 4/5/2017.
 */
public class RFunctionSignature {

    private final String name;
    private final List<RParameter> parameters;
    private final TypeReference returnType;
    private final Modifiers modifiers;
    private final TypeReference owner;

    public RFunctionSignature(String name, List<RParameter> parameters, TypeReference returnType, Modifiers modifiers, TypeReference owner) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.modifiers = modifiers;
        this.owner = owner;
    }


    public String getName() {
        return name;
    }

    public List<RParameter> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public RParameter getParameterForName(String name) {
        return parameters.stream()
                .filter(param -> param.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ParameterForNameNotFoundException(name, parameters, null));
    }

    public TypeReference getReturnType() {
        return returnType;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public TypeReference getOwner() {
        return owner;
    }
}
