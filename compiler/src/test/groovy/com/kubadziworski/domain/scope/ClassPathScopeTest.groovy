package com.kubadziworski.domain.scope

import com.kubadziworski.domain.node.expression.Parameter
import com.kubadziworski.domain.type.DefaultTypes
import com.kubadziworski.domain.type.JavaClassType
import com.kubadziworski.domain.type.Type
import com.kubadziworski.domain.type.intrinsic.AnyType
import com.kubadziworski.domain.type.intrinsic.TypeProjection
import com.kubadziworski.domain.type.intrinsic.VoidType
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes
import spock.lang.Specification

import java.lang.reflect.Modifier
/**
 * Created by kuba on 11.05.16.
 */
class ClassPathScopeTest extends Specification {
    def "GetMethodSignature"() {
        given:
        def expectedParams = expectedParamsTypes.collect {
            new Parameter("arg0", it, null)
        }
        def expectedSignature = new FunctionSignature(expectedName, expectedParams, expectedReturnType, Modifier.PUBLIC, type)
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getMethodSignature(type, methodName, args)
        then:
        actualSignature.isPresent()
        actualSignature.get().equals(expectedSignature);
        where:
        methodName     | type                                      | args                                        | expectedName   | expectedParamsTypes                                                                                                                    | expectedReturnType
        "equals"       | DefaultTypes.STRING                       | [new JavaClassType(java.lang.Object.class)] | "equals"       | [new TypeProjection(AnyType.INSTANCE, Type.Nullability.UNKNOWN)]                                                                       | PrimitiveTypes.BOOLEAN_TYPE
        "hashCode"     | new JavaClassType(java.lang.Object.class) | []                                          | "hashCode"     | []                                                                                                                                     | PrimitiveTypes.INT_TYPE
        "replaceFirst" | DefaultTypes.STRING                       | [DefaultTypes.STRING, DefaultTypes.STRING]  | "replaceFirst" | [new TypeProjection(DefaultTypes.STRING, Type.Nullability.UNKNOWN), new TypeProjection(DefaultTypes.STRING, Type.Nullability.UNKNOWN)] | new TypeProjection(DefaultTypes.STRING, Type.Nullability.UNKNOWN)
    }

    def "GetConstructorSignature should not return private method"() {
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getMethodSignature(type as JavaClassType, methodName, args)
        then:
        !actualSignature.isPresent()
        where:
        methodName             | type              | args
        "indexOfSupplementary" | DefaultTypes.STRING | [PrimitiveTypes.INT_TYPE, PrimitiveTypes.INT_TYPE]
    }

    def "GetConstructorSignature"() {
        given:
        def expectedParams = expectedParamsTypes.collect {
            new Parameter("arg", it, null)
        }
        def expectedSignature = new FunctionSignature(expectedClassName, expectedParams, VoidType.INSTANCE, Modifier.PUBLIC, new JavaClassType(Class.forName(className)));
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getConstructorSignature(new JavaClassType(Class.forName(className)), args)
        then:
        actualSignature.isPresent()
        actualSignature.get().equals(expectedSignature);
        where:
        className          | args                              | expectedClassName  | expectedParamsTypes
        "java.lang.String" | []                                | "java.lang.String" | []
        "java.lang.String" | [new JavaClassType(byte[].class)] | "java.lang.String" | [new TypeProjection(new JavaClassType(byte[].class), Type.Nullability.UNKNOWN)]
        "java.lang.Long"   | [new JavaClassType(Long.class)]   | "java.lang.Long"   | [new TypeProjection(PrimitiveTypes.LONG_TYPE, Type.Nullability.NOT_NULL)]
    }

    def "GetConstructorSignature should not return private Constructor"() {
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getConstructorSignature(new JavaClassType(Class.forName(className)), args)
        then:
        !actualSignature.isPresent()
        where:
        className        | args
        "java.lang.Math" | []
    }
}
