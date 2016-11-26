package com.kubadziworski.domain.type;


public interface BoxableType {

    Type getBoxedType();

    Type getUnBoxedType();

    boolean isBoxed();

}
