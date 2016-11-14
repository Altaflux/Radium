package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.ExpressionContext;
import com.kubadziworski.antlr.EnkelParser.VariableDeclarationContext;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.VariableDeclaration;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.intrinsic.NullType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.IncompatibleTypesException;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

public class VariableDeclarationStatementVisitor extends EnkelBaseVisitor<VariableDeclaration> {
    private final ExpressionVisitor expressionVisitor;
    private final Scope scope;

    public VariableDeclarationStatementVisitor(ExpressionVisitor expressionVisitor, Scope scope) {
        this.expressionVisitor = expressionVisitor;
        this.scope = scope;
    }

    @Override
    public VariableDeclaration visitVariableDeclaration(@NotNull VariableDeclarationContext ctx) {
        String varName = ctx.name().getText();
        ExpressionContext expressionCtx = ctx.expression();
        Expression expression = expressionCtx.accept(expressionVisitor);

        boolean mutable = true;
        if (ctx.IMMUTABLE() != null) {
            mutable = false;
        } else if (ctx.VARIABLE() != null) {
            mutable = true;
        }

        Type declarationType = expression.getType();
        if (ctx.type() != null) {
            declarationType = TypeResolver.getFromTypeContext(ctx.type(), scope);
        }

        if (declarationType.equals(NullType.INSTANCE)) {
            declarationType = ClassTypeFactory.createClassType("radium.Nothing");
        }

        if (!expression.getType().equals(NullType.INSTANCE)) {
            if (expression.getType().inheritsFrom(declarationType) < 0) {
                throw new IncompatibleTypesException(varName, declarationType, expression.getType());
            }
        }
        scope.addLocalVariable(new LocalVariable(varName, declarationType, mutable));
        return new VariableDeclaration(varName, expression, declarationType, mutable);
    }
}