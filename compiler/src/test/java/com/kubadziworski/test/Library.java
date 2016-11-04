package com.kubadziworski.test;

public class Library {

    public static String statField = "Aloha";

    public static void execute(String hola) {
        System.out.println("EXECUTED!! " + hola);
    }

    public static void thrower() {
        throw new RuntimeException();
    }


}
