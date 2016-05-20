package com.kubadziworski.bytecodegeneration.statement

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator
import com.kubadziworski.domain.node.expression.Value
import com.kubadziworski.domain.node.statement.Assignment
import com.kubadziworski.domain.scope.Field
import com.kubadziworski.domain.scope.LocalVariable
import com.kubadziworski.domain.scope.Scope
import com.kubadziworski.domain.type.BultInType
import com.kubadziworski.domain.type.ClassType
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import spock.lang.Specification

/**
 * Created by kuba on 13.05.16.
 */
class AssignmentStatementGeneratorTest extends Specification {
//
//    String varName = assignment.getVarName();
//    Expression assignmentExpression = assignment.getExpression();
//    Type variableType = assignmentExpression.getType();
//    if(scope.isLocalVariableExists(varName)) {
//        int index = scope.getLocalVariableIndex(varName);
//        methodVisitor.visitVarInsn(variableType.getStoreVariableOpcode(), index);
//        return;
//    }
//    Field field = scope.getField(varName);
//    String descriptor = field.getType().getDescriptor();
//    methodVisitor.visitVarInsn(Opcodes.ALOAD,0);
//    assignmentExpression.accept(expressionGenerator);
//    methodVisitor.visitFieldInsn(Opcodes.PUTFIELD,field.getOwnerInternalName(),field.getName(),descriptor);
    def "should generate bytecode for local variable if variable for name exists in scope"() {
        given:
            def assignment = new Assignment(varName,assignmentExpression)
            def localVariable = Mock(LocalVariable)
            MethodVisitor methodVisitor = Mock()
            ExpressionGenerator expressionGenerator = Mock()
            Scope scope = Mock()
        when:
            new AssignmentStatementGenerator(methodVisitor,expressionGenerator,scope).generate(assignment)
        then :
            1*scope.isLocalVariableExists(varName) >> true
            1*scope.getLocalVariableIndex(varName) >> 3
            1* scope.getLocalVariable(varName) >> localVariable
            1* localVariable.getType() >> assignmentExpression.getType()
            1*methodVisitor.visitVarInsn(expectedOpcode,3)
        where:
            varName  | assignmentExpression                    | expectedOpcode
            "var"    | new Value(BultInType.INT, "25")         | Opcodes.ISTORE
            "strVar" | new Value(BultInType.STRING, "somestr") | Opcodes.ASTORE
    }

    def "should generate bytecode for assignment if field for name exists in scope but local variable does not"() {
        given:
            def assignment = new Assignment(varName,assignmentExpression)
            def field = new Field(varName, variableOwner, variableType)
            MethodVisitor methodVisitor = Mock()
            ExpressionGenerator expressionGenerator = Mock()
            Scope scope = Mock()
        when:
            new AssignmentStatementGenerator(methodVisitor,expressionGenerator,scope).generate(assignment)
            then :
            1*scope.isLocalVariableExists(varName) >> false
            1*scope.getField(varName) >> field
            1* methodVisitor.visitFieldInsn(Opcodes.PUTFIELD,field.ownerInternalName,field.name,field.type.descriptor)
        where:
            varName     | variableOwner         | variableType      | assignmentExpression
            "var"       | new ClassType("Main") | BultInType.INT    | new Value(BultInType.INT, "25")
            "stringVar" | new ClassType("Main") | BultInType.STRING | new Value(BultInType.STRING, "someString")
    }
}
