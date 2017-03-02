package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.BooleanExpression;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.Label;
import org.objectweb.asm.commons.InstructionAdapter;

public class BooleanExpressionGenerator {

    private final InstructionAdapter adapter;

    public BooleanExpressionGenerator(InstructionAdapter adapter) {
        this.adapter = adapter;
    }

    public void generate(BooleanExpression expression, StatementGenerator generator) {
        if (expression.isAnd()) {
            generateAndExpression(expression, generator);
        } else {
            generateOrExpression(expression, generator);
        }
    }

    private void generateAndExpression(BooleanExpression expression, StatementGenerator generator) {
        Label lastLabel = new Label();
        Label midLabel = new Label();

        expression.getLeftExpression().accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.BOOLEAN_TYPE, expression.getLeftExpression().getType(), adapter);
        adapter.ifeq(midLabel);

        expression.getRightExpression().accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.BOOLEAN_TYPE, expression.getRightExpression().getType(), adapter);
        adapter.ifeq(midLabel);
        adapter.iconst(1);
        adapter.goTo(lastLabel);

        adapter.visitLabel(midLabel);
        adapter.iconst(0);

        adapter.visitLabel(lastLabel);
    }

    private void generateOrExpression(BooleanExpression expression, StatementGenerator generator) {
        Label lastLabel = new Label();
        Label midLabel = new Label();
        Label firstLabel = new Label();

        expression.getLeftExpression().accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.BOOLEAN_TYPE, expression.getLeftExpression().getType(), adapter);
        adapter.ifne(firstLabel);
        expression.getRightExpression().accept(generator);
        PrimitiveTypesWrapperFactory.coerce(PrimitiveTypes.BOOLEAN_TYPE, expression.getRightExpression().getType(), adapter);
        adapter.ifeq(midLabel);

        adapter.visitLabel(firstLabel);
        adapter.iconst(1);
        adapter.goTo(lastLabel);

        adapter.visitLabel(midLabel);
        adapter.iconst(0);

        adapter.visitLabel(lastLabel);
    }
}
