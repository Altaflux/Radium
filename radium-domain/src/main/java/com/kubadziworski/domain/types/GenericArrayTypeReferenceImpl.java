package com.kubadziworski.domain.types;


public class GenericArrayTypeReferenceImpl implements GenericArrayTypeReference {

    private final TypeReference typeReference;

    public GenericArrayTypeReferenceImpl(TypeReference typeReference) {
        this.typeReference = typeReference;
    }

    @Override
    public String getQualifiedName() {
        return typeReference.getQualifiedName() + "[]";
    }

    @Override
    public String getIdentifier() {
        TypeReference componentType = getComponentType();
        if (componentType != null)
            return componentType.getIdentifier() + "[]";
        return null;
    }

    @Override
    public String getSimpleName() {
        return typeReference.getSimpleName() + "[]";
    }

    @Override
    public TypeReference getComponentType() {
        return typeReference;
    }

    @Override
    public ArrayType getType() {
        TypeReference componentTypeReference = getComponentType();
        if (componentTypeReference != null) {
            RType componentType = componentTypeReference.getType().unwrap();
            if (componentType instanceof ComponentType) {
                return ((ComponentType) componentType).getArrayType();
            }
        }
        return null;
    }

    @Override
    public int getDimensions() {
        ArrayType arrayType = getType();
        if (arrayType != null)
            return arrayType.getDimensions();
        return -1;
    }
}
