package com.kubadziworski.domain.types;

/**
 * Created by pablo.lozano on 4/11/2017.
 */
public abstract class TypeReferenceImpl implements TypeReference {

    @Override
    public String getIdentifier() {
        RType type = getType();
        if (type != null)
            return type.getIdentifier();
        return null;
    }

    @Override
    public String getSimpleName() {
        RType type = getType();
        if (type != null)
            return type.getSimpleName();
        return null;
    }

    @Override
    public String getQualifiedName() {
        RType type = getType();
        if (type != null)
            return type.getQualifiedName();
        return null;
    }

}
