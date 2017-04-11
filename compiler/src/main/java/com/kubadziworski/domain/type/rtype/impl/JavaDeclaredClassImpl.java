package com.kubadziworski.domain.type.rtype.impl;

import com.kubadziworski.configuration.CompilerConfigInstance;
import com.kubadziworski.configuration.JvmConfiguration;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.scope.RField;
import com.kubadziworski.domain.scope.RFunctionSignature;
import com.kubadziworski.domain.type.rtype.JavaDeclaredClass;
import com.kubadziworski.domain.type.rtype.TypeReference;
import com.kubadziworski.exception.CompilationException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class JavaDeclaredClassImpl extends DeclaredTypeImpl implements JavaDeclaredClass {
    private final Class clazz;

    public JavaDeclaredClassImpl(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public List<TypeReference> getSuperTypes() {
        return null;
    }

    @Override
    public Modifiers getModifiers() {
        return Modifiers.empty();
    }

    @Override
    public String getPackageName() {
        return clazz.getPackage().getName();
    }

    @Override
    public List<RField> getFields() {
        Class iteratedClass = clazz;
        List<java.lang.reflect.Field> result = new ArrayList<>();
        while (iteratedClass != null) {
            Collections.addAll(result, iteratedClass.getDeclaredFields());
            iteratedClass = iteratedClass.getSuperclass();
        }
//        return result.stream()
//                .map(field -> RReflectionObjectToSignatureMapper.fromField(field, this))
//                .collect(Collectors.toList());
        return null;
    }

    @Override
    public List<RFunctionSignature> getFunctionSignatures() {
        return null;
    }

    @Override
    public List<RFunctionSignature> getConstructorSignatures() {
        return null;
    }

    @Override
    public String getQualifiedName() {
        return clazz.getName();
    }

    @Override
    public String getSimpleName() {
        return clazz.getSimpleName();
    }

    @Override
    public ClassNode getClassNode(boolean skipCode) {
        return createClassNode(skipCode);
    }

    private ClassNode createClassNode(boolean skipCode){


        ClassNode classNode = new ClassNode(Opcodes.ASM5);
        try {

            InputStream stream = null;
            JvmConfiguration configuration = CompilerConfigInstance.getConfig();
            if (configuration.getClassLoader() != null) {
                stream = configuration.getClassLoader().getResourceAsStream(clazz.getName().replace(".", "/") + ".class");
            }
            if (stream == null) {
                stream = ClassLoader.getSystemResourceAsStream(clazz.getName().replace(".", "/") + ".class");
            }
            ClassReader classVisitor = new ClassReader(stream);
            try {
                stream.close();
            } catch (Exception e) {
                //
            }
            if (skipCode) {
                classVisitor.accept(classNode, ClassReader.SKIP_CODE + ClassReader.SKIP_DEBUG + ClassReader.SKIP_FRAMES);
            } else {
                classVisitor.accept(classNode, ClassReader.EXPAND_FRAMES);
            }

        } catch (IOException e) {
            throw new CompilationException("Could not parse class: " + clazz.getName(), e);
        }

        return classNode;

    }
}
