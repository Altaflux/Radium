package com.kubadziworski.parsing.visitor.phase;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.FieldVisitor;
import org.antlr.v4.runtime.misc.NotNull;

class FieldPhaseVisitor extends EnkelBaseVisitor<Scope> {

    private final Scope scope;

    FieldPhaseVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope visitClassDeclaration(@NotNull EnkelParser.ClassDeclarationContext ctx) {
        FieldVisitor fieldVisitor = new FieldVisitor(scope);
        ctx.classBody().field().stream()
                .map(field -> field.accept(fieldVisitor))
                .forEach(scope::addField);
        return scope;
    }
}
