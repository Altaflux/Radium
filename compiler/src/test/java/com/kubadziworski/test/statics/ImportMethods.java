package com.kubadziworski.test.statics;


public class ImportMethods {

    public static String statField = "Aloha";

    public static void execute(String value) {
        System.out.println("EXECUTED!! " + value);
    }

    public static void thrower() {
        throw new RuntimeException();
    }

}
