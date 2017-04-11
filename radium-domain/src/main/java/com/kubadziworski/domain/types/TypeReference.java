package com.kubadziworski.domain.types;

/**
 * Created by plozano on 3/29/2017.
 */
public interface TypeReference {

    RType getType();

    String getQualifiedName();


    String getSimpleName();
}
