package com.kubadziworski.bytecodegeneration.expression

import com.kubadziworski.domain.node.expression.FieldReference
import com.kubadziworski.domain.node.expression.LocalVariableReference
import com.kubadziworski.domain.scope.Field
import com.kubadziworski.domain.scope.LocalVariable
import com.kubadziworski.domain.scope.Scope
import com.kubadziworski.domain.type.BultInType
import com.kubadziworski.domain.type.ClassType
import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes
import org.objectweb.asm.MethodVisitor
import spock.lang.Specification

/**
 * Created by kuba on 13.05.16.
 */
class ReferenceExpressionGeneratorTest extends Specification {
    def "should generate field reference"() {
        given:
            Scope scope = Mock()
            MethodVisitor methodVisitor = Mock()
            def field = new Field(name,owner,type)
            def fieldReference = new FieldReference(field)
        when:
            new ReferenceExpressionGenerator(methodVisitor,scope).generate(fieldReference)
        then:
            1* methodVisitor.visitVarInsn(Opcodes.ALOAD,0)
            1* methodVisitor.visitFieldInsn(Opcodes.GETFIELD,field.ownerInternalName,field.name,field.type.descriptor)
        where:
            name        | owner                 | type
            "intVar"    | new ClassType("Main") | BultInType.INT
            "stringVar" | new ClassType("Main") | BultInType.STRING
            "objVar"    | new ClassType("Main") | new ClassType("java.lang.Object")
    }

    def "should generate local variable reference"() {
        given:
            Scope scope = Mock()
            MethodVisitor methodVisitor = Mock()
            def variable = new LocalVariable(name,type)
            def localVariableReference = new LocalVariableReference(variable)
        when:
            new ReferenceExpressionGenerator(methodVisitor,scope).generate(localVariableReference)
        then:
            1* scope.getLocalVariableIndex(name) >> 3
            1* methodVisitor.visitVarInsn(expectedOpcode,3)
        where:
            name        | owner                 | type                              | expectedOpcode
            "objVar"    | new ClassType("Main") | new ClassType("java.lang.Object") | Opcodes.ALOAD
            "intVar"    | new ClassType("Main") | BultInType.INT                    | Opcodes.ILOAD
            "stringVar" | new ClassType("Main") | BultInType.STRING                 | Opcodes.ALOAD
    }
}
