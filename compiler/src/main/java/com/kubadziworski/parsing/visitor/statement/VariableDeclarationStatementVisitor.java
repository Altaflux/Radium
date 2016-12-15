package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.ExpressionContext;
import com.kubadziworski.antlr.EnkelParser.VariableDeclarationContext;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.VariableDeclaration;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.NullType;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import com.kubadziworski.domain.type.intrinsic.VoidType;
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

        Type.Nullability nullability = declarationType.isNullable();

        if (declarationType instanceof TypeProjection) {
            declarationType = ((TypeProjection) declarationType).getInternalType();
        }

        if (declarationType.equals(NullType.INSTANCE)) {
            declarationType = new TypeProjection(ClassTypeFactory.createClassType("radium.Nothing"), nullability);
        }

        if (declarationType.equals(VoidType.INSTANCE)) {
            declarationType = new TypeProjection(UnitType.CONCRETE_INSTANCE, nullability);
        }

        if (!expression.getType().equals(NullType.INSTANCE)) {
            if (expression.getType().inheritsFrom(declarationType) < 0) {
                //TODO FIX VERY VERY UGLY HACK TO LET Unit instances to be equal to Concrete Unit instances when doing variable declaration
                if (!(declarationType.getName().equals(UnitType.CONCRETE_INSTANCE.getName()))) {
                    throw new IncompatibleTypesException(varName, declarationType, expression.getType());
                }
            }
        }


//        if (declarationType.isPrimitive()) {
//            Type.Nullability nullability = declarationType.isNullable();
//            if (declarationType instanceof TypeProjection) {
//                declarationType = ((TypeProjection) declarationType).getInternalType();
//            }
//            TypeProjection typeProjection = new TypeProjection(((BoxableType) declarationType).getUnBoxedType(), nullability);
//            scope.addLocalVariable(new LocalVariable(varName, typeProjection, mutable));
//            return new VariableDeclaration(new RuleContextElementImpl(ctx), varName, expression, typeProjection, mutable);
//        }

        Type finalType = new TypeProjection(declarationType, nullability);
        LocalVariable localVariable = new LocalVariable(varName, finalType, mutable);
        scope.addLocalVariable(localVariable);
        return new VariableDeclaration(new RuleContextElementImpl(ctx), localVariable, expression);
    }
}