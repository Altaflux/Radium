package com.kubadziworski.domain.type.rtype.impl;

import com.kubadziworski.domain.type.rtype.RType;
import com.kubadziworski.domain.type.rtype.TypeReference;

/**
 * Created by plozano on 4/5/2017.
 */
public class TypeReferenceImpl implements TypeReference {

    private final RType type;

    public TypeReferenceImpl(RType type) {
        this.type = type;
    }

    @Override
    public RType getType() {
        return type;
    }

    @Override
    public String getQualifiedName() {
        return type.getQualifiedName();
    }

    @Override
    public String getSimpleName() {
        return type.getSimpleName();
    }
}
