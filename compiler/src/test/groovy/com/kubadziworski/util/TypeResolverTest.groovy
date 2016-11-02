package com.kubadziworski.util

import com.kubadziworski.antlr.EnkelParser
import com.kubadziworski.domain.type.BuiltInType
import com.kubadziworski.domain.type.DefaultTypes
import com.kubadziworski.domain.type.JavaClassType
import org.antlr.v4.runtime.tree.TerminalNode
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
        "java.lang.Integer" | new JavaClassType("java.lang.Integer")
        "int"               | BuiltInType.INT
        "boolean"           | BuiltInType.BOOLEAN
        "java.lang.String"  | DefaultTypes.STRING
        "byte[]"            | BuiltInType.BYTE_ARR
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
        "java.lang.Integer" | new JavaClassType("java.lang.Integer")
        "int"               | BuiltInType.INT
        "boolean"           | BuiltInType.BOOLEAN
        "java.lang.String"  | DefaultTypes.STRING
    }

    def "getFromTypeContext where typeContext = null should return VOID"() {
        when:
        def actualType = TypeResolver.getFromTypeContext(null)

        then:
        actualType.equals(BuiltInType.VOID)
    }

    def "GetFromValue"() {
        given:
        def valueCtx = Mock(EnkelParser.ValueContext)
        def terminalNode = Mock(TerminalNode)
        when:
        def actualType = TypeResolver.getFromValue(valueCtx)

        then:
        if (contextType == "int") {
            1 * valueCtx.IntegerLiteral() >> (contextType == "int" ? terminalNode : null)
        }
        if (contextType == "char") {
            1 * valueCtx.CharacterLiteral() >> (contextType == "char" ? terminalNode : null)
        }
        if (contextType == "float") {
            1 * valueCtx.FloatingPointLiteral() >> (contextType == "float" ? terminalNode : null)
        }
        if (contextType == "boolean") {
            1 * valueCtx.BOOL() >> (contextType == "boolean" ? terminalNode : null)
        }

        1 * valueCtx.getText() >> stringValue
        actualType.equals(expectedType)

        where:
        stringValue | contextType | expectedType
        "true"      | "boolean"   | BuiltInType.BOOLEAN
        "5.5f"      | "float"     | BuiltInType.FLOAT
        "0x20D"     | "int"       | BuiltInType.INT
        "something" | "string"    | DefaultTypes.STRING
        "'c'"       | "char"    | BuiltInType.CHAR
    }

}
