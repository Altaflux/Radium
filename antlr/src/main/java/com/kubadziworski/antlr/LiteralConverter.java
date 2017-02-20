package com.kubadziworski.antlr;

public class LiteralConverter {

    public static ValueHolder floatingPointLiteralFromString(String text) {
        String val = text.replace("_", "");
        boolean toDouble = val.endsWith("D");
        boolean toFloat = val.endsWith("F");
        if (toDouble) {
            return ValueHolder.of(ValueHolder.ValueType.DOUBLE, Double.parseDouble(val.substring(0, val.length() - 1)));
        } else if (toFloat) {
            return ValueHolder.of(ValueHolder.ValueType.FLOAT, Float.parseFloat(val.substring(0, val.length() - 1)));
        } else {
            return ValueHolder.of(ValueHolder.ValueType.DOUBLE, Double.parseDouble(val));
        }
    }

    public static ValueHolder decimalIntegerLiteral(String text) {
        String val = text.replace("_", "");
        boolean toLong = val.endsWith("L");
        if (toLong) {
            return ValueHolder.of(ValueHolder.ValueType.LONG, Long.parseLong(val.substring(0, val.length() - 1)));
        } else {
            try {
                return ValueHolder.of(ValueHolder.ValueType.INT, Integer.parseInt(val));
            } catch (NumberFormatException e) {
                return ValueHolder.of(ValueHolder.ValueType.LONG, Long.parseLong(val));
            }
        }
    }

    public static ValueHolder hexIntegerLiteral(String text) {
        String val = text.replace("_", "");
        boolean toLong = val.endsWith("L");
        if (toLong) {
            return ValueHolder.of(ValueHolder.ValueType.LONG, Long.decode(val.substring(0, val.length() - 1)));
        } else {
            try {
                return ValueHolder.of(ValueHolder.ValueType.INT, Integer.decode(val));
            } catch (NumberFormatException e) {
                return ValueHolder.of(ValueHolder.ValueType.LONG, Long.decode(val));
            }
        }
    }

    public static ValueHolder octalIntegerLiteral(String text) {
        String val = text.replace("_", "");
        boolean toLong = val.endsWith("L");
        if (toLong) {
            return ValueHolder.of(ValueHolder.ValueType.LONG, Long.parseLong(val.substring(0, val.length() - 1), 8));
        } else {
            try {
                return ValueHolder.of(ValueHolder.ValueType.INT, Integer.parseInt(val, 8));
            } catch (NumberFormatException e) {
                return ValueHolder.of(ValueHolder.ValueType.LONG, Long.parseLong(val, 8));
            }
        }
    }

    public static ValueHolder binaryIntegerLiteral(String text) {
        String val = text.replace("_", "").substring(2);
        boolean toLong = val.endsWith("L");
        if (toLong) {
            return ValueHolder.of(ValueHolder.ValueType.LONG, Long.parseLong(val.substring(0, val.length() - 1), 2));
        } else {
            try {
                return ValueHolder.of(ValueHolder.ValueType.INT, Integer.parseInt(val, 2));
            } catch (NumberFormatException e) {
                return ValueHolder.of(ValueHolder.ValueType.LONG, Long.parseLong(val, 2));
            }
        }
    }
}
