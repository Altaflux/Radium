package com.kubadziworski.domain.type;

import com.kubadziworski.bytecodegeneration.inline.CodeInliner;
import com.kubadziworski.bytecodegeneration.inline.JvmCodeInliner;
import com.kubadziworski.configuration.CompilerConfigInstance;
import com.kubadziworski.configuration.JvmConfiguration;
import com.kubadziworski.domain.ClassMetadata;
import com.kubadziworski.domain.MetaDataBuilder;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.exception.CompilationException;
import com.kubadziworski.util.ReflectionObjectToSignatureMapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import radium.internal.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JavaClassType implements Type {

    private final String name;
    private final Class aClass;
    private final org.objectweb.asm.Type asmType;
    private final Metadata metadata;

    private static final Map<JavaClassType, ClassMetadata> typeClassMetadataMap = new HashMap<>();
    private static final Map<JavaClassType, ClassNodeContainer> classNodeCache = new HashMap<>();

    public JavaClassType(Class clazz) {
        aClass = clazz;
        name = clazz.getCanonicalName();
        asmType = org.objectweb.asm.Type.getType(clazz);
        metadata = (Metadata) aClass.getAnnotation(Metadata.class);
    }

    @Override
    public String getName() {
        return name;
    }


    public Class<?> getTypeClass() {
        return aClass;
    }

    @Override
    public Optional<Type> getSuperType() {
        return Optional.ofNullable(getTypeClass().getSuperclass())
                .map(JavaClassType::new);
    }

    @Override
    public List<Field> getFields() {
        Class iteratedClass = getTypeClass();
        List<java.lang.reflect.Field> result = new ArrayList<>();
        while (iteratedClass != null) {
            Collections.addAll(result, iteratedClass.getDeclaredFields());
            iteratedClass = iteratedClass.getSuperclass();
        }
        return result.stream()
                .map(field -> ReflectionObjectToSignatureMapper.fromField(field, this))
                .collect(Collectors.toList());
    }

//    @Override
//    public int inheritsFrom(Type type) {
//        int arity = 0;
//        if (type.getAsmType().getDescriptor().equals(this.getAsmType().getDescriptor())) {
//            return arity;
//        }
//        Class iteratedClass = getTypeClass();
//        while (iteratedClass != null) {
//            if (org.objectweb.asm.Type.getDescriptor(iteratedClass).equals(type.getAsmType().getDescriptor())) {
//                return arity;
//            } else {
//                for (Class inter : iteratedClass.getInterfaces()) {
//                    if (org.objectweb.asm.Type.getDescriptor(inter).equals(type.getAsmType().getDescriptor())) {
//                        return arity;
//                    }
//                }
//            }
//            iteratedClass = iteratedClass.getSuperclass();
//            arity++;
//        }
//        return -1;
//    }

    @Override
    public int inheritsFrom(Type type) {
        int arity = 0;
        if (type.getAsmType().getDescriptor().equals(this.getAsmType().getDescriptor())) {
            return arity;
        }
        Class iteratedClass = getTypeClass();
        while (iteratedClass != null) {
            if (org.objectweb.asm.Type.getDescriptor(iteratedClass).equals(type.getAsmType().getDescriptor())) {
                return arity;
            } else {
                for (Class inter : iteratedClass.getInterfaces()) {
                    if (org.objectweb.asm.Type.getDescriptor(inter).equals(type.getAsmType().getDescriptor())) {
                        return arity;
                        //TODO this is not fully correct but it is enough for now
                    } else if (ClassTypeFactory.createClassType(inter).inheritsFrom(type) > -1) {
                        return arity;
                    }
                }
            }
            iteratedClass = iteratedClass.getSuperclass();
            arity++;
        }
        return -1;
    }

    //Possible cache the type inheritance if this starts taking too much time

//    public int inheritsFrom(Type type) {
//        int arity = 0;
//        if (type.getDescriptor().equals(this.getDescriptor())) {
//            return arity;
//        }
//
//        LinkedMap<Class,Class[]> classLinkedMap = cachedInheritance.get(this);
//        if(classLinkedMap == null){
//            createInheretanceTree();
//            classLinkedMap = cachedInheritance.get(this);
//        }
//
//        Class iteratedClass = classLinkedMap.firstKey();
//        while (iteratedClass != null) {
//            if (org.objectweb.asm.Type.getDescriptor(iteratedClass).equals(type.getDescriptor())) {
//                return arity;
//            } else {
//                for (Class inter : classLinkedMap.get(iteratedClass)) {
//                    if (org.objectweb.asm.Type.getDescriptor(inter).equals(type.getDescriptor())) {
//                        return arity;
//                    }
//                }
//            }
//            iteratedClass = classLinkedMap.nextKey(iteratedClass);
//            arity++;
//        }
//        return -1;
//    }

//    private void createInheretanceTree(){
//        LinkedMap<Class,Class[]> classLinkedMap = new LinkedMap<>();
//
//        Class iteratedClass = getTypeClass();
//        while (iteratedClass != null) {
//            Class[] interfaces = iteratedClass.getInterfaces();
//            classLinkedMap.put(iteratedClass, interfaces);
//            iteratedClass = iteratedClass.getSuperclass();
//        }
//        cachedInheritance.put(this, classLinkedMap);
//    }

    @Override
    public Optional<Type> nearestDenominator(Type type) {
        if (type.getAsmType().getDescriptor().equals(this.getAsmType().getDescriptor())) {
            return Optional.of(type);
        }

        Class iteratedClass = getTypeClass();
        while (iteratedClass != null) {
            if (org.objectweb.asm.Type.getDescriptor(iteratedClass).equals(type.getAsmType().getDescriptor())) {
                return Optional.of(new JavaClassType(iteratedClass));
            }
            iteratedClass = iteratedClass.getSuperclass();
        }
        return Optional.empty();
    }


    @Override
    public List<FunctionSignature> getConstructorSignatures() {
        Class iteratedClass = getTypeClass();
        List<Constructor> result = Arrays.asList(iteratedClass.getConstructors());
        return result.stream()
                .map(constructor -> ReflectionObjectToSignatureMapper.fromConstructor(constructor, this))
                .collect(Collectors.toList());
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        Class iteratedClass = getTypeClass();
        List<Method> result = new ArrayList<>();
        while (iteratedClass != null) {
            Collections.addAll(result, iteratedClass.getDeclaredMethods());
            iteratedClass = iteratedClass.getSuperclass();
        }
        return result.stream()
                .map(method -> ReflectionObjectToSignatureMapper.fromMethod(method, new JavaClassType(method.getDeclaringClass())))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public org.objectweb.asm.Type getAsmType() {
        return asmType;
    }

    @Override
    public Nullability isNullable() {
        return Nullability.NOT_NULL;
    }

    public ClassNode getClassNode(boolean skipCode) {
        return createClassNode(skipCode);
    }

    public List<Type> getInterfaces() {
        return Stream.of(aClass.getInterfaces()).map(ClassTypeFactory::createClassType).collect(Collectors.toList());
    }

    public ClassType getClassType() {
        return aClass.isInterface() ? ClassType.INTERFACE : ClassType.CLASS;
    }

    public Optional<ClassMetadata> classMetadata() {
        if (typeClassMetadataMap.containsKey(this)) {
            return Optional.ofNullable(typeClassMetadataMap.get(this));
        }

        if (metadata != null) {
            MetaDataBuilder metaDataBuilder = new MetaDataBuilder();
            ClassMetadata classMetadata = metaDataBuilder.fromString(metadata.data());
            typeClassMetadataMap.put(this, classMetadata);
            return Optional.of(classMetadata);
        } else {
            typeClassMetadataMap.put(this, null);
        }
        return Optional.empty();
    }


    private ClassNode createClassNode(boolean skipCode) {
        if (classNodeCache.containsKey(this)) {
            ClassNodeContainer container = classNodeCache.get(this);
            if (skipCode) {
                return container.classNode;
            } else if (!container.skipCode) {
                return container.classNode;
            }
        }
        ClassNode classNode = new ClassNode(Opcodes.ASM5);
        try {

            InputStream stream = null;
            JvmConfiguration configuration = CompilerConfigInstance.getConfig();
            if (configuration.getClassLoader() != null) {
                stream = configuration.getClassLoader().getResourceAsStream(this.getTypeClass().getName().replace(".", "/") + ".class");
            }
            if (stream == null) {
                stream = ClassLoader.getSystemResourceAsStream(this.getTypeClass().getName().replace(".", "/") + ".class");
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
            throw new CompilationException("Could not parse class: " + this.getTypeClass().getName(), e);
        }
        classNodeCache.put(this, new ClassNodeContainer(classNode, skipCode));
        return classNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof TypeProjection) {
            o = ((TypeProjection) o).getInternalType();
        }
        return o instanceof Type && getName().equals(((Type) o).getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public CodeInliner getInliner() {
        return JvmCodeInliner.INSTANCE;
    }

    @Override
    public String toString() {
        return "JavaClassType{" +
                "name='" + name + '\'' +
                '}';
    }

    private static class ClassNodeContainer {
        private final ClassNode classNode;
        private final boolean skipCode;

        private ClassNodeContainer(ClassNode classNode, boolean skipCode) {
            this.classNode = classNode;
            this.skipCode = skipCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassNodeContainer container = (ClassNodeContainer) o;

            return skipCode == container.skipCode && classNode.equals(container.classNode);
        }

        @Override
        public int hashCode() {
            int result = classNode.hashCode();
            result = 31 * result + (skipCode ? 1 : 0);
            return result;
        }
    }
}
