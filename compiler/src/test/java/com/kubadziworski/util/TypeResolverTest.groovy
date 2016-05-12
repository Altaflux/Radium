package com.kubadziworski.util

import com.kubadziworski.antlr.EnkelParser
import com.kubadziworski.domain.type.BultInType
import com.kubadziworski.domain.type.ClassType
import spock.lang.Specification

/**
 * Created by kuba on 12.05.16.
 */
class TypeResolverTest extends Specification {
    def "GetFromTypeName with string"() {
        when:
        def actualType = TypeResolver.getFromTypeName(typeName)

        then:
        actualType.equals(expectedType)

        where:
        typeName            | expectedType
        "java.lang.Integer" | new ClassType("java.lang.Integer")
        "int"               | BultInType.INT
        "boolean"           | BultInType.BOOLEAN
        "java.lang.String"  | BultInType.STRING
        "byte[]"  | BultInType.BYTE_ARR
    }

    def "getFromTypeContext"() {
        given:
        EnkelParser.TypeContext typeContext = Mock(EnkelParser.TypeContext)

        when:
        def actualType = TypeResolver.getFromTypeContext(typeContext)

        then:
        1 * typeContext.getText() >> typeName
        actualType.equals(expectedType)

        where:
        typeName            | expectedType
        "java.lang.Integer" | new ClassType("java.lang.Integer")
        "int"               | BultInType.INT
        "boolean"           | BultInType.BOOLEAN
        "java.lang.String"  | BultInType.STRING
    }

    def "getFromTypeContext where typeContext = null should return VOID"() {
        when:
        def actualType = TypeResolver.getFromTypeContext(null)

        then:
        actualType.equals(BultInType.VOID)
    }

    def "GetFromValue"() {
        when:
        def actualType = TypeResolver.getFromValue(value)

        then:
        actualType.equals(expectedType)

        where:
        value       | expectedType
        "5"         | BultInType.INT
        "5.5"       | BultInType.FLOAT
        "something" | BultInType.STRING
    }

    def "GetValueFromString"() {
        //TODO to powinno byc gdzie indziej
    }
}
