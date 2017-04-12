package com.kubadziworski.domain.types;

import com.kubadziworski.domain.types.builder.MemberBuilder;
import com.kubadziworski.domain.types.builder.ModifierTransformer;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by plozano on 4/10/2017.
 */
public class ReflectionTypeBuilder {


    GenericType toJvmType(Class aClass) {

        if (aClass.isSynthetic() || aClass.isAnonymousClass()) {
            throw new IllegalStateException("Cannot create type for anonymous or synthetic classes");
        }

        GenericTypeImpl.GenericTypeImplBuilder builder = GenericTypeImpl.builder();
        builder.packageName(aClass.getPackage().getName())
                .simpleName(aClass.getSimpleName())
                .superTypes(getSuperTypes(aClass))
                .functionBuilder(fromMethods(aClass))
                .constructorBuilder(fromConstructors(aClass))
                .typeParameters(setTypeParameters(aClass))
                .fieldBuilder(Stream.of(aClass.getDeclaredFields()).map(this::fromField).collect(Collectors.toList()))
                .modifiers(ModifierTransformer.transformJvm(aClass.getModifiers()));

        return builder.build();
    }

    MemberBuilder<RField, RType> fromField(Field field) {
        return owner -> {
            String name = field.getName();
            TypeReference typeReference = createTypeReference(field.getGenericType());
            Modifiers modifiers = ModifierTransformer.transformJvm(field.getModifiers());
            return new RField(name, owner, typeReference, modifiers);

        };
    }

    List<MemberBuilder<RFunctionSignature, RType>> fromMethods(Class clazz) {
        return Stream.of(clazz.getDeclaredMethods())
                .filter(method -> !method.isSynthetic()).map(this::fromMethod).collect(Collectors.toList());
    }

    MemberBuilder<RFunctionSignature, RType> fromMethod(Method method) {
        String name = method.getName();
        Modifiers modifiers = ModifierTransformer.transformJvm(method.getModifiers());

        Type returnType;
        try {
            returnType = method.getGenericReturnType();
        } catch (GenericSignatureFormatError | MalformedParameterizedTypeException error) {
            returnType = method.getReturnType();
        }
        TypeReference returnTypeReference = createTypeReference(returnType);
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        Type[] genericParameterTypes;
        try {
            genericParameterTypes = method.getGenericParameterTypes();
        } catch (GenericSignatureFormatError | MalformedParameterizedTypeException error) {
            genericParameterTypes = method.getParameterTypes();
        }


        List<RParameter> rParameters = new ArrayList<>();
        for (int x = 0; x < parameters.length; x++) {
            String paramName = parameters[x].getName();
            TypeReference typeReference = createTypeReference(genericParameterTypes[x]);
            RParameter rParameter = new RParameter(paramName, typeReference, null);
            rParameters.add(rParameter);
        }

        return owner -> new RFunctionSignature(name, rParameters, returnTypeReference, modifiers, owner);
    }

    List<MemberBuilder<RFunctionSignature, RType>> fromConstructors(Class clazz) {
        return Stream.of(clazz.getConstructors())
                .filter(method -> !method.isSynthetic()).map(this::fromConstructor).collect(Collectors.toList());
    }

    MemberBuilder<RFunctionSignature, RType> fromConstructor(Constructor method) {
        String name = method.getName();
        Modifiers modifiers = ModifierTransformer.transformJvm(method.getModifiers());

        TypeReference returnTypeReference = voidReference();

        java.lang.reflect.Parameter[] parameters = method.getParameters();

        Type[] genericParameterTypes;
        try {
            genericParameterTypes = method.getGenericParameterTypes();
        } catch (GenericSignatureFormatError | MalformedParameterizedTypeException error) {
            genericParameterTypes = method.getParameterTypes();
        }


        List<RParameter> rParameters = new ArrayList<>();
        for (int x = 0; x < parameters.length; x++) {
            String paramName = parameters[x].getName();
            TypeReference typeReference = createTypeReference(genericParameterTypes[x]);
            RParameter rParameter = new RParameter(paramName, typeReference, null);
            rParameters.add(rParameter);
        }

        return owner -> new RFunctionSignature(name, rParameters, returnTypeReference, modifiers, owner);
    }


    List<TypeParameter> setTypeParameters(Class clazz) {
        List<TypeParameter> jvmTypeParameters = new ArrayList<>();
        try {
            TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
            if (typeParameters.length != 0) {
                for (TypeVariable<?> variable : typeParameters) {
                    jvmTypeParameters.add(createTypeParameter(variable));
                }
            }
        } catch (GenericSignatureFormatError | MalformedParameterizedTypeException error) {

        }
        return jvmTypeParameters;
    }

    protected TypeParameter createTypeParameter(TypeVariable<?> variable) {

        List<MemberBuilder<Constraint, TypeParameter>> constraints = new ArrayList<>();
        Type[] bounds = variable.getBounds();
        if (bounds.length != 0) {
            for (Type bound : variable.getBounds()) {
                constraints.add(owner -> new UpperBoundConstraintImpl(createTypeReference(bound), owner));
            }
        }
        return new TypeParameterImpl(variable.getName(), constraints);
    }

    List<TypeReference> getSuperTypes(Class clazz) {
        List<TypeReference> typeReferences = new ArrayList<>();

        Type superclass;
        try {
            superclass = clazz.getGenericSuperclass();
        } catch (GenericSignatureFormatError | MalformedParameterizedTypeException error) {
            superclass = clazz.getSuperclass();
        }
        typeReferences.add(createTypeReference(superclass));


        Type[] interfaces;
        try {
            interfaces = clazz.getGenericInterfaces();
        } catch (GenericSignatureFormatError | MalformedParameterizedTypeException error) {
            interfaces = clazz.getInterfaces();
        }
        for (Type type : interfaces) {
            typeReferences.add(createTypeReference(type));
        }

        if (typeReferences.isEmpty() && Object.class != clazz) {
            typeReferences.add(createTypeReference(Object.class));
        }

        return typeReferences;
    }

    //TODO
    TypeReference voidReference() {
        return null;
    }

    //TODO
    TypeReference createTypeReference(Type type) {
        if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            Type componentType = arrayType.getGenericComponentType();
            return createArrayTypeReference(componentType);
        }else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type ownerType = parameterizedType.getOwnerType();
            if (ownerType instanceof ParameterizedType) {
                TypeReference ownerTypeReference = createTypeReference(ownerType);

                if (ownerTypeReference instanceof ParameterizedTypeReference) {

                }
            }
        }
        return null;
    }

    protected TypeReference createArrayTypeReference(Type componentType) {
        TypeReference componentTypeReference = createTypeReference(componentType);
        GenericArrayTypeReferenceImpl result = new GenericArrayTypeReferenceImpl(componentTypeReference);
        return result;
    }
}
