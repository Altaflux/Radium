package com.kubadziworski.domain.scope

import com.kubadziworski.domain.node.expression.Parameter
import com.kubadziworski.domain.type.BultInType
import com.kubadziworski.domain.type.ClassType
import spock.lang.Specification

/**
 * Created by kuba on 11.05.16.
 */
class ClassPathScopeTest extends Specification {
    def "GetMethodSignature"() {
        given:
        def expectedParams = expectedParamsTypes.collect {
            new Parameter("arg", it, Optional.empty())
        }
        def expectedSignature = new FunctionSignature(expectedName, expectedParams, expectedReturnType)
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getMethodSignature(type, methodName, args)
        then:
        actualSignature.isPresent()
        actualSignature.get().equals(expectedSignature);
        where:
        methodName     | type                              | args                                   | expectedName   | expectedParamsTypes                    | expectedReturnType
        "equals"       | BultInType.STRING                 | [new ClassType("java.lang.Object")]    | "equals"       | [new ClassType("java.lang.Object")]    | BultInType.BOOLEAN
        "hashCode"     | new ClassType("java.lang.Object") | []                                     | "hashCode"     | []                                     | BultInType.INT
        "replaceFirst" | BultInType.STRING                 | [BultInType.STRING, BultInType.STRING] | "replaceFirst" | [BultInType.STRING, BultInType.STRING] | BultInType.STRING
    }

    def "GetConstructorSignature should not return private method"() {
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getMethodSignature(type, methodName, args)
        then:
        !actualSignature.isPresent()
        where:
        methodName             | type              | args
        "indexOfSupplementary" | BultInType.STRING | [BultInType.INT, BultInType.INT]
    }

    def "GetConstructorSignature"() {
        given:
        def expectedParams = expectedParamsTypes.collect {
            new Parameter("arg", it, Optional.empty())
        }
        def expectedSignature = new FunctionSignature(expectedClassName, expectedParams, BultInType.VOID);
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getConstructorSignature(className, args)
        then:
        actualSignature.isPresent()
        actualSignature.get().equals(expectedSignature);
        where:
        className          | args                  | expectedClassName  | expectedParamsTypes
        "java.lang.String" | []                    | "java.lang.String" | []
        "java.lang.String" | [BultInType.BYTE_ARR] | "java.lang.String" | [BultInType.BYTE_ARR]
        "java.lang.Long"   | [BultInType.LONG]     | "java.lang.Long"   | [BultInType.LONG]
    }

    def "GetConstructorSignature should not return private Constructor"() {
        when:
        ClassPathScope classPathScope = new ClassPathScope();
        def actualSignature = classPathScope.getConstructorSignature(className, args)
        then:
        !actualSignature.isPresent()
        where:
        className        | args
        "java.lang.Math" | []
    }
}