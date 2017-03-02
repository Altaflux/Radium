package com.kubadziworski.domain;

import java.util.List;

/**
 * Created by kuba on 06.04.16.
 */
public class MetaData {
    private final String className;
    private final String packageName;
    private final String superClass;
    private final List<String> interfaces;
    private final String filename;

    public MetaData(String className, String packageName, String superClass, List<String> interfaces, String filename) {
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

    public String getSuperClass() {
        return superClass;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public String getFilename() {
        return filename;
    }
}
