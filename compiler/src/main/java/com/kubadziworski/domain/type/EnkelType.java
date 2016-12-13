package com.kubadziworski.domain.type;

import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EnkelType implements Type {

    private final String name;
    private final Scope scope;

    public EnkelType(String name, Scope scope) {
        this.name = name;
        this.scope = scope;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Field> getFields() {
        List<Field> fields = new ArrayList<>();
        fields.addAll(scope.getFields().values());
        fields.addAll(getSuperType().map(Type::getFields).orElse(Collections.emptyList()));
        return fields;
    }

    @Override
    public Optional<Type> getSuperType() {
        return Optional.ofNullable(scope.getSuperClassName())
                .map(s -> ClassTypeFactory.createClassType(scope.getSuperClassName()));

    }

    @Override
    public List<FunctionSignature> getConstructorSignatures() {
        return scope.getConstructorSignatures();
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        List<FunctionSignature> signatures = new ArrayList<>();
        signatures.addAll(scope.getFunctionSignatures());
        signatures.addAll(getSuperType().map(Type::getFunctionSignatures).orElse(Collections.emptyList()));
        return signatures;
    }

    public Optional<Scope> getScope() {
        return Optional.ofNullable(scope);
    }

    @Override
    public int inheritsFrom(Type type) {


        int arity = 0;
        if (type.getName().equals(this.getName())) {
            return arity;
        }

        Type type1 = this;
        while (type1 != null) {
            if (type1.getName().equals(type.getName())) {
                return arity;
            }
            type1 = type1.getSuperType().orElse(null);
            arity++;
        }
        return -1;
    }

    @Override
    public Optional<Type> nearestDenominator(Type type) {
        if (type.getName().equals(this.getName())) {
            return Optional.of(type);
        }

        Type type1 = this;
        while (type1 != null) {
            if (type1.getName().equals(type.getName())) {
                return Optional.of(type1);
            }
            type1 = type1.getSuperType().orElse(null);
        }
        return Optional.empty();
    }

    @Override
    public Class<?> getTypeClass() {
        throw new RuntimeException("Enkel class " + name + " do not have a clazz instance");
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
    public boolean isPrimitive(){
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

        return o instanceof Type && getName().equals(((Type) o).getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "EnkelType{" +
                "name='" + name + '\'' +
                '}';
    }
}
