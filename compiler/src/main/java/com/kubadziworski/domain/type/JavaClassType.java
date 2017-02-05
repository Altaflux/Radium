package com.kubadziworski.domain.type;

import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.exception.CompilationException;
import com.kubadziworski.util.Lazy;
import com.kubadziworski.util.ReflectionObjectToSignatureMapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class JavaClassType implements Type {

    private final String name;
    private final Class aClass;
    private final org.objectweb.asm.Type asmType;
    //private static Map<Type, LinkedMap<Class, Class[]>> cachedInheritance = new HashMap<>();

    private volatile Supplier<ClassNode> classNodeSupplier;
    private static final Map<JavaClassType, ClassNode> classNodeCache = new HashMap<>();

    public JavaClassType(Class clazz) {
        aClass = clazz;
        name = clazz.getCanonicalName();
        asmType = org.objectweb.asm.Type.getType(clazz);
        classNodeSupplier = Lazy.lazily(() -> classNodeSupplier = Lazy.value(createClassNode()));
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

    public ClassNode getClassNode() {
        return classNodeSupplier.get();
    }

    private ClassNode createClassNode() {
        if (classNodeCache.containsKey(this)) {
            return classNodeCache.get(this);
        }
        ClassNode classNode = new ClassNode(Opcodes.ASM5);
        try {
            ClassReader classVisitor = new ClassReader(this.getTypeClass().getName());
            classVisitor.accept(classNode, ClassReader.SKIP_CODE + ClassReader.SKIP_DEBUG + ClassReader.SKIP_FRAMES);
        } catch (IOException e) {
            throw new CompilationException("Could not parse class: " + this.getTypeClass().getName(), e);
        }
        classNodeCache.put(this, classNode);
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

    @Override
    public String toString() {
        return "JavaClassType{" +
                "name='" + name + '\'' +
                '}';
    }
}
