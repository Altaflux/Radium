package com.kubadziworski.domain;

import com.kubadziworski.domain.type.Type;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Created by kuba on 06.04.16.
 */
public class MetaData {
    private final String className;
    private final String packageName;
    private final Supplier<Type> superClass;
    private final Supplier<List<Type>> interfaces;
    private final String filename;

    public MetaData(String className, String packageName, Supplier<Type> superClass, Supplier<List<Type>> interfaces, String filename) {
        this.className = className;
        this.packageName = packageName;
        this.superClass = memoize(superClass);
        this.interfaces = memoize(interfaces);
        this.filename = filename;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public Type getSuperClass() {
        return superClass.get();
    }

    public List<Type> getInterfaces() {
        return interfaces.get();
    }

    public String getFilename() {
        return filename;
    }


    private static <T> Supplier<T> memoize(Supplier<T> delegate) {
        AtomicReference<T> value = new AtomicReference<>();
        return () -> {
            T val = value.get();
            if (val == null) {
                val = value.updateAndGet(cur -> cur == null ?
                        Objects.requireNonNull(delegate.get()) : cur);
            }
            return val;
        };
    }
}
