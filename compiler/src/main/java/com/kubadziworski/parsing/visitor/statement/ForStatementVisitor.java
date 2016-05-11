package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.ForConditionsContext;
import com.kubadziworski.antlr.EnkelParser.ForStatementContext;
import com.kubadziworski.antlr.EnkelParser.VariableReferenceContext;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.RangedForStatement;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.node.statement.VariableDeclaration;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * Created by kuba on 23.04.16.
 */
public class ForStatementVisitor extends EnkelBaseVisitor<RangedForStatement> {
    private final Scope scope;
    private final ExpressionVisitor expressionVisitor;
    private final StatementVisitor statementVisitor;

    public ForStatementVisitor(Scope scope) {
        this.scope = new Scope(scope);
        expressionVisitor = new ExpressionVisitor(this.scope);
        statementVisitor = new StatementVisitor(this.scope);
    }

    @Override
    public RangedForStatement visitForStatement(@NotNull ForStatementContext ctx) {
        ForConditionsContext forExpressionContext = ctx.forConditions();
        Expression startExpression = forExpressionContext.startExpr.accept(expressionVisitor);
        Expression endExpression = forExpressionContext.endExpr.accept(expressionVisitor);
        VariableReferenceContext iterator = forExpressionContext.iterator;
        String varName = iterator.getText();
        if(scope.isLocalVariableExists(varName)) {
            Statement iteratorVariable = new Assignment(varName, startExpression);
            Statement statement = ctx.statement().accept(statementVisitor);
            return new RangedForStatement(iteratorVariable, startExpression, endExpression,statement, varName, scope);
        } else {
            scope.addLocalVariable(new LocalVariable(varName,startExpression.getType()));
            Statement iteratorVariable = new VariableDeclaration(varName,startExpression);
            Statement statement = ctx.statement().accept(statementVisitor);
            return new RangedForStatement(iteratorVariable, startExpression, endExpression,statement, varName,scope);
        }
    }

}
