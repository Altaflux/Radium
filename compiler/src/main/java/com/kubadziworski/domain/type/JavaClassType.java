package com.kubadziworski.domain.type;

import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.util.ReflectionObjectToSignatureMapper;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class JavaClassType implements Type {

    private final String name;

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
            return Class.forName(name);
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
        if(type.getDescriptor().equals(this.getDescriptor())){
            return arity;
        }
        Class iteratedClass = getTypeClass();
        while (iteratedClass != null) {
            if (org.objectweb.asm.Type.getDescriptor(iteratedClass).equals(type.getDescriptor())) {
                return arity;
            }
            iteratedClass = iteratedClass.getSuperclass();
            arity++;
        }
        return -1;
    }

    @Override
    public Optional<Type> nearestDenominator(Type type) {
        if(type.getDescriptor().equals(this.getDescriptor())){
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
    public int getLoadVariableOpcode() {
        return Opcodes.ALOAD;
    }

    @Override
    public int getStoreVariableOpcode() {
        return Opcodes.ASTORE;
    }

    @Override
    public int getDupCode() {
        return Opcodes.DUP;
    }

    @Override
    public int getDupX1Code() {
        return Opcodes.DUP_X1;
    }

    @Override
    public int getReturnOpcode() {
        return Opcodes.ARETURN;
    }

    @Override
    public int getAddOpcode() {
        throw new RuntimeException("Addition operation not (yet ;) ) supported for custom objects");
    }

    @Override
    public int getSubstractOpcode() {
        throw new RuntimeException("Subtraction operation not (yet ;) ) supported for custom objects");
    }

    @Override
    public int getMultiplyOpcode() {
        throw new RuntimeException("Multiplcation operation not (yet ;) ) supported for custom objects");
    }

    @Override
    public int getDividOpcode() {
        throw new RuntimeException("Division operation not (yet ;) ) supported for custom objects");
    }

    @Override
    public int getNegation() {
        throw new RuntimeException("Negation operation not (yet ;) ) supported for custom objects");
    }

    @Override
    public int getStackSize() {
        return 1;
    }

    @Override
    public boolean isPrimitive(){
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaClassType that = (JavaClassType) o;

        return name.equals(that.name);

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
