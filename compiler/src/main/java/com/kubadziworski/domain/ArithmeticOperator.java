package com.kubadziworski.domain;


import java.util.Arrays;

public enum ArithmeticOperator {

    INCREMENT("++"), DECREMENT("--");

    private final String operator;


    ArithmeticOperator(String operator) {
        this.operator = operator;
    }


    public static ArithmeticOperator fromString(String sign) {
        return Arrays.stream(values()).filter(opSign -> opSign.operator.equals(sign))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sign not implemented"));
    }
}
