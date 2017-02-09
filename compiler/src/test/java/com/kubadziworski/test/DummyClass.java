package com.kubadziworski.test;

import radium.annotations.NotNull;
import radium.internal.InlineOnly;


public class DummyClass {

    @NotNull
    public Integer objectInt() {
        return 5;
    }

    public String myString(@NotNull String myString) {
        return myString;
    }

    @InlineOnly
    public static void fooooo(String myString) {
        System.out.println(myString);
    }

    //@InlineOnly
    public void faaaaaa(String myString) {
        System.out.println(this);
        System.out.println(myString);
    }
}
