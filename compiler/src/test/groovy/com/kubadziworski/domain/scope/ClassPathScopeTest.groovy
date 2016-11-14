package com.kubadziworski.domain.scope

import com.kubadziworski.domain.node.expression.Parameter
import com.kubadziworski.domain.type.BuiltInType
import com.kubadziworski.domain.type.DefaultTypes
import com.kubadziworski.domain.type.JavaClassType
import com.kubadziworski.domain.type.intrinsic.UnitType
import spock.lang.Specification

import java.lang.reflect.Modifier
/**
 * Created by kuba on 11.05.16.
 */
class ClassPathScopeTest extends Specification {
    def "GetMethodSignature"() {
        given:
        def expectedParams = expectedParamsTypes.collect {
            new Parameter("arg", it, null)
        }
        def expectedSignature = new FunctionSignature(expectedName, expectedParams, expectedReturnType, Modifier.PUBLIC, type)
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getMethodSignature(type, methodName, args)
        then:
        actualSignature.isPresent()
        actualSignature.get().equals(expectedSignature);
        where:
        methodName     | type                              | args                                         | expectedName   | expectedParamsTypes                        | expectedReturnType
        "equals"       | DefaultTypes.STRING                 | [new JavaClassType("java.lang.Object")]    | "equals"       | [new JavaClassType("java.lang.Object")]        | BuiltInType.BOOLEAN
        "hashCode"     | new JavaClassType("java.lang.Object") | []                                           | "hashCode"     | []                                         | BuiltInType.INT
        "replaceFirst" | DefaultTypes.STRING                 | [DefaultTypes.STRING, DefaultTypes.STRING] | "replaceFirst" | [DefaultTypes.STRING, DefaultTypes.STRING] | DefaultTypes.STRING
    }

    def "GetConstructorSignature should not return private method"() {
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getMethodSignature(type, methodName, args)
        then:
        !actualSignature.isPresent()
        where:
        methodName             | type              | args
        "indexOfSupplementary" | DefaultTypes.STRING | [BuiltInType.INT, BuiltInType.INT]
    }

    def "GetConstructorSignature"() {
        given:
        def expectedParams = expectedParamsTypes.collect {
            new Parameter("arg", it, null)
        }
        def expectedSignature = new FunctionSignature(expectedClassName, expectedParams, UnitType.INSTANCE, Modifier.PUBLIC, new JavaClassType(className));
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getConstructorSignature(new JavaClassType(className), args)
        then:
        actualSignature.isPresent()
        actualSignature.get().equals(expectedSignature);
        where:
        className          | args                   | expectedClassName  | expectedParamsTypes
        "java.lang.String" | []                     | "java.lang.String" | []
        "java.lang.String" | [BuiltInType.BYTE_ARR] | "java.lang.String" | [BuiltInType.BYTE_ARR]
        "java.lang.Long"   | [BuiltInType.LONG]     | "java.lang.Long"   | [BuiltInType.LONG]
    }

    def "GetConstructorSignature should not return private Constructor"() {
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getConstructorSignature(new JavaClassType(className), args)
        then:
        !actualSignature.isPresent()
        where:
        className        | args
        "java.lang.Math" | []
    }
}