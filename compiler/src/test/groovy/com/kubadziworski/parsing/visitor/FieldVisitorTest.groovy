package com.kubadziworski.parsing.visitor

import com.kubadziworski.antlr.EnkelParser
import com.kubadziworski.domain.scope.Field
import com.kubadziworski.domain.scope.FunctionSignature
import com.kubadziworski.domain.scope.LocalVariable
import com.kubadziworski.domain.scope.Scope
import com.kubadziworski.domain.type.BuiltInType
import com.kubadziworski.domain.type.DefaultTypes
import com.kubadziworski.domain.type.JavaClassType
import spock.lang.Specification

/**
 * Created by kuba on 13.05.16.
 */
class FieldVisitorTest extends Specification {
    def "should create Field object from antlr generated FieldContext object"() {
        given:
            EnkelParser.FieldContext fieldContext = Mock()
            EnkelParser.NameContext nameContext = Mock()
            EnkelParser.TypeContext typeContext = Mock()
            Scope scope = Mock()
        when:
            def field = new FieldVisitor(scope).visitField(fieldContext)
        then:
            field.ownerInternalName == expectedOwnerInternalName;
            field.type == expectedType;
            field.name == name;
            1* scope.getClassType() >> new JavaClassType("com.kubadziworski.test.DummyClass")
            2* scope.getFunctionSignatures() >> new ArrayList<FunctionSignature>()
            2* scope.getFields() >> new org.apache.commons.collections4.map.LinkedMap<String, Field>()
            2* scope.getLocalVariables() >> new org.apache.commons.collections4.map.LinkedMap<String, LocalVariable>()
            2* scope.getClassType() >> new JavaClassType("com.kubadziworski.test.DummyClass")
            1* nameContext.getText() >> name
            1* fieldContext.name() >> nameContext
            1* fieldContext.type() >> typeContext
            1* typeContext.getText() >> typeName

            if(typeName == "java.lang.Integer"){
                1* scope.resolveClassName(typeName) >> new JavaClassType("java.lang.Integer")
            }
        where:
        name        | typeName            | expectedType                           | expectedOwnerInternalName
        "var"       | "int"               | BuiltInType.INT                        | "com/kubadziworski/test/DummyClass"
        "stringVar" | "java.lang.String"  | DefaultTypes.STRING                    | "com/kubadziworski/test/DummyClass"
        "objVar"    | "java.lang.Integer" | new JavaClassType("java.lang.Integer") | "com/kubadziworski/test/DummyClass"
    }
}
