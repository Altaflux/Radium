package com.kubadziworski.parsing.visitor.phase;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.scope.*;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.parsing.visitor.expression.function.ParameterExpressionVisitor;
import com.kubadziworski.util.PropertyAccessorsUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ConstructorPhaseVisitor extends EnkelParserBaseVisitor<Scope> {

    private final Scope scope;

    public ConstructorPhaseVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope visitClassDeclaration(EnkelParser.ClassDeclarationContext ctx) {
        if (ctx.primaryConstructor() == null || ctx.primaryConstructor().constructorParametersList() == null) {
            scope.addConstructor(new FunctionSignature(scope.getClassType().getName(), Collections.emptyList(),
                    VoidType.INSTANCE, Modifiers.empty().with(Modifier.PUBLIC), scope.getClassType(), SignatureType.CONSTRUCTOR_CALL));
            return scope;
        }

        FunctionScope functionScope = new FunctionScope(scope, null);
        ParameterExpressionVisitor parameterExpressionVisitor = new ParameterExpressionVisitor(new ExpressionVisitor(functionScope), functionScope);
        Stream<Pair<Parameter, Field>> normalParameters = ctx.primaryConstructor().constructorParametersList().constructorParam()
                .stream().map(constructorParamContext -> {
                    Parameter parameter = parameterExpressionVisitor.visitParameter(constructorParamContext.parameter());
                    if (constructorParamContext.asField != null) {
                        return Pair.of(parameter, buildField(constructorParamContext.accessModifiers(), constructorParamContext.KEYWORD_val() != null, parameter));
                    }
                    return Pair.of(parameter, (Field) null);
                });

        Stream<Pair<Parameter, Field>> defParameters = ctx.primaryConstructor().constructorParametersList().constructorParameterWithDefaultValue()
                .stream().map(constructorParamContext -> {
                    Parameter parameter = parameterExpressionVisitor.visitParameterWithDefaultValue(constructorParamContext.parameterWithDefaultValue());
                    if (constructorParamContext.asField != null) {
                        Parameter invisibleParam = new Parameter(parameter.getName(), parameter.getType(), parameter.getDefaultValue().get(), false);
                        return Pair.of(invisibleParam, buildField(constructorParamContext.accessModifiers(), constructorParamContext.KEYWORD_val() != null, parameter));
                    }
                    return Pair.of(parameter, (Field) null);
                });
        List<Pair<Parameter, Field>> parameters = Stream.concat(normalParameters, defParameters).collect(Collectors.toList());
        List<Parameter> parameterList = parameters.stream().map(Pair::getKey).collect(Collectors.toList());
        FunctionSignature signature = new FunctionSignature(scope.getClassType().getName(), parameterList, VoidType.INSTANCE, Modifiers.empty().with(Modifier.PUBLIC),
                scope.getClassType(), SignatureType.CONSTRUCTOR_CALL);
        scope.addConstructor(signature);

        parameters.stream().map(Pair::getValue).filter(Objects::nonNull).forEach(scope::addField);

        return scope;
    }

    private Field buildField(EnkelParser.AccessModifiersContext accessModifiers, boolean finalKey, Parameter parameter) {
        Modifiers modifiersSet = Modifiers.empty();
        if (accessModifiers != null) {
            modifiersSet = modifiersSet.with(Modifier.fromValue(accessModifiers.getText()));
        } else {
            modifiersSet = modifiersSet.with(Modifier.PUBLIC);
        }

        if (finalKey) {
            modifiersSet = modifiersSet.with(Modifier.FINAL);
        }

        return Field.builder()
                .name(parameter.getName())
                .owner(scope.getClassType())
                .type(parameter.getType())
                .getterFunction(field -> PropertyAccessorsUtil.generateGetter(field, scope))
                .setterFunction(field -> PropertyAccessorsUtil.generateSetter(field, scope))
                .modifiers(modifiersSet)
                .initialExpression(field -> scope -> {
                    LocalVariable localVariable = scope.getLocalVariable(parameter.getName());
                    LocalVariableReference localVariableReference = new LocalVariableReference(localVariable);
                    LocalVariableReference owner = new LocalVariableReference(scope.getLocalVariable("this"));
                    return new FieldAssignment(owner, field, localVariableReference);
                }).build();
    }
}
