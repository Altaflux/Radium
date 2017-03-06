package com.kubadziworski.domain;

import com.kubadziworski.domain.type.Type;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by kuba on 06.04.16.
 */
public class MetaData {
    private final String className;
    private final String packageName;
    private final Supplier<Type> superClass;
    private final List<Supplier<Type>> interfaces;
    private final String filename;

    public MetaData(String className, String packageName, Supplier<Type> superClass, List<Supplier<Type>> interfaces, String filename) {
        this.className = className;
        this.packageName = packageName;
        this.superClass = superClass;
        this.interfaces = interfaces;
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
        return interfaces.stream().map(Supplier::get).collect(Collectors.toList());
    }

    public String getFilename() {
        return filename;
    }
}
