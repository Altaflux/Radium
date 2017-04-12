package com.kubadziworski.domain.types;

import java.util.List;


public class ParameterizedTypeReferenceImpl extends TypeReferenceImpl implements ParameterizedTypeReference {

    protected final RType rType;
    protected final List<TypeReference> arguments;

    public ParameterizedTypeReferenceImpl(RType rType, List<TypeReference> arguments) {
        this.rType = rType;
        this.arguments = arguments;
    }

    public RType getType() {
        return rType;
    }

    public List<TypeReference> getArguments() {
        return arguments;
    }
}
