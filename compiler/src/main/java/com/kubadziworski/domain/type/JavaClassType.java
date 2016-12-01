package com.kubadziworski.domain.type;

import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.util.ReflectionObjectToSignatureMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class JavaClassType implements Type {

    private final String name;

    //private static Map<Type, LinkedMap<Class, Class[]>> cachedInheritance = new HashMap<>();

    public JavaClassType(String name) {
        this.name = name;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getTypeClass() {
        try {
            return Class.forName(name, false, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Type> getSuperType() {
        return Optional.ofNullable(getTypeClass().getSuperclass())
                .map(aClass -> new JavaClassType(aClass.getName()));
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
        if (type.getDescriptor().equals(this.getDescriptor())) {
            return arity;
        }
        Class iteratedClass = getTypeClass();
        while (iteratedClass != null) {
            if (org.objectweb.asm.Type.getDescriptor(iteratedClass).equals(type.getDescriptor())) {
                return arity;
            } else {
                for (Class inter : iteratedClass.getInterfaces()) {
                    if (org.objectweb.asm.Type.getDescriptor(inter).equals(type.getDescriptor())) {
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
        if (type.getDescriptor().equals(this.getDescriptor())) {
            return Optional.of(type);
        }

        Class iteratedClass = getTypeClass();
        while (iteratedClass != null) {
            if (org.objectweb.asm.Type.getDescriptor(iteratedClass).equals(type.getDescriptor())) {
                return Optional.of(new JavaClassType(iteratedClass.getName()));
            }
            iteratedClass = iteratedClass.getSuperclass();
        }
        return Optional.empty();
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
                .map(ReflectionObjectToSignatureMapper::fromMethod)
                .collect(Collectors.toList());
    }

    @Override
    public String getDescriptor() {
        return "L" + getInternalName() + ";";
    }

    @Override
    public String getInternalName() {
        return name.replace(".", "/");
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public org.objectweb.asm.Type getAsmType() {
        return org.objectweb.asm.Type.getType(getDescriptor());
    }

    @Override
    public Nullability isNullable() {
        return Nullability.NOT_NULL;
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
