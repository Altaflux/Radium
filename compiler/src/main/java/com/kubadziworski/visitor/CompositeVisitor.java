package com.kubadziworski.visitor;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.exception.NoVisitorReturnedValueException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Arrays;

/**
 * Created by kuba on 03.04.16.
 */
public class CompositeVisitor<T> {

    private EnkelBaseVisitor<? extends T>[] visitors;

    public CompositeVisitor(EnkelBaseVisitor<? extends T> ... visitors) {
        this.visitors = visitors;
    }

    public T accept(ParserRuleContext context) {
        return Arrays.stream(visitors)
                .map(context::accept)
                .filter(t -> t!=null)
                .findFirst()
                .orElseThrow(() -> new NoVisitorReturnedValueException());
    }
}
