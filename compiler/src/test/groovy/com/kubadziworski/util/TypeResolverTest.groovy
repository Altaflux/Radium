package com.kubadziworski.util

import com.kubadziworski.antlr.EnkelParser
import com.kubadziworski.domain.type.BuiltInType
import com.kubadziworski.domain.type.DefaultTypes
import com.kubadziworski.domain.type.JavaClassType
import com.kubadziworski.domain.type.Type
import com.kubadziworski.domain.type.intrinsic.UnitType
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes
import org.antlr.v4.runtime.tree.TerminalNode
import spock.lang.Specification

/**
 * Created by kuba on 12.05.16.
 */
class TypeResolverTest extends Specification {
    def "GetFromTypeName with string"() {
        when:
        def actualType = TypeResolver.getFromTypeName(typeName, Type.Nullability.NOT_NULL)

        then:
        actualType.equals(expectedType)

        where:
        typeName            | expectedType
        "java.lang.Integer" | new JavaClassType("java.lang.Integer")
        "radium.Int"               | PrimitiveTypes.INT_TYPE
        "radium.Boolean"           | PrimitiveTypes.BOOLEAN_TYPE
        "java.lang.String"  | DefaultTypes.STRING
        "byte[]"            | BuiltInType.BYTE_ARR
    }

    def "getFromTypeContext"() {
        given:
        EnkelParser.TypeContext typeContext = Mock(EnkelParser.TypeContext)
        EnkelParser.TypeCompositionContext compositionContext = Mock()
        typeContext.simpleName = compositionContext

        when:
        def actualType = TypeResolver.getFromTypeContext(typeContext)

        then:
        1 * compositionContext.getText() >> typeName
        actualType.equals(expectedType)

        where:
        typeName            | expectedType
        "java.lang.Integer" | new JavaClassType("java.lang.Integer")
        "radium.Int"               | PrimitiveTypes.INT_TYPE
        "radium.Boolean"           | PrimitiveTypes.BOOLEAN_TYPE
        "java.lang.String"  | DefaultTypes.STRING
    }

    def "getFromTypeContext where typeContext = null should return VOID"() {
        when:
        def actualType = TypeResolver.getFromTypeContext(null)

        then:
        actualType.equals(UnitType.INSTANCE)
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
        "true"      | "boolean"   | PrimitiveTypes.BOOLEAN_TYPE
        "5.5f"      | "float"     | PrimitiveTypes.FLOAT_TYPE
        "0x20D"     | "int"       | PrimitiveTypes.INT_TYPE
        "something" | "string"    | DefaultTypes.STRING
        "'c'"       | "char"    | PrimitiveTypes.CHAR_TYPE
    }

}
