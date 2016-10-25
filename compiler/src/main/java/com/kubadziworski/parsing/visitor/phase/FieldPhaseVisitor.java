package com.kubadziworski.parsing.visitor.phase;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.FieldVisitor;
import com.kubadziworski.util.ReflectionUtils;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.stream.Collectors;

class FieldPhaseVisitor extends EnkelBaseVisitor<Scope> {

    private final Scope scope;

    FieldPhaseVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope visitClassDeclaration(@NotNull EnkelParser.ClassDeclarationContext ctx) {
        FieldVisitor fieldVisitor = new FieldVisitor(scope);
        List<Field> fields = ctx.classBody().field().stream()
                .map(field -> field.accept(fieldVisitor))
                .collect(Collectors.toList());

        fields.forEach(scope::addField);
        fields.stream().map(ReflectionUtils::createGetterForField).forEach(scope::addSignature);
        fields.stream().map(ReflectionUtils::createSetterForField).forEach(scope::addSignature);
        return scope;
    }
}
