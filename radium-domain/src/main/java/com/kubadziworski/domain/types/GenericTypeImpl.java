package com.kubadziworski.domain.types;

import com.kubadziworski.domain.types.builder.MemberBuilder;
import lombok.Builder;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.stream.Collectors;


public class GenericTypeImpl extends DeclaredTypeImpl implements GenericType {

    private final List<RField> rFields;
    private final List<RFunctionSignature> rFunctionSignatures;
    private final List<RFunctionSignature> rConstructorSignatures;
    private final List<TypeParameter> typeParameters;

    @Builder
    public GenericTypeImpl(String simpleName, String packageName, List<TypeReference> superTypes,
                           Modifiers modifiers, List<MemberBuilder<RField, RType>> fieldBuilder,
                           List<MemberBuilder<RFunctionSignature, RType>> functionBuilder, List<MemberBuilder<RFunctionSignature, RType>> constructorBuilder,
                           List<TypeParameter> typeParameters) {
        super(simpleName, packageName, modifiers, superTypes);
        this.typeParameters = typeParameters;
        this.rFields = fieldBuilder.stream().map(rFieldMemberBuilder -> rFieldMemberBuilder.build(this))
                .collect(Collectors.toList());
        this.rConstructorSignatures = constructorBuilder.stream().map(rFieldMemberBuilder -> rFieldMemberBuilder.build(this))
                .collect(Collectors.toList());
        this.rFunctionSignatures = functionBuilder.stream().map(rFieldMemberBuilder -> rFieldMemberBuilder.build(this))
                .collect(Collectors.toList());
    }

    @Override
    public List<RField> getFields() {
        return rFields;
    }

    @Override
    public List<RFunctionSignature> getFunctionSignatures() {
        return rFunctionSignatures;
    }

    @Override
    public List<RFunctionSignature> getConstructorSignatures() {
        return rConstructorSignatures;
    }

    @Override
    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    @Override
    public ArrayType getArrayType() {
        //TODO
        throw new NotImplementedException("TODO");
    }
}
