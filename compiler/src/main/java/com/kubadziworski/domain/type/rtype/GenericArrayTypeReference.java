package com.kubadziworski.domain.type.rtype;

/**
 * Created by plozano on 3/29/2017.
 */
public interface GenericArrayTypeReference extends TypeReference {

    TypeReference getComponentType();

    ArrayType getType();
}