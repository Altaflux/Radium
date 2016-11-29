package com.kubadziworski.domain.type.intrinsic;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.expression.EmptyExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;

import java.lang.reflect.Modifier;

public class UnitType extends JavaClassType {

    public static final UnitType CONCRETE_INSTANCE = new UnitType();

    private static final Expression expression =
            new FieldReference(new Field("INSTANCE", CONCRETE_INSTANCE, CONCRETE_INSTANCE, Modifier.PUBLIC + Modifier.STATIC),
                    new EmptyExpression(CONCRETE_INSTANCE));

    private UnitType() {
        super("radium.Unit");
    }

    public static Expression expression() {
        return expression;
    }

    public static Expression chainExpression(Expression expression) {
        return new UnitExpression(expression);
    }

    @Override
    public String getDescriptor() {
        return super.getDescriptor();
    }

    @Override
    public org.objectweb.asm.Type getAsmType() {
        return super.getAsmType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof TypeProjection) {
            o = ((TypeProjection) o).getInternalType();
        }

        return o instanceof Type && getName().equals(((Type) o).getName());
    }

    @Override
    public String toString() {
        return super.toString();
    }


    private static class UnitExpression extends ElementImpl implements Expression {

        private final Expression expression;

        public UnitExpression(Expression expression) {
            this.expression = expression;
        }

        @Override
        public Type getType() {
            return UnitType.CONCRETE_INSTANCE;
        }

        @Override
        public void accept(StatementGenerator generator) {
            expression.accept(generator);
            UnitType.expression().accept(generator);
        }
    }
}
