package com.kubadziworski.util;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.ValueHolder;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.node.expression.arthimetic.Addition;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.NullType;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.exception.CompilationException;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.parsing.visitor.statement.RadiumTokenFactory;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.kubadziworski.antlr.EnkelParser.*;

public class ValueCtxResolver {

    private static final Type BOOLEAN_TYPE = PrimitiveTypes.BOOLEAN_TYPE;
    private static final Type INT_TYPE = PrimitiveTypes.INT_TYPE;
    private static final Type LONG_TYPE = PrimitiveTypes.LONG_TYPE;
    private static final Type DOUBLE_TYPE = PrimitiveTypes.DOUBLE_TYPE;
    private static final Type FLOAT_TYPE = PrimitiveTypes.FLOAT_TYPE;
    private static final Type CHAR_TYPE = PrimitiveTypes.CHAR_TYPE;


    public static Value getValueFromContext(EnkelParser.ValueContext value) {
        String stringValue = value.getText();
        if (StringUtils.isEmpty(stringValue)) return new Value(UnitType.CONCRETE_INSTANCE, null);
        if (stringValue.equals("null")) return new Value(NullType.INSTANCE, null);

        if (value.integerLiteral() != null) {
            return handleIntegerValue(value);

        } else if (value.floatingPointLiteral() != null) {
            return handleFloatValue(value);

        } else if (value.BOOL() != null) {
            return new Value(BOOLEAN_TYPE, Boolean.valueOf(stringValue));
        }
        if (value.CharacterLiteral() != null) {
            return handleCharacterValue(value);
        }
        throw new RuntimeException("Unrecognized type: " + stringValue);
    }

    private static Value handleCharacterValue(ValueContext ctx) {
        return new Value(CHAR_TYPE, ctx.getText().charAt(1));
    }

    private static Value handleFloatValue(ValueContext ctx) {
        ValueHolder holder = ctx.floatingPointLiteral().number;
        return new Value(getValType(holder.type), holder.value);
    }

    private static Value handleIntegerValue(EnkelParser.ValueContext ctx) {
        ValueHolder holder = ctx.integerLiteral().number;
        return new Value(getValType(holder.type), holder.value);
    }

    public static Expression handleStringValue(EnkelParser.ValueContext ctx, ExpressionVisitor expressionVisitor) {

        List<Expression> expressions = new ArrayList<>();
        for (int x = 0; x < ctx.stringLiteral().children.size(); x++) {
            ParseTree parseTree = ctx.stringLiteral().children.get(x);
            if (parseTree instanceof TerminalNode) {
                int symbol = ((TerminalNode) parseTree).getSymbol().getType();
                if (symbol == SINGLE_TEXT || symbol == MULTILINE_QUOTE_TEXT) {
                    expressions.add(new Value(DefaultTypes.STRING, parseTree.getText()));
                } else if (symbol == SINGLE_QUOTE_REF || symbol == MULTILINE_QUOTE_REF) {
                    Token token = RadiumTokenFactory.DEFAULT.create(SimpleName, parseTree.getText().replace("$", ""));
                    EnkelParser.VariableReferenceContext referenceContext = new EnkelParser.VariableReferenceContext(ctx, 0);
                    TerminalNodeImpl node = new TerminalNodeImpl(token);
                    referenceContext.addChild(node);
                    referenceContext.start = ctx.start;
                    referenceContext.stop = ctx.stop;
                    expressions.add(referenceContext.accept(expressionVisitor));
                }
            } else if (parseTree instanceof EnkelParser.ExpressionContext) {
                Expression expression = parseTree.accept(expressionVisitor);
                expressions.add(expression);
            }
        }
        //Case of empty String
        if (expressions.size() == 0) {
            return new Value(DefaultTypes.STRING, "");
        }
        return createStringAddition(expressions);
    }

    private static Type getValType(ValueHolder.ValueType type) {
        switch (type) {
            case BOOLEAN:
                return BOOLEAN_TYPE;
            case CHAR:
                return CHAR_TYPE;
            case DOUBLE:
                return DOUBLE_TYPE;
            case INT:
                return INT_TYPE;
            case FLOAT:
                return FLOAT_TYPE;
            case LONG:
                return LONG_TYPE;
            case STRING:
                return DefaultTypes.STRING;
            default:
                throw new CompilationException("Type not eligible as value");
        }
    }

    private static Expression createStringAddition(List<Expression> expressions) {
        if (expressions.size() == 1) {
            return expressions.get(0);
        }

        List<Expression> subList = new ArrayList<>(expressions.subList(1, expressions.size()));
        Expression addition = expressions.get(0);
        for (Expression expression : subList) {
            Expression tempAddition = addition;
            addition = new Addition(tempAddition, expression);
        }
        return addition;
    }
}
