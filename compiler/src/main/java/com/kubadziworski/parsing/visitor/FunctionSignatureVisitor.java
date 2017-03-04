package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelParser.FunctionDeclarationContext;
import com.kubadziworski.antlr.EnkelParser.ParametersListContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.parsing.visitor.expression.function.ParameterExpressionListVisitor;
import com.kubadziworski.util.TypeResolver;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by kuba on 06.04.16.
 */
public class FunctionSignatureVisitor extends EnkelParserBaseVisitor<FunctionSignature> {

    private final ExpressionVisitor expressionVisitor;
    private final Scope scope;

    public FunctionSignatureVisitor(Scope scope) {
        this.expressionVisitor = new ExpressionVisitor(scope);
        this.scope = scope;
    }

    @Override
    public FunctionSignature visitFunctionDeclaration(FunctionDeclarationContext ctx) {
        String functionName = ctx.functionName().getText();
        Type returnType = TypeResolver.getFromTypeContext(ctx.type(), scope);

        if (returnType.getName().equals("radium.Unit") && returnType.isNullable().equals(Type.Nullability.NULLABLE)) {
            returnType = new TypeProjection(UnitType.CONCRETE_INSTANCE, Type.Nullability.NULLABLE);
        }

        if (returnType.getName().equals("radium.Unit") && returnType.isNullable().equals(Type.Nullability.NOT_NULL)) {
            returnType = new TypeProjection(VoidType.INSTANCE, Type.Nullability.NOT_NULL);
        }


        Modifiers modifiersSet = Modifiers.empty();
        if (ctx.methodModifier() != null) {
            Set<Modifier> mSets = ctx.methodModifier().methodModifiers().stream()
                    .map(methodModifiersContext -> Modifier.fromValue(methodModifiersContext.getText()))
                    .collect(Collectors.toSet());
            for (Modifier md : mSets) {
                modifiersSet = modifiersSet.with(md);
            }
            if (ctx.methodModifier().accessModifiers() != null) {
                modifiersSet = modifiersSet.with(Modifier.fromValue(ctx.methodModifier().accessModifiers().getText()));
            } else {
                modifiersSet = modifiersSet.with(Modifier.PUBLIC);
            }
        }

        ParametersListContext parametersCtx = ctx.parametersList();
        SignatureType signatureType = SignatureType.FUNCTION_CALL;
        if (parametersCtx != null) {

            List<Parameter> parameters = parametersCtx.accept(new ParameterExpressionListVisitor(expressionVisitor, scope));
            return new FunctionSignature(functionName, parameters, returnType, modifiersSet, scope.getClassType(), signatureType);
        }
        return new FunctionSignature(functionName, Collections.emptyList(), returnType, modifiersSet, scope.getClassType(), signatureType);

    }
}
