package com.kubadziworski.test;

import radium.annotations.NotNull;


public class DummyClass {

    @NotNull
    public Integer objectInt(){
        return 5;
    }

    public String myString(@NotNull String myString) {
        return myString;
    }
}
