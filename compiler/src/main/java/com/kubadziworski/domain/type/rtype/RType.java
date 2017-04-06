package com.kubadziworski.domain.type.rtype;


public interface RType {

    String getQualifiedName();

    String getSimpleName();

    default String getIdentifier() {
        return "$";
    }
}

