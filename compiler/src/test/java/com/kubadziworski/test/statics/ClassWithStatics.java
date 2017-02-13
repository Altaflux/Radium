package com.kubadziworski.test.statics;


public class ClassWithStatics {

    public static void staticMethod(String myString) {
        System.out.println("Called staticMethod");
    }

    public void nonStaticMethod(String myString) {
        System.out.println("Called nonStaticMethod");
    }
}
