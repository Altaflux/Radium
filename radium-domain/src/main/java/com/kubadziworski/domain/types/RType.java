package com.kubadziworski.domain.types;


public interface RType {

    String getQualifiedName();

    String getSimpleName();

    default String getIdentifier() {
        return "$";
    }
}

