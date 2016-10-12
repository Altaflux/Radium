package com.kubadziworski.parsing.visitor.statement

import com.kubadziworski.antlr.EnkelParser
import com.kubadziworski.domain.MetaData
import com.kubadziworski.domain.node.expression.EmptyExpression
import com.kubadziworski.domain.node.expression.Value
import com.kubadziworski.domain.scope.LocalVariable
import com.kubadziworski.domain.scope.Scope
import com.kubadziworski.domain.type.BultInType
import com.kubadziworski.domain.type.ClassType
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor
import spock.lang.Specification

/**
 * Created by kuba on 13.05.16.
 */
class AssignmentStatementVisitorTest extends Specification {

    def "should create Assignment Object from antlr generated AssignmentContext object"() {
        given:
            Scope scope1 = Mock()
            EnkelParser.AssignmentContext assignmentContext = Mock()
            EnkelParser.NameContext nameContext = Mock()
            EnkelParser.ExpressionContext expressionContext = Mock()
            ExpressionVisitor expressionVisitor = Mock()
            assignmentContext.postExpr = expressionContext
        when:
            def assignment = new AssignmentStatementVisitor(expressionVisitor, scope1).visitAssignment(assignmentContext)
        then:
            1* scope1.isLocalVariableExists(name) >> true
            1* assignmentContext.name() >> nameContext
            1* nameContext.getText() >> name
            1* expressionContext.accept(expressionVisitor) >> expression

            assignment.assignmentExpression == expression
            assignment.varName == name
        where:
            name | expression
            "cos" | new EmptyExpression(BultInType.VOID)
            "int assignment" | new Value(BultInType.INT,"255")
    }
}
