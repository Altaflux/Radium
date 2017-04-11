package com.kubadziworski.domain.types;

import java.util.Collections;
import java.util.List;

/**
 * Created by plozano on 4/10/2017.
 */
public class RFunctionSignature {

    private final String name;
    private final List<RParameter> parameters;
    private final TypeReference returnType;
    private final Modifiers modifiers;
    private final RType owner;

    public RFunctionSignature(String name, List<RParameter> parameters, TypeReference returnType, Modifiers modifiers, RType owner) {
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
                .orElseThrow(() -> new RuntimeException(name + " Not Found"));
    }

    public TypeReference getReturnType() {
        return returnType;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public RType getOwner() {
        return owner;
    }
}
