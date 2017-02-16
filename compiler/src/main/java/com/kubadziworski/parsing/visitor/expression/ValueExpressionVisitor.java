package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.ValueContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.node.expression.arthimetic.Addition;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.List;

import static com.kubadziworski.antlr.EnkelParser.SINGLE_QUOTE_REF;
import static com.kubadziworski.antlr.EnkelParser.SINGLE_TEXT;
import static com.kubadziworski.antlr.EnkelParser.SimpleName;

public class ValueExpressionVisitor extends EnkelParserBaseVisitor<Expression> {

    private final ExpressionVisitor expressionVisitor;

    public ValueExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }


    @Override
    public Expression visitValue(@NotNull ValueContext ctx) {

        if (ctx.stringLiteral() != null) {
            List<Expression> expressions = new ArrayList<>();
            for (int x = 0; x < ctx.stringLiteral().children.size(); x++) {
                ParseTree parseTree = ctx.stringLiteral().children.get(x);
                if (parseTree instanceof TerminalNode) {
                    if (((TerminalNode) parseTree).getSymbol().getType() == SINGLE_TEXT) {
                        expressions.add(new Value(DefaultTypes.STRING, parseTree.getText()));
                    } else if (((TerminalNode) parseTree).getSymbol().getType() == SINGLE_QUOTE_REF) {
                        Token token = CommonTokenFactory.DEFAULT.create(SimpleName, parseTree.getText().replace("$", ""));
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
            if (expressions.size() == 1) {
                return expressions.get(0);
            }
            return createStringAddition(expressions);
        }
        String value = ctx.getText();
        Type type = TypeResolver.getFromValue(ctx);
        return new Value(new RuleContextElementImpl(ctx), type, value);
    }

    private Expression createStringAddition(List<Expression> expressions) {
        Expression addition = new Value(DefaultTypes.STRING, "");
        for (Expression expression : expressions) {
            Expression tempAddition = addition;
            addition = new Addition(tempAddition, expression);
        }

        return addition;
    }
}