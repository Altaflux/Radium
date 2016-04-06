package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.domain.type.ClassType;
import com.kubadziworski.antlr.domain.type.BultInType;
import com.kubadziworski.antlr.domain.type.Type;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by kuba on 02.04.16.
 */
public class TypeVisitor extends EnkelBaseVisitor<Type> {

    @Override
    public Type visitValue(@NotNull EnkelParser.ValueContext ctx) {
        if(ctx == null) return BultInType.VOID;
        String stringType = ctx.getText();
        if(StringUtils.isNumeric(stringType)) {
            return BultInType.INT;
        }
        return new ClassType(stringType);
    }

    @Override
    public Type visitPrimitiveType(@NotNull EnkelParser.PrimitiveTypeContext ctx) {
        return BultInType.fromString(ctx.getText());
    }

    @Override
    public Type visitClassType(@NotNull EnkelParser.ClassTypeContext ctx) {
        return new ClassType(ctx.getText());
    }
}
