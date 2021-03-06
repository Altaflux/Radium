package com.kubadziworski.bytecodegeneration.expression

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator
import com.kubadziworski.bytecodegeneration.statement.StatementGeneratorFilter
import com.kubadziworski.compiler.RadiumArguments
import com.kubadziworski.configuration.CompilerConfigInstance
import com.kubadziworski.domain.MetaData
import com.kubadziworski.domain.Modifier
import com.kubadziworski.domain.Modifiers
import com.kubadziworski.domain.node.expression.FieldReference
import com.kubadziworski.domain.node.expression.LocalVariableReference
import com.kubadziworski.domain.scope.Field
import com.kubadziworski.domain.scope.FunctionScope
import com.kubadziworski.domain.scope.LocalVariable
import com.kubadziworski.domain.scope.Scope
import com.kubadziworski.domain.type.DefaultTypes
import com.kubadziworski.domain.type.JavaClassType
import com.kubadziworski.domain.type.Type
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes
import com.kubadziworski.resolver.ClazzImportResolver
import com.kubadziworski.resolver.ImportResolver
import com.kubadziworski.resolver.ResolverContainer
import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.InstructionAdapter
import spock.lang.Specification

import java.util.function.Supplier

/**
 * Created by kuba on 13.05.16.
 */
class ReferenceExpressionGeneratorTest extends Specification {
    def setupSpec() {
        RadiumArguments arguments = new RadiumArguments()
        arguments.classLoader = ClassLoader.systemClassLoader;
        CompilerConfigInstance.initialize(arguments)
    }

    def "should generate field reference"() {
        given:
        MetaData metaData = new MetaData("Main", "", new Supplier<Type>() {
            @Override
            Type get() {
                return new JavaClassType(java.lang.Object.class)
            }
        }, new Supplier<List<Type>>() {
            @Override
            List<Type> get() {
                return Collections.emptyList()
            }
        }, "Main.enk")
        ResolverContainer container = new ResolverContainer(Arrays.asList(new ClazzImportResolver(ClassLoader.systemClassLoader)))
        Scope scope = new Scope(metaData, new ImportResolver(Collections.emptyList(), container))
        FunctionScope functionScope = new FunctionScope(scope, null)
            MethodVisitor methodVisitor = Mock()
            StatementGenerator expressionGenerator = new StatementGeneratorFilter(new InstructionAdapter(methodVisitor), functionScope)
            LocalVariable local = new LocalVariable("this",scope.getClassType())
        functionScope.addLocalVariable(new LocalVariable("this",scope.getClassType()))

        def field = Field.builder().name(name).owner(owner).type(type).modifiers(Modifiers.empty().with(Modifier.PUBLIC)).build()
            LocalVariableReference ref = new LocalVariableReference(local)
            def fieldReference = new FieldReference(field, ref)
        when:
        new ReferenceExpressionGenerator(new InstructionAdapter(methodVisitor)).generate(fieldReference, expressionGenerator)
        then:
            1* methodVisitor.visitVarInsn(Opcodes.ALOAD,0)
        1 * methodVisitor.visitFieldInsn(Opcodes.GETFIELD, field.owner.asmType.internalName, field.name, field.type.asmType.descriptor)
        where:
            name    | owner                                                      | type
        "intVar"    | new JavaClassType(com.kubadziworski.test.DummyClass.class) | PrimitiveTypes.INT_TYPE
        "stringVar" | new JavaClassType(com.kubadziworski.test.DummyClass.class) | DefaultTypes.STRING
        "objVar"    | new JavaClassType(com.kubadziworski.test.DummyClass.class) | new JavaClassType(java.lang.Object.class)
    }

    def "should generate local variable reference"() {
        given:
            FunctionScope scope = Mock()
            MethodVisitor methodVisitor = Mock()
            StatementGenerator expressionGenerator = new StatementGeneratorFilter(new InstructionAdapter(methodVisitor), scope)
            def variable = new LocalVariable(name,type)
            def localVariableReference = new LocalVariableReference(variable)
        when:
        new ReferenceExpressionGenerator(new InstructionAdapter(methodVisitor)).generate(localVariableReference, scope)
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
