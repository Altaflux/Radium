package com.kubadziworski.domain.types;


public interface RType {

    RType unwrap();

    String getQualifiedName();

    String getSimpleName();

    default String getIdentifier() {
        return "$";
    }
}

