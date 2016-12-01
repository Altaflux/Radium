package com.kubadziworski.bytecodegeneration.expression

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator
import com.kubadziworski.bytecodegeneration.statement.StatementGeneratorFilter
import com.kubadziworski.domain.MetaData
import com.kubadziworski.domain.node.expression.FieldReference
import com.kubadziworski.domain.node.expression.LocalVariableReference
import com.kubadziworski.domain.resolver.ImportResolver
import com.kubadziworski.domain.scope.Field
import com.kubadziworski.domain.scope.GlobalScope
import com.kubadziworski.domain.scope.LocalVariable
import com.kubadziworski.domain.scope.Scope
import com.kubadziworski.domain.type.DefaultTypes
import com.kubadziworski.domain.type.JavaClassType
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes
import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.InstructionAdapter
import spock.lang.Specification

import java.lang.reflect.Modifier
/**
 * Created by kuba on 13.05.16.
 */
class ReferenceExpressionGeneratorTest extends Specification {
    def "should generate field reference"() {
        given:
            MetaData metaData = new MetaData("Main", "", "java.lang.Object", Collections.emptyList())
            Scope scope = new Scope(metaData, new ImportResolver(Collections.emptyList(), new GlobalScope()))
            MethodVisitor methodVisitor = Mock()
            StatementGenerator expressionGenerator = new StatementGeneratorFilter(new InstructionAdapter(methodVisitor), scope)
            LocalVariable local = new LocalVariable("this",scope.getClassType())
            scope.addLocalVariable(new LocalVariable("this",scope.getClassType()))
            def field = new Field(name,owner,type, Modifier.PUBLIC)
            LocalVariableReference ref = new LocalVariableReference(local)
            def fieldReference = new FieldReference(field, ref)
        when:
            new ReferenceExpressionGenerator(methodVisitor).generate(fieldReference, expressionGenerator)
        then:
            1* methodVisitor.visitVarInsn(Opcodes.ALOAD,0)
            1* methodVisitor.visitFieldInsn(Opcodes.GETFIELD,field.ownerInternalName,field.name,field.type.descriptor)
        where:
            name    | owner                                                      | type
        "intVar"    | new JavaClassType(com.kubadziworski.test.DummyClass.class) | PrimitiveTypes.INT_TYPE
        "stringVar" | new JavaClassType(com.kubadziworski.test.DummyClass.class) | DefaultTypes.STRING
        "objVar"    | new JavaClassType(com.kubadziworski.test.DummyClass.class) | new JavaClassType(java.lang.Object.class)
    }

    def "should generate local variable reference"() {
        given:
            Scope scope = Mock()
            MethodVisitor methodVisitor = Mock()
            StatementGenerator expressionGenerator = new StatementGeneratorFilter(new InstructionAdapter(methodVisitor), scope)
            def variable = new LocalVariable(name,type)
            def localVariableReference = new LocalVariableReference(variable)
        when:
            new ReferenceExpressionGenerator(methodVisitor).generate(localVariableReference, scope)
        then:
            1* scope.getLocalVariableIndex(name) >> 3
            1* methodVisitor.visitVarInsn(expectedOpcode,3)
        where:
            name    | owner                                                      | type                                      | expectedOpcode
        "objVar"    | new JavaClassType(com.kubadziworski.test.DummyClass.class) | new JavaClassType(java.lang.Object.class) | Opcodes.ALOAD
        "intVar"    | new JavaClassType(com.kubadziworski.test.DummyClass.class) | PrimitiveTypes.INT_TYPE                   | Opcodes.ILOAD
        "stringVar" | new JavaClassType(com.kubadziworski.test.DummyClass.class) | DefaultTypes.STRING                       | Opcodes.ALOAD
    }
}
