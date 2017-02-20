package com.kubadziworski.antlr;


public class ValueHolder {

    public final ValueType type;
    public final Object value;

    private ValueHolder(ValueType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static ValueHolder of(ValueType type, Object value) {
        return new ValueHolder(type, value);
    }

    public enum ValueType {
        CHAR(Character.class),
        STRING(String.class),
        INT(Integer.class),
        LONG(Long.class),
        DOUBLE(Double.class),
        FLOAT(Float.class),
        BOOLEAN(Boolean.class);

        public Class clazz;

        ValueType(Class clazz) {
            this.clazz = clazz;
        }
    }
}
