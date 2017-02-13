package com.kubadziworski.test.coercion;


import radium.annotations.NotNull;

public class TypeCoercionTest {

    @NotNull
    public Integer objectInt() {
        return 5;
    }

    public String returnString(@NotNull String myString) {
        return myString;
    }
}
