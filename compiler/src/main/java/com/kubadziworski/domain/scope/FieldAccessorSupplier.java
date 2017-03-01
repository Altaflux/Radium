package com.kubadziworski.domain.scope;


import com.kubadziworski.domain.Function;

public interface FieldAccessorSupplier {

    Function get(Field field);
}
