package com.kubadziworski.domain;

import java.util.List;

/**
 * Created by kuba on 28.03.16.
 */
public class CompilationUnit {

    private final List<ClassDeclaration> classDeclaration;
    private final String filePath;

    public CompilationUnit(List<ClassDeclaration> classDeclaration, String filePath) {
        this.classDeclaration = classDeclaration;
        this.filePath = filePath;
    }

    public List<ClassDeclaration> getClassDeclaration() {
        return classDeclaration;
    }

//    public String getClassName() {
//        return classDeclaration.getName();
//    }

    public String getFilePath() {
        return filePath;
    }
}

/**

 x.filePath= "";
 x.setFilePath("")
 var foo = x.getFilePath
 **/