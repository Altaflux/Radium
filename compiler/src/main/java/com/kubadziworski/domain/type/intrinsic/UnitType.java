package com.kubadziworski.domain.type.intrinsic;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.EmptyExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;

public class UnitType extends JavaClassType {

    public static final UnitType INSTANCE = new UnitType();
    private static final Expression expression =
            new FieldReference(new Field("INSTANCE", INSTANCE, INSTANCE, Modifier.PUBLIC + Modifier.STATIC),
                    new EmptyExpression(INSTANCE));

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
    public int getReturnOpcode() {
        return Opcodes.RETURN;
    }


    @Override
    public String toString() {
        return "UnitType{} " + super.toString();
    }

    private static class UnitExpression implements Expression {

        private final Expression expression;

        public UnitExpression(Expression expression) {
            this.expression = expression;
        }

        @Override
        public Type getType() {
            return UnitType.INSTANCE;
        }

        @Override
        public void accept(StatementGenerator generator) {
            expression.accept(generator);
            UnitType.expression().accept(generator);
        }
    }
}
