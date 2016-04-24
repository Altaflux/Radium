package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.ForExpressionContext;
import com.kubadziworski.antlr.EnkelParser.ForStatementContext;
import com.kubadziworski.antlr.EnkelParser.VarReferenceContext;
import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.expression.RangedForStatement;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.statement.AssignmentStatement;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.domain.statement.VariableDeclarationStatement;
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
        ForExpressionContext forExpressionContext = ctx.forExpression();
        Expression startExpression = forExpressionContext.startExpr.accept(expressionVisitor);
        Expression endExpression = forExpressionContext.endExpr.accept(expressionVisitor);
        VarReferenceContext iterator = forExpressionContext.iterator;
        String varName = iterator.getText();
        if(scope.localVariableExists(varName)) {
            Statement iteratorVariable = new AssignmentStatement(varName, startExpression);
            Statement statement = ctx.statement().accept(statementVisitor);
            return new RangedForStatement(iteratorVariable, startExpression, endExpression,statement, varName, scope);
        } else {
            scope.addLocalVariable(new LocalVariable(varName,startExpression.getType()));
            Statement iteratorVariable = new VariableDeclarationStatement(varName,startExpression);
            Statement statement = ctx.statement().accept(statementVisitor);
            return new RangedForStatement(iteratorVariable, startExpression, endExpression,statement, varName,scope);
        }
    }

}
