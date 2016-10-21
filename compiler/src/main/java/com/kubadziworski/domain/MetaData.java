package com.kubadziworski.domain;

/**
 * Created by kuba on 06.04.16.
 */
public class MetaData {
    private final String className;
    private final String packageName;

    public MetaData(String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }
}
