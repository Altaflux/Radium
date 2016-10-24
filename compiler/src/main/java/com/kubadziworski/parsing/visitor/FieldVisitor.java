package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.WrongModifiersException;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by kuba on 13.05.16.
 */
public class FieldVisitor extends EnkelBaseVisitor<Field> {

    private final Scope scope;

    public FieldVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Field visitField(@NotNull EnkelParser.FieldContext ctx) {
        Type owner = scope.getClassType();
        Type type = TypeResolver.getFromTypeContext(ctx.type(), scope);
        String name = ctx.name().getText();


        int modifiers = Modifier.PUBLIC;
        if(ctx.fieldModifier() != null && ctx.fieldModifier().size() > 1){
            throw new WrongModifiersException("Too many modifiers: " + ctx.fieldModifier());
        }
        if (ctx.fieldModifier() != null) {
            modifiers = ctx.fieldModifier().stream().map(methodModifiersContext -> {
                if (methodModifiersContext.getText().equals("private")) {
                    return Modifier.PRIVATE;
                }
                if (methodModifiersContext.getText().equals("protected")) {
                    return Modifier.PROTECTED;
                }
                if (methodModifiersContext.getText().equals("public")) {
                    return Modifier.PUBLIC;
                }
                return 0;
            }).findAny().orElse(Modifier.PUBLIC);
        }
        return new Field(name, owner, type, modifiers);
    }
}
