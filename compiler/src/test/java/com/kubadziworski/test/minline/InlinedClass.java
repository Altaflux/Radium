package com.kubadziworski.test.minline;

import radium.internal.InlineOnly;


public class InlinedClass {

    @InlineOnly
    public static void inlinedStatic(String myString) {
        System.out.println(myString);
    }

    @InlineOnly
    public void inlinedNonStatic(String myString) {
        System.out.println(this);
        System.out.println(myString);
    }
}
