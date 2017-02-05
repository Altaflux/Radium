package com.kubadziworski.util;

import java.util.function.Supplier;

public interface Lazy<T> extends Supplier<T> {

    Supplier<T> init();

    default T get() {
        return init().get();
    }

    static <U> Supplier<U> lazily(Lazy<U> lazy) {
        return lazy;
    }

    static <T> Supplier<T> value(T value) {
        return () -> value;
    }
}
