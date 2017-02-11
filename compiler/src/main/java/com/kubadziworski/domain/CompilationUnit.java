package com.kubadziworski.domain;

import java.util.List;

/**
 * Created by kuba on 28.03.16.
 */
public class CompilationUnit {

    private final List<ClassDeclaration> classDeclaration;
    private final String filePath;
    private final String classPackage;

    public CompilationUnit(List<ClassDeclaration> classDeclaration, String filePath, String classPackage) {
        this.classDeclaration = classDeclaration;
        this.filePath = filePath;
        this.classPackage = classPackage;
    }

    public List<ClassDeclaration> getClassDeclaration() {
        return classDeclaration;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getClassPackage() {
        return classPackage;
    }
}
