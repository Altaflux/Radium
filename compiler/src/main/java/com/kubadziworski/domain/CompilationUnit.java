package com.kubadziworski.domain;

/**
 * Created by kuba on 28.03.16.
 */
public class CompilationUnit {

    private final ClassDeclaration classDeclaration;

    public CompilationUnit(ClassDeclaration classDeclaration) {
        this.classDeclaration = classDeclaration;
    }

    public ClassDeclaration getClassDeclaration() {
        return classDeclaration;
    }

    public String getClassName() {
        return classDeclaration.getName();
    }
}
