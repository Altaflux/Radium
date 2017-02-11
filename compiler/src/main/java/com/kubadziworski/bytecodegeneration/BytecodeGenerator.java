package com.kubadziworski.bytecodegeneration;

import com.kubadziworski.domain.CompilationUnit;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kuba on 01.04.16.
 */
public class BytecodeGenerator {
    public List<GeneratedClassHolder> generate(CompilationUnit compilationUnit) {
        return compilationUnit.getClassDeclaration().stream()
                .map(classDeclaration -> {
                    ClassGenerator classGenerator = new ClassGenerator();

                    return new GeneratedClassHolder(classGenerator.generate(classDeclaration).toByteArray(), classDeclaration.getName());
                }).collect(Collectors.toList());
    }

    public static class GeneratedClassHolder {
        private final byte[] bytes;
        private final String name;

        GeneratedClassHolder(byte[] bytes, String name) {
            this.bytes = bytes;
            this.name = name;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public String getName() {
            return name;
        }
    }
}
