package com.kubadziworski.domain.types;

import com.kubadziworski.domain.types.builder.MemberBuilder;
import com.kubadziworski.domain.types.builder.ModifierTransformer;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        List<MemberBuilder<RParameter, RFunctionSignature>> rParameters = new ArrayList<>();
        for (int x = 0; x < parameters.length; x++) {
            String paramName = parameters[x].getName();
            Type paramType = genericParameterTypes[x];

            rParameters.add(rFunctionSignature -> {
                TypeReference argType;
                if (isLocal(paramType, method)) {
                    argType = createLocalTypeReference(paramType, rFunctionSignature, method);
                } else {
                    argType = createTypeReference(paramType);
                }
                return new RParameter(paramName, argType, null);
            });
        }
        List<TypeParameter> typeParameters = enhanceGenericDeclaration(method);
        return owner -> new RFunctionSignature(name, rParameters, returnTypeReference, modifiers, owner, typeParameters);
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


        List<MemberBuilder<RParameter, RFunctionSignature>> rParameters = new ArrayList<>();
        for (int x = 0; x < parameters.length; x++) {
            String paramName = parameters[x].getName();
            Type paramType = genericParameterTypes[x];

            rParameters.add(rFunctionSignature -> {
                TypeReference argType;
                if (isLocal(paramType, method)) {
                    argType = createLocalTypeReference(paramType, rFunctionSignature, method);
                } else {
                    argType = createTypeReference(paramType);
                }
                return new RParameter(paramName, argType, null);
            });
        }

        List<TypeParameter> typeParameters = enhanceGenericDeclaration(method);
        return owner -> new RFunctionSignature(name, rParameters, returnTypeReference, modifiers, owner, typeParameters);
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


    private TypeReference createTypeReference(Type type) {
        if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            Type componentType = arrayType.getGenericComponentType();
            return createArrayTypeReference(componentType);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type ownerType = parameterizedType.getOwnerType();
            if (ownerType instanceof ParameterizedType) {
                TypeReference ownerTypeReference = createTypeReference(ownerType);

                if (ownerTypeReference instanceof ParameterizedTypeReference) {
                    Pair<RType, List<TypeReference>> pair = enhanceTypeReference(parameterizedType);
                    return new InnerTypeReferenceImpl(pair.getLeft(), pair.getRight(), (ParameterizedTypeReference) ownerTypeReference);
                } else {
                    Pair<RType, List<TypeReference>> pair = enhanceTypeReference(parameterizedType);
                    return new ParameterizedTypeReferenceImpl(pair.getLeft(), pair.getRight());
                }
            } else {
                Pair<RType, List<TypeReference>> pair = enhanceTypeReference(parameterizedType);
                return new ParameterizedTypeReferenceImpl(pair.getLeft(), pair.getRight());
            }
        } else if (type instanceof Class<?> && ((Class<?>) type).isArray()) {
            Class<?> arrayType = (Class<?>) type;
            Type componentType = arrayType.getComponentType();
            return createArrayTypeReference(componentType);
        } else {
            return new ParameterizedTypeReferenceImpl(createProxy(type), Collections.emptyList());
        }
    }

    protected List<TypeParameter> enhanceGenericDeclaration(GenericDeclaration declaration) {
        TypeVariable<?>[] typeParameters = declaration.getTypeParameters();
        List<TypeParameter> jvmTypeParameters = new ArrayList<>();
        for (TypeVariable<?> variable : typeParameters) {
            jvmTypeParameters.add(createTypeParameter(variable));
        }
        return jvmTypeParameters;
    }

    private Pair<RType, List<TypeReference>> enhanceTypeReference(ParameterizedType parameterizedType) {

        RType type = createProxy(parameterizedType.getRawType());
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        List<TypeReference> arguments = new ArrayList<>();
        if (actualTypeArguments.length != 0) {
            for (Type actualTypeArgument : actualTypeArguments) {
                TypeReference argument = createTypeArgument(actualTypeArgument);
                arguments.add(argument);
            }
        }

        return Pair.of(type, arguments);
    }

    TypeReference createTypeArgument(Type actualTypeArgument) {
        if (actualTypeArgument instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) actualTypeArgument;
            List<MemberBuilder<Constraint, ConstraintOwner>> constraints = new ArrayList<>();
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length != 0) {
                for (Type boundType : upperBounds) {
                    TypeReference upperBoundType = createTypeReference(boundType);
                    constraints.add(owner -> new UpperBoundConstraintImpl(upperBoundType, owner));

                }
            }
            Type[] lowerBounds = wildcardType.getUpperBounds();
            if (lowerBounds.length != 0) {
                for (Type boundType : lowerBounds) {
                    TypeReference upperBoundType = createTypeReference(boundType);
                    constraints.add(owner -> new LowerBoundConstraintImpl(upperBoundType, owner));

                }
            }
            return new WildcardTypeReferenceImpl(constraints);
        } else {
            return createTypeReference(actualTypeArgument);

        }
    }

    protected TypeReference createLocalTypeReference(Type type, TypeParameterDeclarator container,
                                                     GenericDeclaration member) {
        if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            Type componentType = arrayType.getGenericComponentType();
            return createLocalArrayTypeReference(componentType, container, member);
        } else if (type instanceof TypeVariable<?>) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            int idx = Arrays.asList(member.getTypeParameters()).indexOf(typeVariable);
            return new ParameterizedTypeReferenceImpl(container.getTypeParameters().get(idx), Collections.emptyList());
        }
        throw new IllegalArgumentException(type.toString());
    }

    protected TypeReference createLocalArrayTypeReference(Type componentType, TypeParameterDeclarator container,
                                                          GenericDeclaration member) {
        TypeReference componentTypeReference = createLocalTypeReference(componentType, container, member);
        GenericArrayTypeReference result = new GenericArrayTypeReferenceImpl(componentTypeReference);
        return result;
    }

    private boolean isLocal(Type parameterType, GenericDeclaration member) {
        if (parameterType instanceof TypeVariable<?>) {
            return member.equals(((TypeVariable<?>) parameterType).getGenericDeclaration());
        } else if (parameterType instanceof GenericArrayType) {
            return isLocal(((GenericArrayType) parameterType).getGenericComponentType(), member);
        }
        return false;
    }

    private RType createProxy(Type type) {
        throw new UnsupportedOperationException("TODO");
    }

    protected TypeReference createArrayTypeReference(Type componentType) {
        TypeReference componentTypeReference = createTypeReference(componentType);
        return new GenericArrayTypeReferenceImpl(componentTypeReference);
    }
}
