package com.kubadziworski.bytecodegeneration;

import com.kubadziworski.domain.ClassDeclaration;
import com.kubadziworski.domain.CompilationUnit;

/**
 * Created by kuba on 01.04.16.
 */
public class BytecodeGenerator {
    public byte[] generate(CompilationUnit compilationUnit) {
        ClassDeclaration classDeclaration = compilationUnit.getClassDeclaration();
        ClassGenerator classGenerator = new ClassGenerator();
        return classGenerator.generate(classDeclaration).toByteArray();
    }
}
