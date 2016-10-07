package com.kubadziworski.domain;


import java.util.Arrays;

public enum ArithmeticOperator {

    INCREMENT("++", "1"), DECREMENT("--", "-1");

    private final String operator;
    private final String incremental;

    ArithmeticOperator(String operator, String incremental) {
        this.operator = operator;
        this.incremental = incremental;
    }

    public String getIncremental() {
        return incremental;
    }

    public static ArithmeticOperator fromString(String sign) {
        return Arrays.stream(values()).filter(opSign -> opSign.operator.equals(sign))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sign not implemented"));
    }
}
