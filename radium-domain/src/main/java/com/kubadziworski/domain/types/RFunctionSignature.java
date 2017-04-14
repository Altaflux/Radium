package com.kubadziworski.domain.types;

import com.kubadziworski.domain.types.builder.MemberBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by plozano on 4/10/2017.
 */
public class RFunctionSignature implements TypeParameterDeclarator {

    private final String name;
    private final List<RParameter> parameters;
    private final TypeReference returnType;
    private final Modifiers modifiers;
    private final RType owner;
    private final List<TypeParameter> typeParameters;

    public RFunctionSignature(String name, List<MemberBuilder<RParameter, RFunctionSignature>> parameters,
                              TypeReference returnType,
                              Modifiers modifiers,
                              RType owner, List<TypeParameter> typeParameters) {
        this.name = name;
        this.parameters = parameters.stream()
                .map(rParameterRFunctionSignatureMemberBuilder -> rParameterRFunctionSignatureMemberBuilder
                        .build(this))
                .collect(Collectors.toList());
        this.returnType = returnType;
        this.modifiers = modifiers;
        this.owner = owner;
        this.typeParameters = typeParameters;
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

    @Override
    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }
}
