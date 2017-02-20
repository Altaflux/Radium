package com.kubadziworski.parsing.visitor.phase;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.FieldVisitor;

import java.util.List;
import java.util.stream.Collectors;

class FieldPhaseVisitor extends EnkelParserBaseVisitor<Scope> {

    private final Scope scope;

    FieldPhaseVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope visitClassDeclaration(EnkelParser.ClassDeclarationContext ctx) {
        FieldVisitor fieldVisitor = new FieldVisitor(scope);
        List<Field> fields = ctx.classBody().field().stream()
                .map(field -> field.accept(fieldVisitor))
                .collect(Collectors.toList());

        fields.forEach(scope::addField);
        return scope;
    }
}
