package com.kubadziworski.util;

import com.kubadziworski.bytecodegeneration.util.ModifierTransformer;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.expression.RParameter;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.RFunctionSignature;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.domain.type.rtype.impl.JavaDeclaredClass;
import com.kubadziworski.domain.type.rtype.impl.TypeReferenceImpl;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import radium.annotations.NotNull;
import radium.annotations.Nullable;
import radium.internal.InlineOnly;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by plozano on 4/5/2017.
 */
public class RReflectionObjectToSignatureMapper {

    private static final String NOT_NULL_DESCRIPTOR = org.objectweb.asm.Type.getDescriptor(NotNull.class);
    private static final String NULLABLE_DESCRIPTOR = org.objectweb.asm.Type.getDescriptor(Nullable.class);
    private static final String INLINE_DESCRIPTOR = org.objectweb.asm.Type.getDescriptor(InlineOnly.class);

    public static FunctionSignature fromMethod(Method method, JavaDeclaredClass javaClassType) {
        String name = method.getName();
        List<Parameter> parameters = getParams(method.getParameters(), method.getName(), org.objectweb.asm.Type.getMethodDescriptor(method), javaClassType);
        Class<?> returnType = method.getReturnType();
        Type owner = ClassTypeFactory.createClassType(method.getDeclaringClass().getName());

        Modifiers modifiers = ModifierTransformer.transformJvm(method.getModifiers());
        if (isInline(method, javaClassType)) {
            modifiers = modifiers.with(Modifier.INLINE);
        }
        SignatureType signatureType = SignatureType.FUNCTION_CALL;
        return new FunctionSignature(name, parameters, TypeResolver.getTypeFromNameWithClazzAlias(returnType, getReturnNullability(method, javaClassType)),
                modifiers, owner, signatureType);
    }

    public static RFunctionSignature fromConstructor(Constructor constructor, JavaDeclaredClass owner) {
        String name = constructor.getName();
        List<RParameter> parameters = getParams(constructor.getParameters(), "<init>", org.objectweb.asm.Type.getConstructorDescriptor(constructor), owner);
        return new RFunctionSignature(name, parameters, VoidType.INSTANCE, ModifierTransformer.transformJvm(constructor.getModifiers()), new TypeReferenceImpl(owner));
    }

    public static Field fromField(java.lang.reflect.Field field, JavaDeclaredClass owner) {
        String name = field.getName();
        return Field.builder()
                .name(name)
                .owner(owner)
                .type(TypeResolver.getTypeFromNameWithClazzAlias(field.getType(), getNullability(field, owner)))
                .modifiers(ModifierTransformer.transformJvm(field.getModifiers())).build();
    }


    @SuppressWarnings("unchecked")
    private static boolean isInline(Method method, JavaDeclaredClass javaClassType) {

        final String methodDescriptor = org.objectweb.asm.Type.getMethodDescriptor(method);
        ClassNode classNode = javaClassType.getClassNode(true);
        Optional<MethodNode> methodNodeOp = ((List<MethodNode>) classNode.methods).stream()
                .filter(o -> (o.desc.equals(methodDescriptor)))
                .filter(methodNode -> methodNode.name.equals(method.getName()))
                .findFirst();

        if (methodNodeOp.isPresent()) {
            MethodNode methodNode = methodNodeOp.get();
            if (methodNode.invisibleAnnotations != null && ((List<AnnotationNode>) methodNode.invisibleAnnotations)
                    .stream()
                    .anyMatch(annotationNode -> annotationNode.desc.equals(INLINE_DESCRIPTOR))) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static Type.Nullability getNullability(java.lang.reflect.Field field, JavaClassType javaClassType) {
        if (field.getType().isPrimitive()) {
            return Type.Nullability.NOT_NULL;
        }

        ClassNode classNode = javaClassType.getClassNode(true);
        Optional<FieldNode> methodNodeOp = ((List<FieldNode>) classNode.fields).stream().filter(o -> (o.name.equals(field.getName())))
                .findAny();

        if (methodNodeOp.isPresent()) {
            FieldNode methodNode = methodNodeOp.get();
            if (methodNode.invisibleAnnotations != null) {
                boolean notNull = ((List<AnnotationNode>) methodNode.invisibleAnnotations).stream()
                        .anyMatch(annotationNode -> annotationNode.desc.equals(NOT_NULL_DESCRIPTOR));

                boolean nullable = ((List<AnnotationNode>) methodNode.invisibleAnnotations).stream()
                        .anyMatch(annotationNode -> annotationNode.desc.equals(NULLABLE_DESCRIPTOR));

                return getNullability(notNull, nullable);
            }
        }
        return Type.Nullability.UNKNOWN;
    }


    private static List<RParameter> getParams(java.lang.reflect.Parameter[] parameters, String name, String descriptor, JavaDeclaredClass type) {
        return getParamsThruReflection(parameters, name, descriptor, type);
    }

    @SuppressWarnings("unchecked")
    private static List<RParameter> getParamsThruReflection(java.lang.reflect.Parameter[] parameters, String name, String descriptor, JavaDeclaredClass type) {


        Optional<MethodNode> methodNodeOp = ((List<MethodNode>) type.getClassNode(true).methods).stream()
                .filter(o -> (o.desc.equals(descriptor)))
                .filter(methodNode -> methodNode.name.equals(name))
                .findFirst();

        MethodNode methodNode = methodNodeOp.orElseThrow(() -> new RuntimeException("Could not find method: " + name + "-" + descriptor));

        List<Parameter> parameterList = new ArrayList<>();
        for (int x = 0; x < parameters.length; x++) {
            java.lang.reflect.Parameter parameter = parameters[x];
            if (parameter.getType().isPrimitive()) {
                RParameter parameter1 = new RParameter(parameter.getName(),
                        TypeResolver.getTypeFromNameWithClazzAlias(parameter.getType(), Type.Nullability.NOT_NULL), null);
                parameterList.add(parameter1);
            } else {
                if (methodNode.invisibleParameterAnnotations != null) {

                    boolean notNull = ((List<AnnotationNode>) methodNode.invisibleParameterAnnotations[x]).stream()
                            .anyMatch(annotationNode -> annotationNode.desc.equals(NOT_NULL_DESCRIPTOR));
                    boolean nullable = ((List<AnnotationNode>) methodNode.invisibleParameterAnnotations[x]).stream()
                            .anyMatch(annotationNode -> annotationNode.desc.equals(NULLABLE_DESCRIPTOR));
                    Parameter parameter1 = new Parameter(parameter.getName(),
                            TypeResolver.getTypeFromNameWithClazzAlias(parameter.getType(), getNullability(notNull, nullable)), null);
                    parameterList.add(parameter1);
                } else {
                    Parameter parameter1 = new Parameter(parameter.getName(),
                            TypeResolver.getTypeFromNameWithClazzAlias(parameter.getType(), Type.Nullability.NOT_NULL), null);
                    parameterList.add(parameter1);
                }
            }
        }
        return parameterList;
    }


    @SuppressWarnings("unchecked")
    private static Type.Nullability getReturnNullability(Method method, JavaDeclaredClass javaClassType) {
        Class<?> returnType = method.getReturnType();
        if (returnType.isPrimitive()) {
            return Type.Nullability.NOT_NULL;
        }

        final String methodDescriptor = org.objectweb.asm.Type.getMethodDescriptor(method);
        ClassNode classNode = javaClassType.getClassNode(true);
        Optional<MethodNode> methodNodeOp = ((List<MethodNode>) classNode.methods).stream()
                .filter(o -> (o.desc.equals(methodDescriptor)))
                .filter(methodNode -> methodNode.name.equals(method.getName()))
                .findFirst();
        if (methodNodeOp.isPresent()) {
            MethodNode methodNode = methodNodeOp.get();

            if (methodNode.invisibleAnnotations != null) {
                boolean notNull = ((List<AnnotationNode>) methodNode.invisibleAnnotations).stream()
                        .anyMatch(annotationNode -> annotationNode.desc.equals(NOT_NULL_DESCRIPTOR));

                boolean nullable = ((List<AnnotationNode>) methodNode.invisibleAnnotations).stream()
                        .anyMatch(annotationNode -> annotationNode.desc.equals(NULLABLE_DESCRIPTOR));

                return getNullability(notNull, nullable);
            } else {
                return Type.Nullability.UNKNOWN;
            }

        } else {
            return Type.Nullability.UNKNOWN;
        }
    }

    private static Type.Nullability getNullability(boolean notNull, boolean nullable) {
        if (notNull) {
            return Type.Nullability.NOT_NULL;
        }
        if (nullable) {
            return Type.Nullability.NULLABLE;
        }
        return Type.Nullability.UNKNOWN;
    }

}
