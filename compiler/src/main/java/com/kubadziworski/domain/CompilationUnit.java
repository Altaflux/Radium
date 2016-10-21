package com.kubadziworski.domain;

/**
 * Created by kuba on 28.03.16.
 */
public class CompilationUnit {

    private final ClassDeclaration classDeclaration;
    private final String filePath;

    public CompilationUnit(ClassDeclaration classDeclaration, String filePath) {
        this.classDeclaration = classDeclaration;
        this.filePath = filePath;
    }

    public ClassDeclaration getClassDeclaration() {
        return classDeclaration;
    }

    public String getClassName() {
        return classDeclaration.getName();
    }

    public String getFilePath() {
        return filePath;
    }
}
